import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LoanServiceのテスト。依存するものは手書きのテストダブルに差し替える
 * - 目録: 決まった本を返すスタブ（ラムダ式）
 * - 貸出の記録: メモリに保存するフェイク（InMemoryLoanRepository）
 * - お知らせ: 送った内容を記録するスパイ（RecordingNotifier）
 * - 今日の日付: 変えられる日付（today）
 */
@DisplayName("貸出サービス（手書きのテストダブル）")
class LoanServiceTest {
    /** 送ったお知らせを記録するだけのNotifier */
    static class RecordingNotifier implements Notifier {
        final List<String> messages = new ArrayList<>();

        @Override
        public void send(String memberId, String message) {
            messages.add(memberId + ": " + message);
        }
    }

    private static final Book JAVA = new Book("1", "Java入門");
    private static final Book ALGO = new Book("2", "アルゴリズム図鑑");
    private static final Book DB = new Book("3", "データベースの基礎");
    private static final Book NET = new Book("4", "ネットワーク概論");
    private static final LocalDate APRIL_1 = LocalDate.of(2026, 4, 1);

    private InMemoryLoanRepository repository;
    private RecordingNotifier notifier;
    private LocalDate today;
    private LoanService service;

    @BeforeEach
    void setUp() {
        Map<String, Book> books = Map.of("1", JAVA, "2", ALGO, "3", DB, "4", NET);
        BookCatalog catalog = isbn -> Optional.ofNullable(books.get(isbn));
        repository = new InMemoryLoanRepository();
        notifier = new RecordingNotifier();
        today = APRIL_1;
        // ラムダ式の中から、テストで書き換えるフィールドtodayを読む
        service = new LoanService(catalog, repository, notifier, () -> today);
    }

    @Nested
    @DisplayName("貸出")
    class Borrow {
        @Test
        @DisplayName("返却期限は今日から14日後で、記録が保存される")
        void borrowBook() throws LoanException {
            Loan loan = service.borrow("M001", "1");

            assertAll(
                () -> assertEquals(LocalDate.of(2026, 4, 15), loan.dueDate()),
                () -> assertEquals(Optional.of(loan), repository.findByIsbn("1")));
        }

        @Test
        @DisplayName("蔵書にない本は貸し出せない")
        void unknownBook() {
            LoanException e = assertThrows(LoanException.class, () -> service.borrow("M001", "9"));
            assertEquals("ISBN 9 の本は蔵書にありません", e.getMessage());
        }

        @Test
        @DisplayName("貸出中の本は貸し出せない")
        void alreadyOnLoan() throws LoanException {
            service.borrow("M001", "1");
            assertThrows(LoanException.class, () -> service.borrow("M002", "1"));
        }

        @Test
        @DisplayName("3冊借りている会員は4冊目を借りられない")
        void tooManyBooks() throws LoanException {
            service.borrow("M001", "1");
            service.borrow("M001", "2");
            service.borrow("M001", "3");

            LoanException e = assertThrows(LoanException.class, () -> service.borrow("M001", "4"));
            assertEquals("M001さんはすでに3冊借りています", e.getMessage());
        }

        @Test
        @DisplayName("返却期限を過ぎた本がある会員は借りられない")
        void hasOverdueBook() throws LoanException {
            service.borrow("M001", "1");
            today = APRIL_1.plusDays(15);

            assertThrows(LoanException.class, () -> service.borrow("M001", "2"));
        }

        @Test
        @DisplayName("返却期限の当日はまだ借りられる")
        void onDueDate() throws LoanException {
            service.borrow("M001", "1");
            today = APRIL_1.plusDays(14);

            assertEquals(LocalDate.of(2026, 4, 29), service.borrow("M001", "2").dueDate());
        }
    }

    @Nested
    @DisplayName("返却")
    class Return {
        @Test
        @DisplayName("期限内の返却は料金0円で、お知らせを送らない")
        void returnOnTime() throws LoanException {
            service.borrow("M001", "1");
            today = APRIL_1.plusDays(14);

            assertEquals(0, service.returnBook("1"));
            assertTrue(repository.findAll().isEmpty());
            assertTrue(notifier.messages.isEmpty());
        }

        @Test
        @DisplayName("3日遅れの返却は30円で、お知らせを送る")
        void returnLate() throws LoanException {
            service.borrow("M001", "1");
            today = APRIL_1.plusDays(17);

            assertEquals(30, service.returnBook("1"));
            assertEquals(List.of("M001: 「Java入門」の延滞料金は30円です"), notifier.messages);
        }

        @Test
        @DisplayName("貸し出していない本は返却できない")
        void notOnLoan() {
            assertThrows(LoanException.class, () -> service.returnBook("1"));
        }
    }

    @Nested
    @DisplayName("延滞のお知らせ")
    class OverdueNotices {
        @Test
        @DisplayName("期限を過ぎた貸出だけにお知らせを送る")
        void onlyOverdue() throws LoanException {
            service.borrow("M001", "1");          // 4/15が期限
            today = APRIL_1.plusDays(5);
            service.borrow("M002", "2");          // 4/20が期限
            today = APRIL_1.plusDays(16);         // 4/17

            assertEquals(1, service.sendOverdueNotices());
            assertEquals(List.of("M001: 「Java入門」の返却期限（2026-04-15）を2日過ぎています"),
                notifier.messages);
        }

        @Test
        @DisplayName("延滞がなければ何も送らない")
        void nothingOverdue() throws LoanException {
            service.borrow("M001", "1");

            assertEquals(0, service.sendOverdueNotices());
            assertTrue(notifier.messages.isEmpty());
        }
    }
}
