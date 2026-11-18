/**
 * 文字列の結合（+）がどのバイトコードになるかを観察する（javapで見る）。
 *   javac StringConcat.java && javap -c -p StringConcat
 *   javac --release 8 -d old StringConcat.java && javap -c -p -cp old StringConcat
 * --release 8でもコンパイルできるよう、IOクラスは使っていない。
 */
public class StringConcat {
    static String greet(String name, int count) {
        return "Hello, " + name + "! #" + count;
    }
}
