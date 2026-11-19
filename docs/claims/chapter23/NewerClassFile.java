// 第23章コラム「「自分のPCでは動くのに」をなくす工夫」：新しい版のクラスファイルを古いJVMで動かしたときのエラー
import java.io.IOException;
import java.lang.reflect.ClassFileFormatVersion;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.tools.ToolProvider;

public class NewerClassFile {
    public static void main(String[] args) throws IOException, InterruptedException {
        IO.println("Java 21のクラスファイルの版: " + ClassFileFormatVersion.RELEASE_21.major());
        IO.println("Java 25のクラスファイルの版: " + ClassFileFormatVersion.RELEASE_25.major());

        // Java 25のJVMは版69までしか読めないので、版を70に書き換えた「未来の版のクラスファイル」で古いJVMの立場を再現する
        Path dir = Files.createTempDirectory("newer");
        Path src = dir.resolve("Hello.java");
        Files.writeString(src, "public class Hello { public static void main(String[] a) { } }");
        ToolProvider.getSystemJavaCompiler().run(null, null, null, "-d", dir.toString(), src.toString());
        Path cls = dir.resolve("Hello.class");
        byte[] b = Files.readAllBytes(cls);
        b[7] = (byte) (ClassFileFormatVersion.latest().major() + 1);   // 6〜7バイト目がメジャーバージョン
        Files.write(cls, b);

        Path java = Path.of(System.getProperty("java.home"), "bin", "java");
        Process p = new ProcessBuilder(List.of(java.toString(), "-Duser.language=en", "-cp", dir.toString(), "Hello"))
                .redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes());
        p.waitFor();
        out.lines().filter(l -> l.contains("UnsupportedClassVersionError")).forEach(IO::println);
        try (var files = Files.list(dir)) {
            for (Path f : files.toList()) {
                Files.delete(f);
            }
        }
        Files.delete(dir);
    }
}
