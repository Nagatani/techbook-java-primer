package chapter07.solutions;

/**
 * ログだけを記録する（課題4の解答例）
 */
public class SystemLogger implements Loggable {
    @Override
    public String getSourceName() {
        return "システム情報";
    }
}
