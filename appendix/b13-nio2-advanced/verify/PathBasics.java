import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pathは文字列ではなく「名前の要素の並び」であることを観察する。
 * 区切り文字がOSで変わらないよう、相対パスと「/」を含まない比べ方で示す。
 */
public class PathBasics {
    public static void main(String[] args) throws IOException {
        Path p = Path.of("reports", "2026", "..", "summary.txt");
        IO.println("要素の数: " + p.getNameCount());
        IO.println("normalize後の要素の数: " + p.normalize().getNameCount());
        IO.println("normalize後のファイル名: " + p.normalize().getFileName());

        // startsWithは要素ごとに比べる（文字列の前方一致ではない）
        Path q = Path.of("reports", "2026");
        IO.println("Path.startsWith(\"rep\"): " + q.startsWith("rep"));
        IO.println("Path.startsWith(\"reports\"): " + q.startsWith("reports"));
        IO.println("String.startsWith(\"rep\"): " + q.toString().startsWith("rep"));

        // relativizeとresolveは逆の操作
        Path base = Path.of("a", "b");
        Path target = Path.of("a", "c", "d.txt");
        Path rel = base.relativize(target);
        IO.println("relativizeの要素: " + names(rel));
        IO.println("resolveで戻すと等しい: " + base.resolve(rel).normalize().equals(target));

        // toAbsolutePathは存在を確かめないが、toRealPathは確かめる
        Path missing = Path.of("no-such-dir", "file.txt");
        IO.println("toAbsolutePathは成功: " + missing.toAbsolutePath().isAbsolute());
        try {
            missing.toRealPath();
        } catch (IOException e) {
            IO.println("toRealPath: " + e.getClass().getName());
        }
    }

    private static String names(Path p) {
        StringBuilder sb = new StringBuilder();
        for (Path name : p) {
            sb.append('[').append(name).append(']');
        }
        return sb.toString();
    }
}
