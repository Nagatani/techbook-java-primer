// リファクタリング後：重複が除去された
public class WordDocument extends Document {
    public WordDocument(String filename, String content) {
        super(filename, content);
    }
    
    // WordDocument固有の機能があれば追加
    public void applyTemplate(String templateName) {
        System.out.println(filename + " にテンプレート " + templateName + " を適用");
    }
}
