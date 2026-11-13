import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileDeleteStrategy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommonsIOExample {
    public static void main(String[] args) throws IOException {
        // 1. FileUtils - ファイル操作の簡略化
        File file = new File("example.txt");
        
        // ファイルへの書き込み（1行で完了）
        FileUtils.writeStringToFile(file, "Hello, Commons IO!\n", 
            StandardCharsets.UTF_8);
        
        // ファイルへの追記
        FileUtils.writeStringToFile(file, "追加のテキスト\n", 
            StandardCharsets.UTF_8, true);
        
        // ファイルの読み込み（1行で完了）
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        IO.println("File content:\n" + content);
        
        // 行単位での読み込み
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        IO.println("Lines count: " + lines.size());
        
        // 2. ファイルのコピー
        File copyFile = new File("example_copy.txt");
        FileUtils.copyFile(file, copyFile);
        
        // 3. ディレクトリ操作
        File dir = new File("temp_dir");
        FileUtils.forceMkdir(dir);  // 親ディレクトリも含めて作成
        
        // ファイルをディレクトリにコピーし、ディレクトリのサイズを取得
        FileUtils.copyFileToDirectory(file, dir);
        long dirSize = FileUtils.sizeOfDirectory(dir);
        IO.println("temp_dir size: " + 
            FileUtils.byteCountToDisplaySize(dirSize));
        
        // 4. FilenameUtils - パス操作
        String filePath = "/home/user/documents/report.pdf";
        IO.println("\nFile path analysis:");
        IO.println("Base name: " + FilenameUtils.getBaseName(filePath));
        IO.println("Extension: " + FilenameUtils.getExtension(filePath));
        IO.println("Full path: " + FilenameUtils.getFullPath(filePath));
        
        // 5. IOUtils - ストリーム操作
        String text = "Stream processing example";
        try (InputStream is = IOUtils.toInputStream(text, StandardCharsets.UTF_8);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            
            IOUtils.copy(is, os);
            String result = os.toString(StandardCharsets.UTF_8);
            IO.println("\nStream copy result: " + result);
        }
        
        // クリーンアップ
        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(copyFile);
        FileUtils.deleteQuietly(dir);
    }
}
