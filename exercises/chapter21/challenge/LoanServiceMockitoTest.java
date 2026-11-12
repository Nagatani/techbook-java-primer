import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LoanServiceのテスト。依存するものをMockitoのモックにする
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("貸出サービス（Mockito）")
class LoanServiceMockitoTest {
    private static final Book JAVA = new Book("1", "Java入門");
    private static final LocalDate APRIL_20 = LocalDate.of(2026, 4, 20);

    @Mock
    private BookCatalog catalog;
    @Mock
    private LoanRepository repository;
    @Mock
    private Notifier notifier;

    private LoanService service;

    @BeforeEach
    void setUp() {
        service = new LoanService(catalog, repository, notifier, () -> APRIL_20);
    }

    @Test
    @DisplayName("貸し出すと、返却期限を入れた記録をリポジトリに保存する")
    void borrowSavesLoan() throws LoanException {
        when(catalog.findByIsbn("1")).thenReturn(Optional.of(JAVA));
        when(repository.findByIsbn("1")).thenReturn(Optional.empty());
        when(repository.findByMember("M001")).thenReturn(List.of());

        service.borrow("M001", "1");

        verify(repository).save(new Loan(JAVA, "M001", LocalDate.of(2026, 5, 4)));
    }

    @Test
    @DisplayName("蔵書にない本なら、リポジトリに何も保存しない")
    void unknownBookIsNotSaved() {
        when(catalog.findByIsbn("9")).thenReturn(Optional.empty());

        assertThrows(LoanException.class, () -> service.borrow("M001", "9"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("5日遅れの返却なら、記録を消して延滞料金のお知らせを送る")
    void lateReturnNotifies() throws LoanException {
        Loan loan = new Loan(JAVA, "M001", APRIL_20.minusDays(5));
        when(repository.findByIsbn("1")).thenReturn(Optional.of(loan));

        assertEquals(50, service.returnBook("1"));
        verify(repository).delete("1");
        verify(notifier).send("M001", "「Java入門」の延滞料金は50円です");
    }

    @Test
    @DisplayName("期限内の返却なら、お知らせを送らない")
    void onTimeReturnDoesNotNotify() throws LoanException {
        Loan loan = new Loan(JAVA, "M001", APRIL_20);
        when(repository.findByIsbn("1")).thenReturn(Optional.of(loan));

        assertEquals(0, service.returnBook("1"));
        verify(notifier, never()).send(anyString(), anyString());
    }
}
