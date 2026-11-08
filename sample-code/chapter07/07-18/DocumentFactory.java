// 抽象ファクトリークラス
abstract class DocumentFactory {
    // Factory Method - サブクラスで実装
    public abstract Document createDocument();
    
    // テンプレートメソッド
    public Document newDocument() {
        Document doc = createDocument();
        doc.open();
        return doc;
    }
}

// 具体的なファクトリー
class WordDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new WordDocument();
    }
}

class PdfDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new PdfDocument();
    }
}

// ドキュメントインターフェイス
interface Document {
    void open();
    void save();
    void close();
}

// 具体的なドキュメント実装
class WordDocument implements Document {
    @Override
    public void open() {
        System.out.println("Word文書を開いています...");
    }
    
    @Override
    public void save() {
        System.out.println("Word文書を保存しています...");
    }
    
    @Override
    public void close() {
        System.out.println("Word文書を閉じています...");
    }
}

class PdfDocument implements Document {
    @Override
    public void open() {
        System.out.println("PDF文書を開いています...");
    }
    
    @Override
    public void save() {
        System.out.println("PDF文書を保存しています...");
    }
    
    @Override
    public void close() {
        System.out.println("PDF文書を閉じています...");
    }
}
