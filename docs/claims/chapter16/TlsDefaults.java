// 第16章：JDK 25のTLSの既定（クライアントで有効なプロトコル）
import java.util.Arrays;
import javax.net.ssl.SSLContext;

public class TlsDefaults {
    public static void main(String[] args) throws Exception {
        SSLContext ctx = SSLContext.getDefault();
        System.out.println("default: " + Arrays.toString(ctx.getDefaultSSLParameters().getProtocols()));
        System.out.println("supported: " + Arrays.toString(ctx.getSupportedSSLParameters().getProtocols()));
    }
}
