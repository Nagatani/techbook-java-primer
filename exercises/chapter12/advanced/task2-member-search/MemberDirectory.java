import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 会員名簿。見つからないかもしれない検索の結果をOptionalで返す
 */
public class MemberDirectory {
    private final List<Member> members;
    private final Map<String, Integer> coupons;  // クーポンの番号 → 割引額

    public MemberDirectory(List<Member> members, Map<String, Integer> coupons) {
        this.members = members;
        this.coupons = coupons;
    }

    // 会員番号で探す
    public Optional<Member> findById(String id) {
        return members.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    // 名前の一部で探し、最初に見つかった会員を返す
    public Optional<Member> findByName(String part) {
        return members.stream()
                .filter(m -> m.getName().contains(part))
                .findFirst();
    }

    // 会員のメールアドレス。会員がいない場合も、未登録の場合も空になる
    public Optional<String> emailOf(String id) {
        return findById(id).flatMap(Member::getEmail);
    }

    // クーポンの割引額。Map.getはないときnullを返すので、ofNullableで包む
    public Optional<Integer> couponDiscount(String code) {
        return Optional.ofNullable(coupons.get(code));
    }

    // ポイントが最も多い会員
    public Optional<Member> topMember() {
        return members.stream()
                .max(Comparator.comparingInt(Member::getPoints));
    }
}
