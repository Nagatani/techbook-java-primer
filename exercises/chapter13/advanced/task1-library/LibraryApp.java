/** 図書館の例外の階層を使い分けるmainメソッド */
public class LibraryApp {
    public static void main(String[] args) {
        Library library = new Library();
        library.addBook("B001", "Java入門");
        library.addBook("B002", "アルゴリズム図鑑");
        library.addBook("B003", "データベースの基礎");

        IO.println("=== 貸出（例外の種類ごとに処理を変える） ===");
        tryLend(library, "佐藤", "B001", 1);
        tryLend(library, "鈴木", "B001", 2);
        tryLend(library, "佐藤", "B999", 2);
        tryLend(library, "佐藤", "B002", 3);
        tryLend(library, "佐藤", "B003", 3);

        IO.println();
        IO.println("=== 親クラスでまとめて受け取る ===");
        String[][] requests = {{"高橋", "B003"}, {"高橋", "B002"}, {"高橋", "B000"}};
        for (String[] request : requests) {
            try {
                int due = library.lend(request[0], request[1], 5);
                IO.println(request[0] + "さんに" + request[1] + "を貸し出しました（返却期限 " + due + "日）");
            } catch (LibraryException e) {
                IO.println("貸出できません（" + e.getClass().getSimpleName()
                        + "、資料番号 " + e.getBookId() + "）: " + e.getMessage());
            }
        }

        IO.println();
        IO.println("=== 返却 ===");
        tryGiveBack(library, "B001");
        tryGiveBack(library, "B001");
        tryGiveBack(library, "B404");

        IO.println();
        IO.println("=== プログラムの誤り（非検査例外） ===");
        try {
            library.lend("", "B001", 6);
        } catch (LibraryException e) {
            IO.println("貸出できません: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            IO.println("呼び出し方の誤り: " + e.getMessage());
        }
    }

    private static void tryLend(Library library, String member, String bookId, int today) {
        try {
            int due = library.lend(member, bookId, today);
            IO.println(today + "日: " + member + "さんに「" + library.getTitle(bookId)
                    + "」を貸し出しました（返却期限 " + due + "日）");
        } catch (BookUnavailableException e) {
            IO.println(today + "日: " + e.getMessage() + " → " + e.getDueDay() + "日以降に来てください");
        } catch (LoanLimitException e) {
            IO.println(today + "日: " + e.getMessage() + " → 返却してから借りてください");
        } catch (LibraryException e) {
            // 子クラスのcatchより後ろに書く（前に書くとコンパイルエラーになる）
            IO.println(today + "日: " + e.getMessage());
        }
    }

    private static void tryGiveBack(Library library, String bookId) {
        try {
            String member = library.giveBack(bookId);
            IO.println(member + "さんから" + bookId + "が返却されました");
        } catch (BookNotFoundException | IllegalStateException e) {
            // 処理が同じ例外は、マルチキャッチでまとめる
            IO.println("返却できません: " + e.getMessage());
        }
    }
}
