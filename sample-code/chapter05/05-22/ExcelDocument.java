public class ExcelDocument extends Document {
    public ExcelDocument(String filename, String content) {
        super(filename, content);  // ①
    }
    
    @Override  // ②
    public void save() {
        fileSize = content != null ? content.length() * 2 : 0;  // ③
        IO.println(filename + " をExcel形式で保存: " + fileSize + "バイト（メタデータ含む）");
    }
    
    public void createChart() {  // ④
        IO.println(filename + " にグラフを作成");
    }
}
