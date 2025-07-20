/**
 * リスト5-6
 * ExcelDocumentクラス（継承を使った再実装）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (500行目)
 */
public class ExcelDocument extends Document {
    public ExcelDocument(String filename, String content) {
        super(filename, content);  // ①
    }
    
    @Override  // ②
    public void save() {
        fileSize = content != null ? content.length() * 2 : 0;  // ③
        System.out.println(filename + " をExcel形式で保存: " + fileSize + "バイト（メタデータ含む）");
    }
    
    public void createChart() {  // ④
        System.out.println(filename + " にグラフを作成");
    }
}