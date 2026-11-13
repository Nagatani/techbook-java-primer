package chapter16.solutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * SimpleServerに文字列を送るTCPクライアント（基礎課題1の解答例）
 */
public class SimpleClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
        String[] messages = {"hello", "java network", "bye"};
        try (Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
            for (String message : messages) {
                out.println(message);
                IO.println("送信: " + message + " -> 返事: " + in.readLine());
            }
        } catch (ConnectException e) {
            System.err.println("サーバーにつながりません（先にSimpleServerを起動してください）: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("通信エラー: " + e.getMessage());
        }
    }
}
