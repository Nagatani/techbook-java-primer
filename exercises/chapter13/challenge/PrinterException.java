/** レシートの印刷に失敗したときの例外。やり直せば成功することがある */
public class PrinterException extends Exception {
    public PrinterException(String message) {
        super(message);
    }
}
