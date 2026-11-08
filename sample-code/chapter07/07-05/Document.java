// Document.java
// ExportableとArchivableの両方のインターフェイスを実装する
public class Document implements Exportable, Archivable {
    private String title;
    private String content;

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Exportableインターフェイスのメソッドの実装
    @Override
    public void exportToFormat(String format) {
        System.out.println(this.title + "を" + format + "形式でエクスポートします。");
    }

    // Archivableインターフェイスのメソッドの実装
    @Override
    public void archiveToStorage(String location) {
        System.out.println(this.title + "を" + location + "にアーカイブします。");
    }
}
