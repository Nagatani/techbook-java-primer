/**
 * リスト17-5
 * HttpURLConnectionExampleクラス
 * 
 * 元ファイル: chapter17-network-programming.md (375行目)
 */

import java.io.*;
import java.net.*;

public class HttpURLConnectionExample {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://api.github.com/users/github");
            HttpURLConnection conn = 
                (HttpURLConnection) url.openConnection();
            
            // リクエストの設定
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            // レスポンスコードの確認
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // レスポンスの読み取り
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            conn.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    System.out.println(
                        "Response: " + response.toString());
                }
            }
            
            conn.disconnect();
            
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
        }
    }
}