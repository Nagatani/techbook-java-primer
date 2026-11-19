import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.module.ModuleDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * scripts/check-api-names.js から呼ぶ補助。標準入力の1行に1つの参照を受け取り、JDK 25のAPIにあるかを調べる。
 *
 *   入力:  種類<TAB>参照       種類は member（Class.member）・qualified（java.util.List など）・type（クラス名だけ）
 *   出力:  結果<TAB>参照<TAB>補足  結果は ok・deprecated・removal・missing・unknown（JDKにない型。自作のクラスなど）
 *
 * JDKのクラスは、公開されたパッケージ（無条件のexports）の public なものだけを対象にする。
 * クラスは名前の一覧（jrt:/）から引き、参照されたものだけを読み込む（初期化はしない）。
 */
public class ApiNames {
    public static void main(String[] args) throws Exception {
        Map<String, List<String>> bySimple = new HashMap<>();
        Set<String> packages = new HashSet<>();
        Set<String> exported = new HashSet<>();
        for (Module m : ModuleLayer.boot().modules()) {
            for (ModuleDescriptor.Exports e : m.getDescriptor().exports()) {
                if (!e.isQualified()) exported.add(e.source());
            }
        }
        FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
        try (Stream<Path> paths = Files.walk(jrt.getPath("/modules"))) {
            paths.filter(p -> p.toString().endsWith(".class")).forEach(p -> {
                Path rel = p.subpath(2, p.getNameCount());
                String name = rel.toString().replace('/', '.');
                name = name.substring(0, name.length() - ".class".length());
                int dot = name.lastIndexOf('.');
                if (dot < 0) return;
                String pkg = name.substring(0, dot);
                if (!exported.contains(pkg)) return;
                packages.add(pkg);
                String simple = name.substring(dot + 1).replace('$', '.');
                if (simple.matches(".*\\.\\d.*")) return;   // 匿名クラス
                bySimple.computeIfAbsent(simple, k -> new ArrayList<>()).add(name);
            });
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.split("\t", 2);
            if (parts.length < 2) continue;
            String ref = parts[1];
            System.out.println(String.join("\t", check(parts[0], ref, bySimple, packages)));
        }
    }

    static String[] check(String kind, String ref, Map<String, List<String>> bySimple, Set<String> packages) {
        if (kind.equals("qualified")) {
            if (packages.contains(ref)) return new String[] {"ok", ref, "package"};
            if (ModuleLayer.boot().findModule(ref).isPresent()) return new String[] {"ok", ref, "module"};
            Class<?> c = load(ref);
            if (c != null) return withDeprecation(c.isAnnotationPresent(Deprecated.class) ? c.getAnnotation(Deprecated.class) : null, ref, "class");
            // java.util.Map.Entry のような入れ子、java.lang.String.valueOf のような修飾されたメンバー
            int dot = ref.lastIndexOf('.');
            Class<?> owner = load(ref.substring(0, dot));
            if (owner == null) owner = load(nested(ref.substring(0, dot)));
            if (owner != null) return member(owner, ref.substring(dot + 1), ref);
            Class<?> n = load(nested(ref));
            if (n != null) return new String[] {"ok", ref, "class"};
            return new String[] {"missing", ref, "JDK 25に、このパッケージ・クラスがない"};
        }
        if (kind.equals("type")) {
            // クラス名だけの参照。JDKにあれば非推奨かどうかを見る（JDKにない名前は自作のクラスとみなす）
            List<String> cs = bySimple.get(ref);
            if (cs == null) return new String[] {"unknown", ref, ""};
            Deprecated d = null;
            for (String fqn : cs) {
                Class<?> c = load(fqn);
                if (c == null) c = load(nested(fqn));
                if (c == null || !Modifier.isPublic(c.getModifiers())) continue;
                Deprecated x = c.getAnnotation(Deprecated.class);
                if (x == null) return new String[] {"ok", ref, c.getName()};
                d = x;
            }
            return d == null ? new String[] {"unknown", ref, ""} : withDeprecation(d, ref, String.join("・", cs));
        }
        // Class.member（Class は Map.Entry のように入れ子を含みうる）
        int dot = ref.lastIndexOf('.');
        String cls = ref.substring(0, dot);
        String mem = ref.substring(dot + 1);
        List<String> candidates = bySimple.get(cls);
        if (candidates == null) return new String[] {"unknown", ref, ""};
        String[] best = null;
        for (String fqn : candidates) {
            Class<?> c = load(fqn);
            if (c == null) c = load(nested(fqn));
            if (c == null || !Modifier.isPublic(c.getModifiers())) continue;
            String[] r = member(c, mem, ref);
            if (r[0].equals("ok")) return r;
            if (best == null || !r[0].equals("missing")) best = r;
        }
        return best != null ? best : new String[] {"unknown", ref, ""};
    }

    static String[] member(Class<?> c, String mem, String ref) {
        // ArrayList::new（コンストラクター参照）、String.class、配列の length
        if (mem.equals("class") || mem.equals("length") || mem.equals("new")) return new String[] {"ok", ref, ""};
        // オーバーロードのうち1つでも非推奨でないものがあれば、非推奨としない
        List<java.lang.reflect.AnnotatedElement> hits = new ArrayList<>();
        for (Method m : c.getMethods()) if (m.getName().equals(mem)) hits.add(m);
        for (Field f : c.getFields()) if (f.getName().equals(mem)) hits.add(f);
        // インターフェイスのstaticメソッドと protected のメソッド（Object.finalize など）は getMethods に出ないので、宣言されたものも見る
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals(mem) && (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers()))) hits.add(m);
        }
        boolean nestedClass = false;
        for (Class<?> n : c.getClasses()) if (n.getSimpleName().equals(mem)) nestedClass = true;
        if (hits.isEmpty() && !nestedClass) return new String[] {"missing", ref, c.getName() + " に " + mem + " がない"};
        if (nestedClass || hits.stream().anyMatch(h -> !h.isAnnotationPresent(Deprecated.class))) return new String[] {"ok", ref, c.getName()};
        return withDeprecation(hits.get(0).getAnnotation(Deprecated.class), ref, c.getName());
    }

    static String[] withDeprecation(Deprecated d, String ref, String where) {
        if (d == null) return new String[] {"ok", ref, where};
        return new String[] {d.forRemoval() ? "removal" : "deprecated", ref, where + (d.since().isEmpty() ? "" : "（since " + d.since() + "）")};
    }

    static String nested(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot) + "$" + name.substring(dot + 1);
    }

    static Class<?> load(String name) {
        try {
            return Class.forName(name, false, ClassLoader.getPlatformClassLoader());
        } catch (Throwable e) {
            return null;
        }
    }
}
