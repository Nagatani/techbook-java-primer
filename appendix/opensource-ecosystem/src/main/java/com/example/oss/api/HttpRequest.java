package com.example.oss.api;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * イミュータブルなHTTPリクエストを表現するクラス
 * 
 * <p>このクラスはHTTPリクエストの全ての情報を保持します。
 * イミュータブルな設計により、スレッドセーフで安全に使用できます。
 * 
 * @since 1.0.0
 */
public final class HttpRequest {
    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final Object body;
    private final Duration timeout;
    
    /**
     * HttpRequestを構築します
     * 
     * @param method HTTPメソッド
     * @param url URL
     * @param headers HTTPヘッダー
     * @param queryParams クエリパラメータ
     * @param body リクエストボディ
     * @param timeout タイムアウト
     */
    public HttpRequest(String method, String url, Map<String, String> headers,
                      Map<String, String> queryParams, Object body, Duration timeout) {
        this.method = Objects.requireNonNull(method, "Method cannot be null");
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        this.headers = Collections.unmodifiableMap(Map.copyOf(headers));
        this.queryParams = Collections.unmodifiableMap(Map.copyOf(queryParams));
        this.body = body;
        this.timeout = Objects.requireNonNull(timeout, "Timeout cannot be null");
    }
    
    /**
     * HTTPメソッドを取得します
     * 
     * @return HTTPメソッド
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * URLを取得します
     * 
     * @return URL
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * HTTPヘッダーを取得します
     * 
     * @return 変更不可能なヘッダーマップ
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    /**
     * クエリパラメータを取得します
     * 
     * @return 変更不可能なクエリパラメータマップ
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
    
    /**
     * リクエストボディを取得します
     * 
     * @return リクエストボディ（nullの可能性あり）
     */
    public Object getBody() {
        return body;
    }
    
    /**
     * タイムアウトを取得します
     * 
     * @return タイムアウト時間
     */
    public Duration getTimeout() {
        return timeout;
    }
    
    /**
     * クエリパラメータを含む完全なURLを構築します
     * 
     * @return クエリパラメータを含むURL
     */
    public String getFullUrl() {
        if (queryParams.isEmpty()) {
            return url;
        }
        
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        
        queryParams.forEach((key, value) -> {
            sb.append(key).append("=").append(value).append("&");
        });
        
        // 最後の&を削除
        sb.setLength(sb.length() - 1);
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("HttpRequest{method='%s', url='%s', headers=%d, queryParams=%d, hasBody=%s, timeout=%s}",
                method, url, headers.size(), queryParams.size(), body != null, timeout);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(method, that.method) &&
               Objects.equals(url, that.url) &&
               Objects.equals(headers, that.headers) &&
               Objects.equals(queryParams, that.queryParams) &&
               Objects.equals(body, that.body) &&
               Objects.equals(timeout, that.timeout);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(method, url, headers, queryParams, body, timeout);
    }
}