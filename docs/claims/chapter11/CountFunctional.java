// 第11章：java.util.functionパッケージのインターフェイスの数を数える
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CountFunctional {
    public static void main(String[] args) throws Exception {
        FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
        Path dir = jrt.getPath("/modules/java.base/java/util/function");
        long n;
        try (Stream<Path> s = Files.list(dir)) {
            n = s.map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(".class") && !name.contains("$"))
                    .map(name -> name.substring(0, name.length() - 6))
                    .filter(CountFunctional::isPublicInterface)
                    .count();
        }
        System.out.println("interfaces=" + n);
    }

    static boolean isPublicInterface(String simpleName) {
        try {
            Class<?> c = Class.forName("java.util.function." + simpleName);
            return c.isInterface() && java.lang.reflect.Modifier.isPublic(c.getModifiers());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
