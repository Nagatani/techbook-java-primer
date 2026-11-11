public class DocumentTest {
    public static void main(String[] args) {
        // WordDocumentの操作
        WordDocument word = new WordDocument("report.docx", "これはWordドキュメントの内容です");
        word.open();
        word.save();
        word.close();
        
        IO.println();
        
        // PDFDocumentの操作  
        PDFDocument pdf = new PDFDocument("manual.pdf", "これはPDFドキュメントの内容です", 10);
        pdf.open();
        pdf.save();
        pdf.generatePDF();
        pdf.close();
    }
}
