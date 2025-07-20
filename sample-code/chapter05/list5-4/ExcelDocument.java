/**
 * リスト5-4
 * ExcelDocumentクラス（重複コードの例）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (399行目)
 */
public class ExcelDocument {
    private String filename;
    private String content;
    private long fileSize;
    
    // また同じメソッドの重複！
    public void open() {
        System.out.println(filename + " を開いています");
    }
    
    public void save() {
        fileSize = content.length();
        System.out.println(filename + " を保存しました: " + fileSize + "バイト");
    }
    
    public void close() {
        content = null;
        System.out.println(filename + " を閉じました");
    }
}