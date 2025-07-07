package com.example.oss.api;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent APIを使用したHTTPリクエストビルダー
 * 
 * <p>このクラスは、HTTPリクエストを構築するための流暢なインターフェースを提供します。
 * メソッドチェーンにより、読みやすく、タイプセーフなコードを実現します。
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * HttpRequest request = HttpRequestBuilder.create()
 *     .post("https://api.example.com/users")
 *     .header("Content-Type", "application/json")
 *     .header("Authorization", "Bearer " + token)
 *     .queryParam("include", "profile")
 *     .timeout(Duration.ofSeconds(10))
 *     .body(userCreateRequest)
 *     .build();
 * }</pre>
 * 
 * @since 1.0.0
 * @author Example Team
 */
public class HttpRequestBuilder {
    private String method = "GET";
    private String url;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private Object body;
    private Duration timeout = Duration.ofSeconds(30);
    
    /**
     * プライベートコンストラクタ
     * ビルダーインスタンスは{@link #create()}メソッドで生成します
     */
    private HttpRequestBuilder() {}
    
    /**
     * 新しいビルダーインスタンスを作成します
     * 
     * @return 新しいHttpRequestBuilderインスタンス
     */
    public static HttpRequestBuilder create() {
        return new HttpRequestBuilder();
    }
    
    /**
     * GETリクエストを設定します
     * 
     * @param url リクエストURL
     * @return このビルダーインスタンス
     * @throws NullPointerException URLがnullの場合
     */
    public HttpRequestBuilder get(String url) {
        this.method = "GET";
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        return this;
    }
    
    /**
     * POSTリクエストを設定します
     * 
     * @param url リクエストURL
     * @return このビルダーインスタンス
     * @throws NullPointerException URLがnullの場合
     */
    public HttpRequestBuilder post(String url) {
        this.method = "POST";
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        return this;
    }
    
    /**
     * PUTリクエストを設定します
     * 
     * @param url リクエストURL
     * @return このビルダーインスタンス
     * @throws NullPointerException URLがnullの場合
     */
    public HttpRequestBuilder put(String url) {
        this.method = "PUT";
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        return this;
    }
    
    /**
     * DELETEリクエストを設定します
     * 
     * @param url リクエストURL
     * @return このビルダーインスタンス
     * @throws NullPointerException URLがnullの場合
     */
    public HttpRequestBuilder delete(String url) {
        this.method = "DELETE";
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        return this;
    }
    
    /**
     * HTTPヘッダーを追加します
     * 
     * @param name ヘッダー名
     * @param value ヘッダー値
     * @return このビルダーインスタンス
     * @throws NullPointerException nameまたはvalueがnullの場合
     */
    public HttpRequestBuilder header(String name, String value) {
        Objects.requireNonNull(name, "Header name cannot be null");
        Objects.requireNonNull(value, "Header value cannot be null");
        this.headers.put(name, value);
        return this;
    }
    
    /**
     * 複数のHTTPヘッダーを一度に追加します
     * 
     * @param headers 追加するヘッダーのマップ
     * @return このビルダーインスタンス
     * @throws NullPointerException headersがnullの場合
     */
    public HttpRequestBuilder headers(Map<String, String> headers) {
        Objects.requireNonNull(headers, "Headers map cannot be null");
        this.headers.putAll(headers);
        return this;
    }
    
    /**
     * クエリパラメータを追加します
     * 
     * @param name パラメータ名
     * @param value パラメータ値
     * @return このビルダーインスタンス
     * @throws NullPointerException nameまたはvalueがnullの場合
     */
    public HttpRequestBuilder queryParam(String name, String value) {
        Objects.requireNonNull(name, "Query parameter name cannot be null");
        Objects.requireNonNull(value, "Query parameter value cannot be null");
        this.queryParams.put(name, value);
        return this;
    }
    
    /**
     * リクエストボディを設定します
     * 
     * @param body リクエストボディ
     * @return このビルダーインスタンス
     */
    public HttpRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }
    
    /**
     * リクエストタイムアウトを設定します
     * 
     * @param timeout タイムアウト時間
     * @return このビルダーインスタンス
     * @throws NullPointerException timeoutがnullの場合
     * @throws IllegalArgumentException timeoutが負の値の場合
     */
    public HttpRequestBuilder timeout(Duration timeout) {
        Objects.requireNonNull(timeout, "Timeout cannot be null");
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout cannot be negative");
        }
        this.timeout = timeout;
        return this;
    }
    
    /**
     * HttpRequestオブジェクトを構築します
     * 
     * @return 構築されたHttpRequestオブジェクト
     * @throws IllegalStateException URLが設定されていない場合
     */
    public HttpRequest build() {
        if (url == null) {
            throw new IllegalStateException("URL is required");
        }
        return new HttpRequest(method, url, headers, queryParams, body, timeout);
    }
}