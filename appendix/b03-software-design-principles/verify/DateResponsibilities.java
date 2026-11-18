import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;

/**
 * 単一責任の原則：java.util.Dateが持っていた責任と、その分割の跡を観察する。
 * JDK 1.1で、日付と時刻の欄（年・月・日など）の計算はCalendarへ、
 * 文字列との変換はDateFormatへ移され、Dateの該当するメソッドは非推奨になった。
 * 実行: javac DateResponsibilities.java && java DateResponsibilities
 */
public class DateResponsibilities {
    public static void main(String[] args) {
        Method[] deprecated = Arrays.stream(Date.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .filter(m -> m.isAnnotationPresent(Deprecated.class))
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .toArray(Method[]::new);
        long publicMethods = Arrays.stream(Date.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .count();
        IO.println("Dateが宣言するpublicメソッド: " + publicMethods);
        IO.println("そのうち非推奨: " + deprecated.length);

        StringBuilder fields = new StringBuilder();
        StringBuilder text = new StringBuilder();
        for (Method m : deprecated) {
            String name = m.getName();
            boolean isText = name.equals("parse") || name.startsWith("to");
            (isText ? text : fields).append(' ').append(name);
        }
        IO.println("欄の計算（Calendarへ移った）:" + fields);
        IO.println("文字列との変換（DateFormatへ移った）:" + text);
    }
}
