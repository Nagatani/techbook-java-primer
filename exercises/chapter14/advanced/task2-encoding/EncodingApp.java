import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Shift_JIS系（Windows-31J）の名簿をUTF-8に変換するmainメソッド */
public class EncodingApp {
    private static final Charset WINDOWS_31J = Charset.forName("Windows-31J");

    public static void main(String[] args) throws IOException {
        Path dir = Path.of("encoding-work");
        Files.createDirectories(dir);
        Path sjisFile = dir.resolve("meibo-sjis.csv");
        Path utf8File = dir.resolve("meibo-utf8.csv");

        // 古いシステムが書き出した名簿を想定し、Windows-31Jのファイルを用意する
        String meibo = "学籍番号,氏名,学科\n"
                + "S001,山田太郎,情報工学科\n"
                + "S002,佐藤花子,電気電子工学科\n"
                + "S003,鈴木一郎,機械工学科\n";
        Files.writeString(sjisFile, meibo, WINDOWS_31J);

        IO.println("=== UTF-8として読んでみる ===");
        try {
            List<String> lines = Files.readAllLines(sjisFile, StandardCharsets.UTF_8);
            IO.println(lines.size() + "行読めました");
        } catch (IOException e) {
            IO.println("読めません: " + e.getClass().getSimpleName() + "（" + e.getMessage() + "）");
        }
        // バイト列からStringを作る方法では例外にならず、読めない部分が置換文字になる
        String garbled = new String(Files.readAllBytes(sjisFile), StandardCharsets.UTF_8);
        long replaced = garbled.chars().filter(c -> c == '�').count();
        IO.println("new Stringで変換すると、置換文字（U+FFFD）が" + replaced + "個できます");

        IO.println();
        IO.println("=== エンコーディングを調べて変換する ===");
        for (Path file : List.of(sjisFile, utf8File)) {
            if (Files.notExists(file)) {
                continue;
            }
            if (EncodingConverter.isUtf8(file)) {
                IO.println(file.getFileName() + ": すでにUTF-8なので変換しません");
            } else {
                int count = EncodingConverter.convertToUtf8(file, WINDOWS_31J, utf8File);
                IO.println(file.getFileName() + ": Windows-31Jとして読み、"
                        + utf8File.getFileName() + " に" + count + "行書き出しました");
            }
        }

        IO.println();
        IO.println("=== ファイルの大きさ ===");
        IO.println(sjisFile.getFileName() + ": " + Files.size(sjisFile) + "バイト（漢字1文字が2バイト）");
        IO.println(utf8File.getFileName() + ": " + Files.size(utf8File) + "バイト（漢字1文字が3バイト）");

        IO.println();
        IO.println("=== 変換後の内容 ===");
        List<String> converted = Files.readAllLines(utf8File, StandardCharsets.UTF_8);
        for (String line : converted) {
            IO.println(line);
        }
        IO.println("元の内容と同じ: " + String.join("\n", converted).equals(meibo.strip()));
    }
}
