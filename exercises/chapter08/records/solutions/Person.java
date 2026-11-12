import java.time.LocalDate;
import java.time.Period;

/**
 * 人（課題2の解答例）
 */
public record Person(String name, LocalDate birthDate, String email) {

    public Person {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("誕生日は今日以前の日付にします: " + birthDate);
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("メールアドレスには@が要ります: " + email);
        }
    }

    // 今日の時点の年齢。実行する日で値が変わる
    public int getAge() {
        return getAgeOn(LocalDate.now());
    }

    // 指定した日の時点の年齢。日付を決めて試せるように分けている
    public int getAgeOn(LocalDate date) {
        return Period.between(birthDate, date).getYears();
    }

    public String getEmailDomain() {
        return email.substring(email.indexOf('@') + 1);
    }

    // 空白で区切った語の先頭を大文字にする（"john doe" → "John Doe"）
    public String getFormattedName() {
        String[] words = name.trim().split(" +");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
        }
        return sb.toString();
    }

    // 誕生日を今日から20年前にしたPerson
    public static Person of(String name, String email) {
        return new Person(name, LocalDate.now().minusYears(20), email);
    }
}
