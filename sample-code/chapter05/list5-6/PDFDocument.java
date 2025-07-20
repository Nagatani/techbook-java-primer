/**
 * リスト5-6
 * PDFDocumentクラス（継承を使った再実装）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (476行目)
 */
public class PDFDocument extends Document {
    private int pageCount;
    
    public PDFDocument(String filename, String content, int pageCount) {
        super(filename, content);
        this.pageCount = pageCount;
    }
    
    // saveメソッドをオーバーライド（PDFは圧縮効率が高い）
    @Override
    public void save() {
        fileSize = (content != null ? content.length() : 0) / 2; // PDFは圧縮される
        System.out.println(filename + " をPDF形式で保存: " + fileSize + "バイト");
    }
    
    public void generatePDF() {
        if (pageCount > 0) {
            System.out.println("PDF形式で " + pageCount + " ページを生成");
        } else {
            System.out.println("ページ数が指定されていません");
        }
    }
}