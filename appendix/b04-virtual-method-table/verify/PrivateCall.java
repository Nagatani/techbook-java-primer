/**
 * privateメソッドの呼び出し命令が、Java 11（ネストメイト）で変わったことを観察する。
 *
 *   javac --release 10 -d r10 PrivateCall.java && javap -c -p -cp r10 PrivateCall
 *   javac --release 11 -d r11 PrivateCall.java && javap -c -p -cp r11 PrivateCall
 */
public class PrivateCall {
    private void helper() { }

    void call() {
        helper();
    }
}
