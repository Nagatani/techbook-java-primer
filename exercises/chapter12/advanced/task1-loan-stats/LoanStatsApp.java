import java.util.List;
import java.util.Map;

/**
 * 第12章 発展課題1: 図書館の貸出記録の集計（解答例）
 *
 * 貸出記録のリストを、Stream APIとCollectors（groupingBy・counting・mapping・
 * collectingAndThen・partitioningBy）で集計します。
 */
public class LoanStatsApp {
    public static void main(String[] args) {
        List<Loan> loans = LoanData.sample();
        LoanStats stats = new LoanStats(loans);

        IO.println("貸出記録: " + loans.size() + "件");

        IO.println();
        IO.println("=== ジャンルごとの貸出件数 ===");
        stats.countByGenre().forEach((genre, count) -> IO.println("  " + genre + ": " + count + "件"));

        IO.println();
        IO.println("=== よく借りられた本（上位3冊） ===");
        List<String> top = stats.topTitles(3);
        for (int i = 0; i < top.size(); i++) {
            IO.println("  " + (i + 1) + "位 " + top.get(i));
        }

        IO.println();
        IO.println("=== 月ごとの利用者数 ===");
        stats.membersByMonth().forEach((month, count) -> IO.println("  " + month + "月: " + count + "人"));

        IO.println();
        IO.println("=== 会員ごとに借りたジャンル ===");
        stats.genresByMember().forEach((member, genres) -> IO.println("  " + member + ": " + genres));

        IO.println();
        IO.println("=== 小説を借りた会員 ===");
        IO.println("  " + stats.membersWhoBorrowed("小説"));

        IO.println();
        IO.println("=== 貸出回数で分ける（3回以上） ===");
        Map<Boolean, List<String>> split = stats.splitByCount(3);
        IO.println("  3回以上: " + split.get(true));
        IO.println("  3回未満: " + split.get(false));

        IO.println();
        IO.println("=== 最も多く借りた会員 ===");
        IO.println("  " + stats.mostActiveMember());
        IO.println("  記録がない場合: " + new LoanStats(List.of()).mostActiveMember());
    }
}
