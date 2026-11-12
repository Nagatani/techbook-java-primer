import java.util.List;
import java.util.Optional;

/** 貸出の記録を保存する場所 */
public interface LoanRepository {
    void save(Loan loan);

    Optional<Loan> findByIsbn(String isbn);

    List<Loan> findByMember(String memberId);

    List<Loan> findAll();

    void delete(String isbn);
}
