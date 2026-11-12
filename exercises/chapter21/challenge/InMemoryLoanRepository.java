import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 貸出の記録をメモリに保存する。デモとテスト（フェイク）で使う */
public class InMemoryLoanRepository implements LoanRepository {
    // 登録した順に並べるためLinkedHashMapを使う
    private final Map<String, Loan> loans = new LinkedHashMap<>();

    @Override
    public void save(Loan loan) {
        loans.put(loan.book().isbn(), loan);
    }

    @Override
    public Optional<Loan> findByIsbn(String isbn) {
        return Optional.ofNullable(loans.get(isbn));
    }

    @Override
    public List<Loan> findByMember(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans.values()) {
            if (loan.memberId().equals(memberId)) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }

    @Override
    public void delete(String isbn) {
        loans.remove(isbn);
    }
}
