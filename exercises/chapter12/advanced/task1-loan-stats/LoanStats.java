import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 貸出記録の集計。どのメソッドもStream APIとCollectorsで書く
 *
 * 結果のMapはTreeMapにして、キーの順に並ぶようにする（HashMapでは表示の順が決まらない）。
 */
public class LoanStats {
    private final List<Loan> loans;

    public LoanStats(List<Loan> loans) {
        this.loans = loans;
    }

    // ジャンルごとの貸出件数
    public Map<String, Long> countByGenre() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::genre, TreeMap::new, Collectors.counting()));
    }

    // よく借りられた本の上位n冊（件数の多い順、同じ件数なら書名の順）
    public List<String> topTitles(int n) {
        Map<String, Long> counts = loans.stream()
                .collect(Collectors.groupingBy(Loan::title, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .map(e -> e.getKey() + "（" + e.getValue() + "回）")
                .toList();
    }

    // 月ごとの利用者数（同じ人が何冊借りても1人と数える）
    public Map<Integer, Integer> membersByMonth() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::month, TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Loan::member, Collectors.toSet()),
                                Set::size)));
    }

    // 会員ごとに、借りたジャンルの一覧（重複なし、名前の順）
    public Map<String, Set<String>> genresByMember() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::member, TreeMap::new,
                        Collectors.mapping(Loan::genre, Collectors.toCollection(TreeSet::new))));
    }

    // 指定したジャンルを1回でも借りた会員（重複なし、名前の順）
    public List<String> membersWhoBorrowed(String genre) {
        return loans.stream()
                .filter(loan -> loan.genre().equals(genre))
                .map(Loan::member)
                .distinct()
                .sorted()
                .toList();
    }

    // 貸出回数がmin回以上の会員（true）と、それ未満の会員（false）に分ける
    public Map<Boolean, List<String>> splitByCount(int min) {
        Map<String, Long> counts = loans.stream()
                .collect(Collectors.groupingBy(Loan::member, TreeMap::new, Collectors.counting()));
        return counts.entrySet().stream()
                .collect(Collectors.partitioningBy(e -> e.getValue() >= min,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    }

    // 最も多く借りた会員の名前と回数（記録が空なら「なし」）
    public String mostActiveMember() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::member, TreeMap::new, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry<String, Long>::getValue))
                .map(e -> e.getKey() + "さん（" + e.getValue() + "回）")
                .orElse("なし");
    }
}
