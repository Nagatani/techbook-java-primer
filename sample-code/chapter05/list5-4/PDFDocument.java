/**
 * リスト5-4
 * PDFDocumentクラス（重複コードの例）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (358行目)
 */
public class PDFDocument {
    private String filename;     // ①と重複
    private String content;      // ①と重複
    private long fileSize;       // ①と重複
    private int pageCount;       // ⑤
    
    public void open() {         // ②と完全重複
        System.out.println(filename + " を開いています");
    }
    
    public void save() {         // ③と完全重複
        fileSize = content.length();
        System.out.println(filename + " を保存しました: " + fileSize + "バイト");
    }
    
    public void close() {        // ④と完全重複
        content = null;
        System.out.println(filename + " を閉じました");
    }
    
    public void generatePDF() {  // ⑥
        System.out.println("PDF形式で " + pageCount + " ページを生成");
    }
}