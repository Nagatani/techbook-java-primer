public class Book extends Product {  // ③
    private String author;     // ④
    private String isbn;       // ④
    
    public void displayBookInfo() {  // ⑤
        displayInfo();  // ②を呼び出し
        IO.println("著者: " + author + ", ISBN: " + isbn);
    }
}
