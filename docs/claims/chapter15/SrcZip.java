// 第15章：JDKのソース（$JAVA_HOME/lib/src.zip）から、ConcurrentHashMapの作者と、書き込みの排他のしかたを読む
//   java SrcZip
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class SrcZip {
    static String read(ZipFile zip, String name) throws IOException {
        var entry = zip.getEntry(name);
        if (entry == null) {
            return "";
        }
        try (var in = zip.getInputStream(entry)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws IOException {
        Path src = Path.of(System.getProperty("java.home"), "lib", "src.zip");
        try (ZipFile zip = new ZipFile(src.toFile())) {
            String chm = read(zip, "java.base/java/util/concurrent/ConcurrentHashMap.java");
            chm.lines().filter(l -> l.contains("@author") || l.contains("@since"))
                    .map(String::trim).distinct().forEach(IO::println);
            // putValの中で、空のビンにはCASで入れ、そうでなければビンの先頭のノードをロックにする
            int putVal = chm.indexOf("final V putVal(");
            String body = chm.substring(putVal, chm.indexOf("addCount(", putVal));
            IO.println("putValで空のビンにCASで入れる: " + body.contains("casTabAt(tab, i, null"));
            IO.println("putValでビンの先頭のノードをロックにする: " + body.contains("synchronized (f)"));
            IO.println("getはロックを取らない: " + !methodBody(chm, "public V get(Object key)").contains("synchronized"));
            String pkg = read(zip, "java.base/java/util/concurrent/package-info.java");
            pkg.lines().filter(l -> l.contains("@since")).map(String::trim).forEach(l -> IO.println("package-info: " + l));
        }
    }

    static String methodBody(String src, String sig) {
        int start = src.indexOf(sig);
        int depth = 0;
        for (int i = src.indexOf('{', start); i < src.length(); i++) {
            char c = src.charAt(i);
            if (c == '{') depth++;
            if (c == '}' && --depth == 0) return src.substring(start, i + 1);
        }
        return "";
    }
}
