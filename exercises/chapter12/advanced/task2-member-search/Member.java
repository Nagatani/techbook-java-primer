import java.util.Optional;

/**
 * 店の会員。メールアドレスは登録していない会員もいる
 *
 * フィールドはnullを許すStringのままにし、getterでOptionalにして返す
 * （フィールドや引数にOptionalを使わない）。
 */
public class Member {
    private final String id;
    private final String name;
    private final String email;  // 未登録ならnull
    private final int points;

    public Member(String id, String name, String email, int points) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public int getPoints() {
        return points;
    }
}
