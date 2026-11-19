// 第14章コラム「文字化けとの長い戦い」：BOM付きのUTF-8と、0x8160（〜）の変換先の違い
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EncodingPitfalls {
    public static void main(String[] args) throws IOException {
        // BOM（EF BB BF）付きのUTF-8のファイル
        Path file = Files.createTempFile("bom", ".csv");
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = "id,name\n1,田中\n".getBytes(StandardCharsets.UTF_8);
        byte[] all = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, all, 0, bom.length);
        System.arraycopy(body, 0, all, bom.length, body.length);
        Files.write(file, all);
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        String header = lines.get(0);
        IO.println("1行目: " + header);
        IO.println("1行目の長さ: " + header.length());
        IO.println("idで始まるか: " + header.startsWith("id"));
        IO.println("先頭の文字: U+%04X".formatted((int) header.charAt(0)));
        Files.delete(file);

        // 「〜」（Shift_JISの0x8160）をどの文字に変換するか
        byte[] wave = {(byte) 0x81, (byte) 0x60};
        String sjis = new String(wave, Charset.forName("Shift_JIS"));
        String ms932 = new String(wave, Charset.forName("windows-31j"));
        IO.println("Shift_JIS: U+%04X".formatted((int) sjis.charAt(0)));
        IO.println("windows-31j: U+%04X".formatted((int) ms932.charAt(0)));
        IO.println("同じ文字か: " + sjis.equals(ms932));
    }
}
