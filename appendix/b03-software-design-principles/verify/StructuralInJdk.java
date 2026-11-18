import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 標準ライブラリの中の構造に関するパターン。
 * 実行: javac StructuralInJdk.java && java StructuralInJdk
 */
public class StructuralInJdk {
    public static void main(String[] args) throws IOException {
        // 変更できない「ビュー」：元のリストを包み、変更の操作だけを断る
        List<String> original = new ArrayList<>(List.of("a"));
        List<String> view = Collections.unmodifiableList(original);
        List<String> copy = List.copyOf(original);
        original.add("b");
        IO.println("unmodifiableListのクラス: " + view.getClass().getName());
        IO.println("元に足したあとのビュー: " + view);
        IO.println("元に足したあとのコピー: " + copy);

        // アダプター：配列をListとして使えるようにする（書き込みは配列に届く）
        String[] array = {"x", "y"};
        List<String> asList = Arrays.asList(array);
        asList.set(0, "z");
        IO.println("Arrays.asListのsetのあとの配列: " + Arrays.toString(array));

        // デコレーター：FilterInputStreamはInputStreamであり、InputStreamを1つ包む
        IO.println("FilterInputStreamのスーパークラス: "
                + FilterInputStream.class.getSuperclass().getName());
        IO.println("FilterInputStreamのコンストラクターの引数: "
                + Arrays.toString(FilterInputStream.class.getDeclaredConstructors()[0]
                        .getParameterTypes()));
        IO.println("BufferedInputStreamのスーパークラス: "
                + BufferedInputStream.class.getSuperclass().getName());
        IO.println("DataInputStreamのスーパークラス: "
                + DataInputStream.class.getSuperclass().getName());

        byte[] bytes = {0, 0, 1, 0};
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new ByteArrayInputStream(bytes)))) {
            IO.println("3段に包んだストリームからreadInt: " + in.readInt());
        }
        InputStream plain = new ByteArrayInputStream(bytes);
        IO.println("包まないストリームにreadIntはない: "
                + Arrays.stream(plain.getClass().getMethods())
                        .noneMatch(m -> m.getName().equals("readInt")));
    }
}
