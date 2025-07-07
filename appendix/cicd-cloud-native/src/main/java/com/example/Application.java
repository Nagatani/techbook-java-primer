package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * クラウドネイティブマイクロサービスアプリケーション
 * 
 * このアプリケーションは以下の機能を実装しています：
 * - RESTful API
 * - データベース連携（PostgreSQL）
 * - キャッシュ（Redis）
 * - 非同期処理
 * - スケジューリング
 * - ヘルスチェック
 * - メトリクス収集
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}