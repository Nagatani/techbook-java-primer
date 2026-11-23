// 第13章：親クラスの例外を先にcatchすると、後ろのcatchに届かずコンパイルエラー
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CatchOrder {
    static void run() {
        try {
            new FileReader("x").close();
        } catch (IOException e) {
        } catch (FileNotFoundException e) {
        }
    }
}
