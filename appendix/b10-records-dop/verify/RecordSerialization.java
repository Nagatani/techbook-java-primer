import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

/**
 * シリアライズしたレコードを読み戻すとき、正準コンストラクターが呼ばれることを観察する。
 * ふつうのクラスでは、Serializableなクラスのコンストラクターは呼ばれない。
 *
 *   javac RecordSerialization.java
 *   java RecordSerialization
 */
public class RecordSerialization {
    record Range(int low, int high) implements Serializable {
        Range {
            IO.println("  Rangeの正準コンストラクター(" + low + ", " + high + ")");
            if (low > high) {
                throw new IllegalArgumentException("low > high");
            }
        }
    }

    static class OldRange implements Serializable {
        private static final long serialVersionUID = 1L;
        final int low;
        final int high;

        OldRange(int low, int high) {
            IO.println("  OldRangeのコンストラクター(" + low + ", " + high + ")");
            if (low > high) {
                throw new IllegalArgumentException("low > high");
            }
            this.low = low;
            this.high = high;
        }
    }

    static byte[] write(Object o) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(bytes)) {
            out.writeObject(o);
        }
        return bytes.toByteArray();
    }

    static Object read(byte[] data) throws IOException, ClassNotFoundException {
        try (var in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        IO.println("書き込み: Range");
        byte[] rangeBytes = write(new Range(1, 5));
        IO.println("読み込み: Range");
        IO.println("  結果: " + read(rangeBytes));

        // バイト列のlowの値を9に書き換え、low > highのデータを読ませる
        // （基本型のフィールドは名前の順にhigh・lowと並ぶので、末尾の4バイトがlow）
        byte[] tampered = rangeBytes.clone();
        tampered[tampered.length - 1] = 9;
        IO.println("読み込み: 書き換えたRange");
        try {
            read(tampered);
        } catch (InvalidObjectException e) {
            IO.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        IO.println("書き込み: OldRange");
        byte[] oldBytes = write(new OldRange(1, 5));
        IO.println("読み込み: OldRange");
        var old = (OldRange) read(oldBytes);
        IO.println("  結果: " + old.low + ".." + old.high);
        byte[] oldTampered = oldBytes.clone();
        oldTampered[oldTampered.length - 1] = 9;
        IO.println("読み込み: 書き換えたOldRange");
        var bad = (OldRange) read(oldTampered);
        IO.println("  結果: " + bad.low + ".." + bad.high + "（コンストラクターの検査を通らずに読み込まれる）");

        // 宣言していないレコードのserialVersionUIDは0L
        IO.println("RangeのserialVersionUID: " + ObjectStreamClass.lookup(Range.class).getSerialVersionUID());
    }
}
