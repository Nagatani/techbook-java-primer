// 第15章：スコープ値（Java 25で正式。Java 24まではプレビュー）
public class ScopedValues {
    static final ScopedValue<String> USER = ScopedValue.newInstance();

    static void run() {
        ScopedValue.where(USER, "alice").run(() -> {
            String name = USER.get();
        });
    }
}
