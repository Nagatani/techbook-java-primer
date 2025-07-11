/**
 * リスト22-6
 * CommonsIOExampleクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (588行目)
 */

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
        System.out.println("File content:\n" + content);
        
        // 行単位での読み込み
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        System.out.println("Lines count: " + lines.size());
        
        // 2. ファイルのコピー
        File copyFile = new File("example_copy.txt");
        FileUtils.copyFile(file, copyFile);
        
        // 3. ディレクトリ操作
        File dir = new File("temp_dir");
        FileUtils.forceMkdir(dir);  // 親ディレクトリも含めて作成
        
        // ディレクトリのサイズ取得
        long dirSize = FileUtils.sizeOfDirectory(new File("."));
        System.out.println("Current directory size: " + 
            FileUtils.byteCountToDisplaySize(dirSize));
        
        // 4. FilenameUtils - パス操作
        String filePath = "/home/user/documents/report.pdf";
        System.out.println("\nFile path analysis:");
        System.out.println("Base name: " + FilenameUtils.getBaseName(filePath));
        System.out.println("Extension: " + FilenameUtils.getExtension(filePath));
        System.out.println("Full path: " + FilenameUtils.getFullPath(filePath));
        
        // 5. IOUtils - ストリーム操作
        String text = "Stream processing example";
        try (InputStream is = IOUtils.toInputStream(text, StandardCharsets.UTF_8);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            
            IOUtils.copy(is, os);
            String result = os.toString(StandardCharsets.UTF_8);
            System.out.println("\nStream copy result: " + result);
        }
        
        // クリーンアップ
        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(copyFile);
        FileUtils.deleteQuietly(dir);
    }
}