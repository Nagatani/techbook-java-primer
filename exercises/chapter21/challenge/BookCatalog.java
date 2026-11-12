import java.util.Optional;

/** 蔵書の目録。ISBNから本を探す */
public interface BookCatalog {
    Optional<Book> findByIsbn(String isbn);
}
