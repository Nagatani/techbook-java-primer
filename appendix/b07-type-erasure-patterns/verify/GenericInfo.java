import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 実行時に型引数がどこまでわかるかを観察する。
 * オブジェクトそのものからは型引数がわからないが、宣言（フィールド・メソッド・スーパークラス）に
 * 書いた型引数はクラスファイルのSignature属性に残り、リフレクションで読める。
 *
 *   javac GenericInfo.java && java GenericInfo
 *   javap -v GenericInfo      Signature属性を見る
 */
public class GenericInfo {
    private List<String> names = new ArrayList<>();
    private Map<String, List<Integer>> scores;

    public List<String> getNames() { return names; }

    // 型引数を固定してクラスを継承すると、その型引数もクラスファイルに残る
    static class StringList extends ArrayList<String> { }

    public static void main(String[] args) throws Exception {
        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();
        IO.println("同じクラスか: " + (strings.getClass() == numbers.getClass()));
        IO.println("オブジェクトのクラス: " + strings.getClass().getName());
        IO.println("クラスの型パラメーター: "
                + Arrays.toString(strings.getClass().getTypeParameters()));

        Type field = GenericInfo.class.getDeclaredField("names").getGenericType();
        IO.println("フィールドnamesの型: " + field);
        Type map = GenericInfo.class.getDeclaredField("scores").getGenericType();
        IO.println("フィールドscoresの型: " + map);
        Type nested = ((ParameterizedType) map).getActualTypeArguments()[1];
        IO.println("  2つ目の型引数: " + nested);

        Method getter = GenericInfo.class.getMethod("getNames");
        IO.println("getNamesの戻り値の型: " + getter.getGenericReturnType());
        IO.println("getNamesの戻り値のクラス: " + getter.getReturnType().getName());

        IO.println("StringListのスーパークラス: "
                + StringList.class.getGenericSuperclass());
    }
}
