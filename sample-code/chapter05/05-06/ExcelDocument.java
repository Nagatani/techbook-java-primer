public class ExcelDocument {
    private String filename;
    private String content;
    private long fileSize;
    
    // また同じメソッドの重複！
    public void open() {
        IO.println(filename + " を開いています");
    }
    
    public void save() {
        fileSize = content.length();
        IO.println(filename + " を保存しました: " + fileSize + "バイト");
    }
    
    public void close() {
        content = null;
        IO.println(filename + " を閉じました");
    }
}
