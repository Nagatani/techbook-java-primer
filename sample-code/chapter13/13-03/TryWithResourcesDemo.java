import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// 本文では省略しているmainメソッド（readFileを試す）
public class TryWithResourcesDemo {
    public static void main(String[] args) throws IOException {
        // 試しに読み込むファイルを一時ファイルとして作る
        Path file = Files.createTempFile("sample", ".txt");
        Files.writeString(file, "これはテストファイルです。\n");

        IO.println("存在するファイルを読み込み:");
        TryWithResourcesSample.readFile(file.toString());
        Files.delete(file);

        IO.println();
        IO.println("存在しないファイルを読み込み:");
        TryWithResourcesSample.readFile("non_existent.txt");
    }
}
