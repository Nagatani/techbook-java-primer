/**
 * ステータスコードが200番台でなかったことを表す例外
 */
public class ApiException extends Exception {
    private final int statusCode;
    private final String body;

    public ApiException(int statusCode, String body) {
        super("ステータスコード " + statusCode + ": " + body);
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
