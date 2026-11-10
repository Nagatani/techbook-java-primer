public class WordDocument {
    private String filename;     // ①
    private String content;      // ①
    private long fileSize;       // ①
    
    public void open() {         // ②
        IO.println(filename + " を開いています");
    }
    
    public void save() {         // ③
        fileSize = content.length();
        IO.println(filename + " を保存しました: " + fileSize + "バイト");
    }
    
    public void close() {        // ④
        content = null;
        IO.println(filename + " を閉じました");
    }
}
