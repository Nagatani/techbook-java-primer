/**
 * リスト22-7
 * OkHttpExampleクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (669行目)
 */

import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpExample {
    public static void main(String[] args) throws IOException {
        // OkHttpClientの設定
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        
        // 1. GETリクエスト
        Request getRequest = new Request.Builder()
            .url("https://api.github.com/users/github")
            .header("User-Agent", "OkHttp Example")
            .build();
        
        try (Response response = client.newCall(getRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            System.out.println("GET Response:");
            System.out.println("Status: " + response.code());
            System.out.println("Body: " + response.body().string());
        }
        
        // 2. POSTリクエスト（JSONデータ送信）
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = """
            {
                "name": "Test User",
                "email": "test@example.com"
            }
            """;
        
        RequestBody body = RequestBody.create(json, JSON);
        Request postRequest = new Request.Builder()
            .url("https://httpbin.org/post")
            .post(body)
            .build();
        
        try (Response response = client.newCall(postRequest).execute()) {
            System.out.println("\nPOST Response:");
            System.out.println("Status: " + response.code());
            System.out.println("Body: " + response.body().string());
        }
        
        // 3. 非同期リクエスト
        Request asyncRequest = new Request.Builder()
            .url("https://httpbin.org/delay/2")
            .build();
        
        client.newCall(asyncRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    System.out.println("\nAsync Response received!");
                    System.out.println("Thread: " + Thread.currentThread().getName());
                }
            }
        });
        
        // 非同期処理の完了を待つ
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}