// 第16章：クライアント側のポート番号はOSが自動で割り当てる。PrintWriterの自動フラッシュ
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketFacts {
    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(0);
             Socket client = new Socket("localhost", server.getLocalPort());
             Socket accepted = server.accept()) {
            int port = client.getLocalPort();
            String os = System.getProperty("os.name").toLowerCase();
            boolean inRange = os.contains("linux") ? port >= 32768 && port <= 60999
                    : port >= 49152 && port <= 65535;
            System.out.println("client port in the OS's ephemeral range: " + inRange);

            PrintWriter auto = new PrintWriter(client.getOutputStream(), true);
            auto.println("hello");
            BufferedReader in = new BufferedReader(new InputStreamReader(accepted.getInputStream()));
            accepted.setSoTimeout(2000);
            System.out.println("autoflush println received: " + in.readLine());
        }
    }
}
