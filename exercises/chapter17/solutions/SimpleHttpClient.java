package chapter17.solutions;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * 簡易HTTPクライアントの実装
 * ソケットを使用してHTTPリクエストを送信し、レスポンスを解析する
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class SimpleHttpClient {
    private static final Logger logger = Logger.getLogger(SimpleHttpClient.class.getName());
    private static final int DEFAULT_PORT = 80;
    private static final int HTTPS_PORT = 443;
    private static final int TIMEOUT_MS = 30000;
    private static final int MAX_REDIRECTS = 5;
    
    /**
     * HTTPレスポンスを表すクラス
     */
    public static class HttpResponse {
        private final int statusCode;
        private final String statusMessage;
        private final Map<String, String> headers;
        private final String body;
        
        public HttpResponse(int statusCode, String statusMessage, 
                          Map<String, String> headers, String body) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.headers = Collections.unmodifiableMap(headers);
            this.body = body;
        }
        
        public int getStatusCode() { return statusCode; }
        public String getStatusMessage() { return statusMessage; }
        public Map<String, String> getHeaders() { return headers; }
        public String getBody() { return body; }
        public String getHeader(String name) { 
            return headers.get(name.toLowerCase()); 
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("HTTP ").append(statusCode).append(" ").append(statusMessage).append("\n");
            headers.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
            sb.append("\n").append(body);
            return sb.toString();
        }
    }
    
    /**
     * HTTP GETリクエストを送信する
     * 
     * @param url リクエスト先のURL
     * @return HTTPレスポンス
     * @throws IOException 通信エラーが発生した場合
     */
    public HttpResponse get(String url) throws IOException {
        return request("GET", url, null, null);
    }
    
    /**
     * HTTP POSTリクエストを送信する
     * 
     * @param url リクエスト先のURL
     * @param body リクエストボディ
     * @param contentType Content-Typeヘッダーの値
     * @return HTTPレスポンス
     * @throws IOException 通信エラーが発生した場合
     */
    public HttpResponse post(String url, String body, String contentType) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(body.length()));
        return request("POST", url, headers, body);
    }
    
    /**
     * HTTPリクエストを送信する（内部メソッド）
     */
    private HttpResponse request(String method, String urlString, 
                               Map<String, String> additionalHeaders, 
                               String body) throws IOException {
        return requestWithRedirects(method, urlString, additionalHeaders, body, 0);
    }
    
    /**
     * リダイレクトを処理しながらHTTPリクエストを送信する
     */
    private HttpResponse requestWithRedirects(String method, String urlString, 
                                            Map<String, String> additionalHeaders, 
                                            String body, int redirectCount) throws IOException {
        if (redirectCount > MAX_REDIRECTS) {
            throw new IOException("リダイレクト回数が上限を超えました");
        }
        
        URL url = new URL(urlString);
        
        // HTTPSはサポートしない（簡易実装のため）
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            throw new IOException("HTTPSはサポートされていません");
        }
        
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            port = DEFAULT_PORT;
        }
        String path = url.getPath();
        if (path.isEmpty()) {
            path = "/";
        }
        if (url.getQuery() != null) {
            path += "?" + url.getQuery();
        }
        
        logger.info("HTTPリクエスト: " + method + " " + urlString);
        
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
            
            try (PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                 BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
                
                // リクエストの送信
                sendRequest(out, method, path, host, additionalHeaders, body);
                
                // レスポンスの受信
                HttpResponse response = readResponse(in);
                
                // リダイレクトの処理
                if (response.getStatusCode() >= 300 && response.getStatusCode() < 400) {
                    String location = response.getHeader("location");
                    if (location != null) {
                        logger.info("リダイレクト: " + location);
                        
                        // 相対URLの処理
                        if (!location.startsWith("http://") && !location.startsWith("https://")) {
                            if (location.startsWith("/")) {
                                location = url.getProtocol() + "://" + url.getHost() + 
                                         (url.getPort() != -1 ? ":" + url.getPort() : "") + location;
                            } else {
                                String basePath = path.substring(0, path.lastIndexOf('/') + 1);
                                location = url.getProtocol() + "://" + url.getHost() + 
                                         (url.getPort() != -1 ? ":" + url.getPort() : "") + 
                                         basePath + location;
                            }
                        }
                        
                        // GETメソッドでリダイレクト
                        return requestWithRedirects("GET", location, null, null, redirectCount + 1);
                    }
                }
                
                return response;
            }
        }
    }
    
    /**
     * HTTPリクエストを送信する
     */
    private void sendRequest(PrintWriter out, String method, String path, String host,
                           Map<String, String> additionalHeaders, String body) {
        // リクエストライン
        out.println(method + " " + path + " HTTP/1.1");
        
        // 必須ヘッダー
        out.println("Host: " + host);
        out.println("User-Agent: SimpleHttpClient/1.0");
        out.println("Accept: */*");
        out.println("Connection: close");
        
        // 追加ヘッダー
        if (additionalHeaders != null) {
            additionalHeaders.forEach((k, v) -> out.println(k + ": " + v));
        }
        
        // 空行
        out.println();
        
        // ボディ
        if (body != null) {
            out.print(body);
            out.flush();
        }
        
        logger.fine("リクエスト送信完了");
    }
    
    /**
     * HTTPレスポンスを読み取る
     */
    private HttpResponse readResponse(BufferedReader in) throws IOException {
        // ステータスラインの読み取り
        String statusLine = in.readLine();
        if (statusLine == null) {
            throw new IOException("レスポンスがありません");
        }
        
        logger.fine("ステータスライン: " + statusLine);
        
        String[] statusParts = statusLine.split(" ", 3);
        if (statusParts.length < 2) {
            throw new IOException("不正なステータスライン: " + statusLine);
        }
        
        int statusCode = Integer.parseInt(statusParts[1]);
        String statusMessage = statusParts.length > 2 ? statusParts[2] : "";
        
        // ヘッダーの読み取り
        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String name = line.substring(0, colonIndex).trim().toLowerCase();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(name, value);
                logger.fine("ヘッダー: " + name + " = " + value);
            }
        }
        
        // ボディの読み取り
        StringBuilder bodyBuilder = new StringBuilder();
        String contentLengthStr = headers.get("content-length");
        
        if (contentLengthStr != null) {
            // Content-Lengthが指定されている場合
            int contentLength = Integer.parseInt(contentLengthStr);
            char[] buffer = new char[contentLength];
            int totalRead = 0;
            
            while (totalRead < contentLength) {
                int read = in.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    break;
                }
                totalRead += read;
            }
            
            bodyBuilder.append(buffer, 0, totalRead);
        } else if ("chunked".equalsIgnoreCase(headers.get("transfer-encoding"))) {
            // チャンク転送エンコーディングの場合
            while (true) {
                String chunkSizeLine = in.readLine();
                if (chunkSizeLine == null) {
                    break;
                }
                
                int chunkSize = Integer.parseInt(chunkSizeLine.trim(), 16);
                if (chunkSize == 0) {
                    // 最後のチャンク
                    in.readLine(); // 最後の空行を読み飛ばす
                    break;
                }
                
                char[] chunk = new char[chunkSize];
                int read = in.read(chunk, 0, chunkSize);
                bodyBuilder.append(chunk, 0, read);
                in.readLine(); // チャンク後の改行を読み飛ばす
            }
        } else {
            // それ以外の場合は、接続が閉じるまで読み取る
            char[] buffer = new char[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                bodyBuilder.append(buffer, 0, read);
            }
        }
        
        return new HttpResponse(statusCode, statusMessage, headers, bodyBuilder.toString());
    }
    
    /**
     * メインメソッド（使用例）
     * 
     * @param args コマンドライン引数（URL）
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("使用方法: java SimpleHttpClient <URL>");
            System.exit(1);
        }
        
        SimpleHttpClient client = new SimpleHttpClient();
        
        try {
            HttpResponse response = client.get(args[0]);
            
            System.out.println("ステータスコード: " + response.getStatusCode());
            System.out.println("ステータスメッセージ: " + response.getStatusMessage());
            System.out.println("\n--- ヘッダー ---");
            response.getHeaders().forEach((k, v) -> 
                System.out.println(k + ": " + v));
            System.out.println("\n--- ボディ ---");
            System.out.println(response.getBody());
            
        } catch (MalformedURLException e) {
            System.err.println("不正なURL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("通信エラー: " + e.getMessage());
            logger.log(Level.SEVERE, "HTTPリクエストエラー", e);
        }
    }
}