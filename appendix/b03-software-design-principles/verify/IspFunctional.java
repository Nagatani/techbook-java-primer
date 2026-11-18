import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * インターフェイス分離の原則：java.util.functionのインターフェイスは、
 * どれも抽象メソッドを1つだけ持つ小さなインターフェイスである。
 * JDKのクラスの一覧は、jrtファイルシステム（実行中のJDKのモジュール）から読む。
 * 実行: javac IspFunctional.java && java IspFunctional
 */
public class IspFunctional {
    public static void main(String[] args) throws Exception {
        FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
        List<String> names;
        try (Stream<java.nio.file.Path> files =
                Files.list(jrt.getPath("/modules/java.base/java/util/function"))) {
            names = files.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".class") && !n.contains("$"))
                    .map(n -> n.substring(0, n.length() - ".class".length()))
                    .sorted()
                    .toList();
        }
        int single = 0;
        int annotated = 0;
        for (String name : names) {
            Class<?> c = Class.forName("java.util.function." + name);
            Method[] methods = c.getMethods();
            long abstracts = Arrays.stream(methods)
                    .filter(m -> Modifier.isAbstract(m.getModifiers())).count();
            if (c.isInterface() && abstracts == 1) {
                single++;
            }
            if (c.isAnnotationPresent(FunctionalInterface.class)) {
                annotated++;
            }
        }
        IO.println("java.util.functionのインターフェイス: " + names.size());
        IO.println("抽象メソッドが1つだけのもの: " + single);
        IO.println("@FunctionalInterfaceが付いたもの: " + annotated);

        long listAbstracts = Arrays.stream(List.class.getMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers())).count();
        IO.println("比べると、Listの抽象メソッド: " + listAbstracts);
    }
}
