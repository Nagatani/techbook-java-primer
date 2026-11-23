// 第14章：文字コードの誤り、Dataストリーム、APPEND、Text Blocksのインデント
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HexFormat;

public class IoFacts {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("ch14");
        Path f = dir.resolve("hello.txt");
        Files.writeString(f, "こんにちは", StandardCharsets.UTF_8);
        try (var r = Files.newBufferedReader(f, Charset.forName("Shift_JIS"))) {
            System.out.println("read as Shift_JIS: " + r.readLine());
        } catch (MalformedInputException e) {
            System.out.println("newBufferedReader Shift_JIS -> MalformedInputException");
        }
        Files.writeString(f, "あいうえお", StandardCharsets.UTF_8);
        try (var r = Files.newBufferedReader(f, Charset.forName("Shift_JIS"))) {
            System.out.println("read as Shift_JIS: " + r.readLine());
        } catch (MalformedInputException e) {
            System.out.println("newBufferedReader Shift_JIS -> MalformedInputException");
        }
        System.out.println("new String as Shift_JIS has replacement or other chars: "
                + !new String(Files.readAllBytes(f), "Shift_JIS").equals("こんにちは"));

        Files.writeString(f, "a\n");
        Files.writeString(f, "b\n", StandardOpenOption.APPEND);
        System.out.println("append: " + Files.readAllLines(f));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(1);
        }
        System.out.println("writeInt(1): " + HexFormat.of().formatHex(bytes.toByteArray()));
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            System.out.println("readInt: " + in.readInt());
        }
        try (DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream())) {
            out.writeUTF("a".repeat(65535));
            System.out.println("writeUTF 65535 bytes: ok");
            out.writeUTF("a".repeat(65536));
        } catch (UTFDataFormatException e) {
            System.out.println("writeUTF 65536 bytes -> UTFDataFormatException");
        }

        String block = """
                A
                  B
            """;
        System.out.println("text block with closing delimiter left: [" + block.replace("\n", "|") + "]");
        String block2 = """
            line1 \
            line2""";
        System.out.println("backslash joins lines: [" + block2 + "]");
    }
}
