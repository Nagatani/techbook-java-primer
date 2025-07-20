/**
 * リスト5-6
 * WordDocumentクラス（継承を使った再実装）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (465行目)
 */
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