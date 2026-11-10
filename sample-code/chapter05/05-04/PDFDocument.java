public class PDFDocument {
    private String filename;     // ①と重複
    private String content;      // ①と重複
    private long fileSize;       // ①と重複
    private int pageCount;       // ⑤
    
    public void open() {         // ②と完全重複
        IO.println(filename + " を開いています");
    }
    
    public void save() {         // ③と完全重複
        fileSize = content.length();
        IO.println(filename + " を保存しました: " + fileSize + "バイト");
    }
    
    public void close() {        // ④と完全重複
        content = null;
        IO.println(filename + " を閉じました");
    }
    
    public void generatePDF() {  // ⑥
        IO.println("PDF形式で " + pageCount + " ページを生成");
    }
}
