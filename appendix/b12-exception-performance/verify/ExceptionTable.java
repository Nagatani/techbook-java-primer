/**
 * try-catchがバイトコードでどう表されるかを見る。
 *
 *   javac ExceptionTable.java && javap -c -p ExceptionTable
 */
public class ExceptionTable {

    static int compute(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("negative");
        }
        return x * 2;
    }

    /** tryのない呼び出し */
    static int plain(int x) {
        return compute(x);
    }

    /** 同じ呼び出しをtry-catchで囲んだもの */
    static int guarded(int x) {
        try {
            return compute(x);
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        IO.println(plain(3) + " " + guarded(3) + " " + guarded(-3));
    }
}
