# 第12章 応用課題

## 学習目標
- 例外処理の高度な活用
- 堅牢なシステム設計
- エラー回復メカニズム
- フォールトトレラント設計
- 信頼性の高いアプリケーション

## 課題一覧

### 課題1: 高可用性銀行システム
**ファイル名**: `HighAvailabilityBankingSystem.java`

99.99%の可用性を保証する銀行システムを作成してください。

**要求仕様**:
- 完全な例外処理とエラー回復
- 自動フェイルオーバー機能
- データ整合性の保証
- 監査ログの完全性

**信頼性機能**:
- 分散トランザクション処理
- 自動バックアップ・復旧
- リアルタイム監視
- セキュリティ侵害検知

**実行例**:
```
=== 高可用性銀行システム ===

SecureBank Enterprise v8.0

=== システム初期化 ===
高可用性アーキテクチャ:

システム構成:
プライマリDB: 東京データセンター
セカンダリDB: 大阪データセンター (ホットスタンバイ)
バックアップDB: クラウド (コールドスタンバイ)
キャッシュ層: Redis Cluster (5ノード)

可用性設計:
目標稼働率: 99.99% (年間52分以内停止)
RPO: 0秒 (データ損失許容なし)
RTO: 30秒 (復旧時間目標)
データ複製: 3拠点同期レプリケーション

例外処理アーキテクチャ:
階層化例外: ビジネス例外 → システム例外 → インフラ例外
自動復旧: 可能な限り自動回復
graceful degradation: 段階的機能縮退
Circuit Breaker: 連鎖障害防止

=== 口座操作 ===
堅牢な取引処理:

取引例: 振込処理
送金者: 山田太郎 (口座: 1234567890)
受取者: 佐藤花子 (口座: 0987654321)  
金額: ¥500,000
取引ID: TXN20240705143000001

分散トランザクション実行:
```java
try {
    // Phase 1: 分散トランザクション開始
    TransactionManager txManager = getTransactionManager();
    DistributedTransaction dtx = txManager.begin();
    
    // 送金者口座からの出金
    AccountService senderService = getAccountService("TOKYO");
    senderService.withdraw("1234567890", 500000, dtx);
    
    // 受取者口座への入金
    AccountService receiverService = getAccountService("OSAKA");  
    receiverService.deposit("0987654321", 500000, dtx);
    
    // 手数料処理
    FeeService feeService = getFeeService();
    feeService.chargeFee("1234567890", 200, dtx);
    
    // 2PC Commit実行
    dtx.prepare(); // 全サービスでprepare
    dtx.commit();  // 全サービスでcommit
    
    log.info("振込取引完了: {}", "TXN20240705143000001");
    
} catch (InsufficientFundsException e) {
    // ビジネス例外: 残高不足
    dtx.rollback();
    auditLogger.logFailedTransaction("TXN20240705143000001", 
        "insufficient_funds", e);
    notifyCustomer("1234567890", "振込失敗: 残高不足");
    return TransactionResult.failed("残高不足のため振込できません");
    
} catch (AccountNotFoundException e) {
    // ビジネス例外: 口座不存在
    dtx.rollback();
    auditLogger.logFailedTransaction("TXN20240705143000001", 
        "account_not_found", e);
    return TransactionResult.failed("指定口座が見つかりません");
    
} catch (NetworkTimeoutException e) {
    // インフラ例外: ネットワーク問題
    log.warn("ネットワークタイムアウト発生、自動復旧試行: {}", e.getMessage());
    
    // 自動復旧メカニズム
    try {
        // 代替ルートで再実行
        AlternativeNetworkManager altNetwork = getAlternativeNetwork();
        return retryTransactionWithAlternative(dtx, altNetwork);
        
    } catch (Exception retryException) {
        // 復旧失敗、システム管理者に通知
        dtx.rollback();
        alertSystemAdministrator("CRITICAL", "振込取引復旧失敗", 
            Arrays.asList(e, retryException));
        return TransactionResult.failed("システム障害により取引を完了できません");
    }
    
} catch (DatabaseConnectionException e) {
    // インフラ例外: DB接続問題
    log.error("データベース接続障害検出: {}", e.getMessage());
    
    // 自動フェイルオーバー
    try {
        DatabaseFailoverManager failover = getDatabaseFailover();
        boolean failoverSuccess = failover.switchToSecondary();
        
        if (failoverSuccess) {
            log.info("セカンダリDBへのフェイルオーバー成功");
            return retryTransactionOnSecondary(dtx);
        } else {
            throw new SystemUnavailableException("フェイルオーバー失敗");
        }
        
    } catch (SystemUnavailableException sue) {
        // 完全システム障害
        dtx.rollback();
        activateDisasterRecoveryMode();
        return TransactionResult.failed("システムメンテナンス中です");
    }
    
} catch (SecurityException e) {
    // セキュリティ例外: 不正アクセス検知
    dtx.rollback();
    securityIncidentManager.reportIncident(
        SecurityIncident.builder()
            .type("UNAUTHORIZED_TRANSACTION")
            .severity("HIGH")
            .sourceAccount("1234567890")
            .details(e.getMessage())
            .timestamp(Instant.now())
            .build());
    
    // アカウント一時ロック
    accountSecurityService.temporaryLock("1234567890", Duration.ofMinutes(30));
    
    return TransactionResult.failed("セキュリティ検証に失敗しました");
    
} catch (Exception e) {
    // 予期しない例外
    dtx.rollback();
    UnexpectedErrorHandler errorHandler = getUnexpectedErrorHandler();
    ErrorReport report = errorHandler.handleUnexpectedError(e);
    
    // 緊急システム診断
    SystemDiagnostics diagnostics = performEmergencyDiagnostics();
    
    return TransactionResult.failed("予期しないエラーが発生しました");
}
```

処理結果:
○ 振込取引完了
実行時間: 1.23秒
使用リソース: CPU 12%, Memory 145MB
整合性チェック: 完全
監査ログ: 記録完了

=== 障害対応システム ===
注意: 自動障害検知・回復:

障害シナリオ1: データベース障害
発生時刻: 14:30:45.123
障害種別: プライマリDB接続タイムアウト
影響範囲: 全取引システム

自動対応シーケンス:
```
14:30:45.123 - DB接続タイムアウト検知
14:30:45.145 - ヘルスチェック実行 → 応答なし
14:30:45.167 - フェイルオーバー判定 → 実行決定
14:30:45.234 - セカンダリDBアクティベート
14:30:45.456 - アプリケーション接続切替
14:30:45.678 - ロードバランサ設定更新
14:30:45.789 - 監視システム状態更新
14:30:45.890 - 運用チーム自動通知
```

フェイルオーバー結果:
○ セカンダリDBへの切替完了
切替時間: 0.767秒 (目標30秒以内)
データ損失: 0件 (同期レプリケーション)
サービス継続: 中断なし

障害シナリオ2: ネットワーク分断
発生時刻: 16:22:33.567
障害種別: 東京-大阪間ネットワーク切断
影響範囲: 拠点間通信

自動対応:
```java
try {
    // 通常ルートでの通信試行
    RemoteService osaka = getRemoteService("OSAKA");
    TransactionResult result = osaka.processTransaction(transaction);
    
} catch (NetworkPartitionException e) {
    log.warn("ネットワーク分断検出: {}", e.getMessage());
    
    // Split-brain防止メカニズム
    ConsensusManager consensus = getConsensusManager();
    boolean isMajorityPartition = consensus.checkMajorityPartition();
    
    if (isMajorityPartition) {
        // 多数派パーティションとして動作継続
        log.info("多数派パーティションとして動作継続");
        
        // 代替通信経路の確立
        AlternativeRoute altRoute = findAlternativeRoute("OSAKA");
        if (altRoute != null) {
            RemoteService osakaAlt = getRemoteServiceViaAlternative(altRoute);
            return osakaAlt.processTransaction(transaction);
        }
        
        // 代替ルートなし、ローカル処理継続
        return processLocallyWithEventualConsistency(transaction);
        
    } else {
        // 少数派パーティション、読み取り専用モード
        log.warn("少数派パーティション、読み取り専用モードに移行");
        activateReadOnlyMode();
        throw new ServiceDegradedException("現在、参照のみ利用可能です");
    }
}
```

=== セキュリティ侵害対応 ===
インシデント自動対応:

侵害検知例:
検知時刻: 18:45:12.789
検知種別: 異常ログイン パターン
詳細: 1分間に50回の失敗ログイン
対象アカウント: 1234567890

自動対応実行:
```java
@SecurityIncidentHandler
public void handleSuspiciousActivity(SecurityIncident incident) {
    try {
        // インシデント重要度評価
        SecuritySeverity severity = evaluateIncidentSeverity(incident);
        
        switch (severity) {
            case CRITICAL:
                // 即座の保護措置
                securityService.emergencyLockAccount(incident.getAccountId());
                
                // 関連アカウントの調査
                List<String> relatedAccounts = findRelatedAccounts(incident.getAccountId());
                for (String account : relatedAccounts) {
                    securityService.enhanceMonitoring(account);
                }
                
                // 法執行機関通知
                lawEnforcementNotifier.reportCriticalIncident(incident);
                break;
                
            case HIGH:
                // アカウント一時制限
                securityService.temporaryRestriction(incident.getAccountId(), 
                    Duration.ofHours(1));
                
                // 追加認証要求
                authenticationService.requireStepUp(incident.getAccountId());
                break;
                
            case MEDIUM:
                // 監視強化
                securityService.enhanceMonitoring(incident.getAccountId());
                
                // ユーザー通知
                notificationService.sendSecurityAlert(incident.getAccountId());
                break;
        }
        
        // インシデントログ記録
        auditLogger.logSecurityIncident(incident, severity);
        
    } catch (SecurityServiceException e) {
        // セキュリティサービス障害
        log.error("セキュリティ対応中にエラー発生: {}", e.getMessage());
        
        // 緊急時フォールバック
        emergencySecurityProtocol.activate();
        
        // 全アカウント一時停止
        securityService.emergencyFreezeAllAccounts();
        
        // 緊急対応チーム召集
        emergencyResponseTeam.mobilize(incident, e);
        
    } catch (Exception e) {
        // 予期しないエラー
        log.error("セキュリティインシデント処理で予期しないエラー: {}", e.getMessage());
        
        // システム全体の緊急停止
        systemShutdownManager.emergencyShutdown();
    }
}
```

=== バックアップ・復旧システム ===
データ保護・復旧:

バックアップ戦略:
リアルタイム同期: プライマリ ↔ セカンダリ
増分バックアップ: 15分間隔
フルバックアップ: 日次 (03:00 JST)
オフサイトバックアップ: 週次 (クラウド)

災害復旧訓練:
シナリオ: データセンター完全停止
実行日時: 2024-07-05 02:00-06:00

復旧手順実行:
```
02:00:00 - 災害復旧訓練開始
02:00:15 - プライマリシステム停止
02:00:30 - 災害検知・判定
02:00:45 - DR サイト起動指示
02:02:30 - バックアップデータ復元開始
02:45:00 - データ復元完了
02:47:30 - アプリケーション起動
02:50:00 - 整合性チェック実行
02:55:00 - 動作確認テスト
03:30:00 - サービス再開
```

復旧結果:
目標復旧時間: 4時間以内
実際復旧時間: 1時間30分 ○
データ損失: 0件 ○
整合性: 100% ○

復旧検証:
```java
@DisasterRecoveryTest
public void validateSystemRecovery() {
    try {
        // データ整合性検証
        DataIntegrityValidator validator = new DataIntegrityValidator();
        IntegrityReport report = validator.validateAllAccounts();
        
        if (!report.isValid()) {
            throw new DataCorruptionException("データ整合性エラー検出: " + 
                report.getCorruptedAccounts());
        }
        
        // 機能動作確認
        FunctionalTester tester = new FunctionalTester();
        TestResult testResult = tester.runCriticalPathTests();
        
        if (!testResult.allPassed()) {
            throw new SystemValidationException("機能テスト失敗: " + 
                testResult.getFailedTests());
        }
        
        // パフォーマンス確認
        PerformanceTester perfTester = new PerformanceTester();
        PerformanceMetrics metrics = perfTester.measureSystemPerformance();
        
        if (metrics.getAverageResponseTime() > 2.0) {
            log.warn("パフォーマンス劣化検出: {}秒", metrics.getAverageResponseTime());
            performanceOptimizer.optimize();
        }
        
        log.info("災害復旧システム検証完了");
        
    } catch (Exception e) {
        log.error("災害復旧検証失敗: {}", e.getMessage());
        // 再復旧プロセス開始
        initiateSecondaryRecoveryProcess();
    }
}
```

=== 監視・アラートシステム ===
リアルタイム健全性監視:

監視メトリクス:
システム性能: CPU, メモリ, ディスク, ネットワーク
アプリケーション: レスポンス時間, エラー率, スループット
ビジネス: 取引成功率, 売上, 顧客満足度
セキュリティ: 不正アクセス, 侵害試行, 異常行動

アラート設定:
Critical: 即座対応 (SMS + 電話)
High: 15分以内対応 (Email + Slack)
Medium: 1時間以内対応 (Email)
Low: 日次レビュー (Dashboard)

アラート例:
```
時刻: 2024-07-05 14:30:45
重要度: CRITICAL
メトリクス: 取引成功率
現在値: 94.2%
閾値: 95.0%
継続時間: 5分
影響範囲: 全システム

自動対応:
1. インシデント作成: INC-20240705-001
2. オンコール担当者通知: 田中エンジニア
3. エスカレーション設定: 15分無応答で上位者通知
4. 一次対応指示: パフォーマンス最適化実行
```

=== システム性能統計 ===
可用性・信頼性指標:

可用性実績 (過去12ヶ月):
月次可用性: 99.98% (目標 99.99%)
年間ダウンタイム: 87分 (目標 52分)
計画停止: 60分 (メンテナンス)
障害停止: 27分 (3件)

障害分析:
障害1: DB接続プール枯渇 (15分)
障害2: ネットワーク機器故障 (8分)  
障害3: アプリケーションメモリリーク (4分)

復旧時間分析:
平均復旧時間: 9分
目標復旧時間: 30分
復旧成功率: 100%

データ整合性:
整合性チェック: 日次実行
検出エラー: 0件 (過去12ヶ月)
自動修復: 対象外
手動修復: 0件

セキュリティ指標:
セキュリティ侵害: 0件
侵害試行: 125,678件 (すべてブロック)
誤検知率: 0.02%
検知精度: 99.98%

顧客影響:
影響顧客数: 0人 (障害時も継続サービス)
苦情件数: 3件 (レスポンス遅延)
満足度: 4.8/5.0
NPS: +67
```

**評価ポイント**:
- 例外処理の網羅的実装
- 自動復旧メカニズムの設計
- データ整合性の保証
- 高可用性アーキテクチャ



### 課題2: 自動運転車制御システム
**ファイル名**: `AutonomousVehicleControlSystem.java`

生命に関わる自動運転車の制御システムを作成してください。

**要求仕様**:
- リアルタイム安全制御
- 多重化された安全機構
- 緊急時自動対応
- 完全な故障許容性

**安全機能**:
- 衝突回避システム
- 冗長化制御システム
- 安全停止機能
- 遠隔監視・制御

**実行例**:
```
=== 自動運転車制御システム ===

SafeDrive Autonomous v12.0

=== 安全システム初期化 ===
多重安全アーキテクチャ:

制御システム構成:
プライマリECU: NVIDIA Drive PX (AI処理)
セーフティECU: Intel Atom (安全監視)
バックアップECU: ARM Cortex (緊急制御)
ウォッチドッグ: 独立監視システム

センサー冗長化:
LiDAR: 4台 (360度カバー)
カメラ: 8台 (ステレオビジョン)
レーダー: 6台 (全方位)
超音波: 12台 (近接)
IMU: 3台 (姿勢検出)

通信システム:
V2V: 車車間通信
V2I: 路側インフラ通信
V2N: ネットワーク通信
緊急通信: 専用チャネル

=== リアルタイム制御 ===
安全クリティカル制御:

制御ループ実行:
制御周期: 10ms (100Hz)
安全監視: 1ms (1kHz)
センサー融合: 20ms (50Hz)
経路計画: 100ms (10Hz)

走行シナリオ: 高速道路走行
現在速度: 80km/h
目標速度: 100km/h  
車線: 中央車線
前方車両: 120m先、同速度

制御実行コード:
```java
@RealTimeController
@SafetyCritical(level = SIL_D) // 最高安全レベル
public class AutonomousVehicleController {
    
    private final MultipleECUManager ecuManager;
    private final SensorFusionEngine sensorFusion;
    private final SafetyMonitor safetyMonitor;
    private final EmergencyBrakingSystem emergencyBraking;
    
    @ExecuteEvery(10, TimeUnit.MILLISECONDS)
    public void controlLoop() {
        try {
            // センサーデータ取得
            SensorData primarySensors = sensorFusion.getPrimarySensorData();
            SensorData backupSensors = sensorFusion.getBackupSensorData();
            
            // センサー整合性チェック
            validateSensorConsistency(primarySensors, backupSensors);
            
            // 環境認識
            EnvironmentModel environment = perceiveEnvironment(primarySensors);
            
            // 安全性評価
            SafetyAssessment safety = safetyMonitor.assessSituation(environment);
            
            if (safety.isSafe()) {
                // 通常制御実行
                executeNormalControl(environment);
            } else {
                // 安全制御実行
                executeSafetyControl(safety);
            }
            
        } catch (SensorFailureException e) {
            handleSensorFailure(e);
        } catch (ActuatorFailureException e) {
            handleActuatorFailure(e);
        } catch (CommunicationException e) {
            handleCommunicationFailure(e);
        } catch (Exception e) {
            // 予期しない例外 → 緊急停止
            executeEmergencyStop(e);
        }
    }
    
    private void handleSensorFailure(SensorFailureException e) {
        log.error("センサー故障検出: {}", e.getFailedSensor());
        
        try {
            // 冗長センサーでの継続動作判定
            RedundancyManager redundancy = getRedundancyManager();
            boolean canContinue = redundancy.canOperateWithFailedSensor(e.getFailedSensor());
            
            if (canContinue) {
                // 冗長系での継続動作
                sensorFusion.switchToRedundantSensor(e.getFailedSensor());
                log.info("冗長センサーに切り替え、動作継続");
                
                // 性能低下モード
                activatePerformanceDegradedMode();
                
            } else {
                // 安全な場所への自動退避
                log.warn("必要センサー不足、安全退避を実行");
                executeSafeEvacuation();
            }
            
        } catch (RedundancySystemException rse) {
            // 冗長系も故障
            log.error("冗長システム故障: {}", rse.getMessage());
            executeEmergencyStop(rse);
        }
    }
    
    private void handleActuatorFailure(ActuatorFailureException e) {
        log.error("アクチュエータ故障: {}", e.getFailedActuator());
        
        try {
            switch (e.getFailedActuator()) {
                case STEERING:
                    // ステアリング故障
                    if (hasBackupSteering()) {
                        switchToBackupSteering();
                        log.info("バックアップステアリングに切替");
                    } else {
                        // ステアリング不可、緊急停止
                        executeControlledEmergencyStop();
                    }
                    break;
                    
                case BRAKING:
                    // ブレーキ故障
                    if (hasRegenerativeBraking() || hasEmergencyBraking()) {
                        switchToAlternativeBraking();
                        log.info("代替ブレーキシステムに切替");
                    } else {
                        // ブレーキ完全故障、エンジンブレーキ使用
                        useEngineAndFrictionBraking();
                    }
                    break;
                    
                case ACCELERATION:
                    // アクセル故障
                    setConstantSpeed();
                    findSafeStoppingLocation();
                    log.info("定速走行で安全停止場所へ移動");
                    break;
            }
            
        } catch (BackupSystemException bse) {
            // バックアップシステムも故障
            log.error("バックアップシステム故障: {}", bse.getMessage());
            executeEmergencyStop(bse);
        }
    }
    
    private void executeEmergencyStop(Exception cause) {
        log.error("緊急停止実行: {}", cause.getMessage());
        
        try {
            // 緊急灯・警告灯点灯
            activateEmergencyLights();
            
            // 緊急ブレーキ作動
            emergencyBraking.activate();
            
            // ハザード通信
            broadcastEmergencySignal();
            
            // 緊急通報
            callEmergencyServices();
            
            // ブラックボックス保存
            saveBlackBoxData(cause);
            
            log.info("緊急停止処理完了");
            
        } catch (Exception e) {
            // 緊急停止処理も失敗
            log.fatal("緊急停止処理失敗: {}", e.getMessage());
            // 物理的強制停止
            physicalEmergencyShutdown();
        }
    }
}
```

走行状況監視:
```
時刻: 14:30:15.123
位置: 35.6895°N, 139.6917°E
速度: 82.3 km/h
加速度: +0.2 m/s²
舵角: +2.3°
ブレーキ: 0%

前方車両検知:
距離: 118.5m
相対速度: -1.2 km/h (接近)
衝突予測時間: 125.3秒 (安全)

制御判定: 通常走行継続
```

=== 危険回避システム ===
注意: 衝突回避制御:

危険シナリオ: 前方急ブレーキ
検知時刻: 14:35:22.456
前方車両状況: 急減速 (-8.5 m/s²)
相対距離: 45.2m
衝突予測時間: 3.2秒

自動回避制御:
```java
@EmergencyController
public void handleCollisionThreat(CollisionThreat threat) {
    try {
        log.warn("衝突危険検知: TTC={}秒", threat.getTimeToCollision());
        
        // 回避戦略の選択
        AvoidanceStrategy strategy = selectOptimalAvoidanceStrategy(threat);
        
        switch (strategy.getType()) {
            case EMERGENCY_BRAKING:
                // 緊急ブレーキ
                emergencyBraking.fullBraking();
                log.info("緊急ブレーキ実行");
                break;
                
            case LANE_CHANGE:
                // 緊急車線変更
                if (isLaneChangeSafe(strategy.getTargetLane())) {
                    executeEmergencyLaneChange(strategy.getTargetLane());
                    log.info("緊急車線変更実行: {}", strategy.getTargetLane());
                } else {
                    // 車線変更不可、ブレーキにフォールバック
                    emergencyBraking.fullBraking();
                }
                break;
                
            case SHOULDER_EVACUATION:
                // 路肩退避
                executeShoulderEvacuation();
                log.info("路肩への緊急退避実行");
                break;
                
            case CONTROLLED_COLLISION:
                // 制御衝突（最後の手段）
                prepareForControlledCollision();
                log.warn("制御衝突準備実行");
                break;
        }
        
        // V2V通信で他車に警告
        broadcastEmergencyWarning(threat);
        
    } catch (BrakingSystemFailureException e) {
        // ブレーキ故障時の代替制御
        try {
            useRegenerativeBraking();
            useEngineCompression();
            log.error("主ブレーキ故障、代替制動使用: {}", e.getMessage());
        } catch (Exception fallbackException) {
            log.fatal("全制動システム故障: {}", fallbackException.getMessage());
            // 物理的緊急措置
            activatePhysicalEmergencyMeasures();
        }
        
    } catch (SteeringFailureException e) {
        // ステアリング故障時
        try {
            if (hasLimitedSteeringCapability()) {
                // 制限的操舵で直進維持
                maintainStraightPath();
                emergencyBraking.fullBraking();
            } else {
                // 操舵不可、直進緊急停止
                emergencyBraking.fullBraking();
            }
        } catch (Exception steeringFallback) {
            log.fatal("操舵系完全故障: {}", steeringFallback.getMessage());
            executeLastResortProcedures();
        }
        
    } catch (Exception e) {
        // その他の予期しない故障
        log.fatal("回避制御中に予期しない故障: {}", e.getMessage());
        executeFailsafeProtocol();
    }
}
```

回避結果:
実行戦略: 緊急ブレーキ + 車線変更
制動距離: 28.5m
最終停止距離: 前方車両まで 16.7m
結果: 衝突回避成功 ○

=== システム故障対応 ===
多重故障対応:

故障シナリオ: 複合故障
1. メインECU故障 (CPU過熱)
2. LiDAR#1故障 (レーザー出力低下)
3. 通信システム遅延 (ネットワーク輻輳)

故障対応シーケンス:
```
14:40:15.123 - メインECU温度異常検知 (85°C)
14:40:15.145 - 処理能力低下開始
14:40:15.234 - セーフティECUが制御権取得
14:40:15.456 - LiDAR#1故障検知
14:40:15.567 - 冗長LiDAR#2,#3,#4に切替
14:40:15.678 - 通信遅延検知 (>100ms)
14:40:15.789 - ローカル制御モードに移行
14:40:16.000 - 安全確保後、路肩退避開始
```

フェイルセーフ制御:
```java
@FailsafeController
public void handleMultipleFailures(List<SystemFailure> failures) {
    try {
        // 故障重要度評価
        FailureSeverity overallSeverity = assessOverallSeverity(failures);
        
        // システム能力評価
        SystemCapability remainingCapability = assessRemainingCapability(failures);
        
        if (remainingCapability.canSafelyOperate()) {
            // 制限モードで継続
            activateLimitedOperationMode(remainingCapability);
            findNearestServiceStation();
            
        } else if (remainingCapability.canSafelyStop()) {
            // 安全停止実行
            executeSafeStoppingProcedure();
            
        } else {
            // 即座停止必要
            executeImmediateEmergencyStop();
        }
        
        // 故障情報記録
        logMultipleFailures(failures);
        
        // 遠隔監視センターに通報
        notifyRemoteOperationsCenter(failures, remainingCapability);
        
    } catch (FailsafeSystemException e) {
        // フェイルセーフシステム自体の故障
        log.fatal("フェイルセーフシステム故障: {}", e.getMessage());
        
        // 最終安全措置
        executeUltimateFailsafe();
    }
}

private void executeUltimateFailsafe() {
    // 全システム停止
    shutdownAllNonEssentialSystems();
    
    // 機械的ブレーキ作動
    activateMechanicalBrakes();
    
    // 乗員保護
    activateOccupantProtection();
    
    // 緊急信号送信
    sendDistressSignal();
    
    log.info("最終安全措置実行完了");
}
```

=== 遠隔監視・介入 ===
リモートオペレーション:

遠隔監視体制:
監視センター: 24時間365日体制
監視車両数: 10,000台同時
オペレーター: 50名 (3交代制)
平均応答時間: 2.3秒

遠隔介入例:
車両ID: AV-12345
発生事象: センサー故障による判断困難
位置: 首都高速湾岸線
状況: 工事区間での複雑な交通状況

遠隔介入実行:
```java
@RemoteOperation
public void handleRemoteIntervention(RemoteInterventionRequest request) {
    try {
        // 車両状態の詳細取得
        VehicleStatus status = getDetailedVehicleStatus(request.getVehicleId());
        
        // 遠隔監視画面への映像配信
        VideoStream stream = activateHighResolutionCamera(request.getVehicleId());
        remoteOperationCenter.displayVehicleView(stream);
        
        // オペレーター指示待機
        while (!request.isCompleted()) {
            OperatorCommand command = remoteOperationCenter.getNextCommand();
            
            switch (command.getType()) {
                case TAKE_MANUAL_CONTROL:
                    // 完全遠隔制御
                    takeRemoteControl(request.getVehicleId());
                    log.info("車両 {} の遠隔制御開始", request.getVehicleId());
                    break;
                    
                case PROVIDE_GUIDANCE:
                    // 自動運転への指示提供
                    provideNavigationGuidance(command.getGuidance());
                    break;
                    
                case SAFE_STOP:
                    // 安全停止指示
                    executeSafeStopUnderRemoteGuidance();
                    break;
                    
                case TRANSFER_TO_LOCAL:
                    // 地域オペレーターに転送
                    transferToLocalOperator(command.getLocalOperatorId());
                    break;
            }
        }
        
    } catch (RemoteConnectionException e) {
        // 通信切断
        log.error("遠隔通信切断: {}", e.getMessage());
        
        // 自律フォールバック
        activateAutonomousFallback();
        
    } catch (RemoteControlSystemException e) {
        // 遠隔制御システム故障
        log.error("遠隔制御システム故障: {}", e.getMessage());
        
        // ローカル緊急制御
        executeLocalEmergencyControl();
    }
}
```

=== 安全性統計 ===
安全性能指標:

走行実績 (過去12ヶ月):
総走行距離: 50億km
総走行時間: 125万時間
事故件数: 0件 ○
インシデント: 23件 (すべて安全に回避)

故障対応実績:
センサー故障: 145件 → 100%安全処理
アクチュエータ故障: 67件 → 100%安全処理
ECU故障: 23件 → 100%安全処理
通信故障: 234件 → 100%自律制御継続

緊急回避実績:
衝突回避: 1,234件 → 100%成功
緊急ブレーキ: 567件 → 100%効果的
車線変更回避: 345件 → 100%成功
路肩退避: 123件 → 100%安全実行

人間運転比較:
事故率: 1/100 (人間運転比)
重大インシデント率: 1/1000 (人間運転比)
燃費効率: +35% (最適化運転)
交通流効率: +25% (協調運転)

システム信頼性:
MTBF (平均故障間隔): 100,000時間
MTTR (平均修復時間): 0.5時間
可用性: 99.999%
安全度水準: SIL 4 (最高レベル)
```

**評価ポイント**:
- 安全クリティカル例外処理
- 多重故障対応能力
- リアルタイム制御実装
- フェイルセーフ設計



### 課題3: 医療情報統合システム
**ファイル名**: `MedicalInformationSystem.java`

生命に関わる医療情報を管理するシステムを作成してください。

**要求仕様**:
- 患者安全優先の例外処理
- 医療データの完全性保証
- 緊急時即座アクセス
- 法的コンプライアンス

**医療機能**:
- 電子カルテシステム
- 薬剤投与管理
- 緊急時情報提供
- 医療品質管理

**実行例**:
```
=== 医療情報統合システム ===

MediCare Integrated System v15.0

=== 患者安全システム ===
薬剤安全管理:

患者情報: 田中太郎 (ID: P12345678)
年齢: 45歳, 男性
身長: 170cm, 体重: 70kg
アレルギー: ペニシリン系, 造影剤
既往歴: 高血圧, 糖尿病

処方例: 感染症治療
処方医: 山田内科医 (ID: D5678)
診断: 急性気管支炎
処方薬: セフェム系抗生物質

薬剤安全チェック:
```java
@MedicalSafetySystem
@CriticalPatientSafety
public class DrugSafetyManager {
    
    public PrescriptionResult prescribeMedication(
            Patient patient, 
            Medication medication, 
            Dosage dosage,
            Doctor prescriber) {
        
        try {
            // 基本安全性チェック
            performBasicSafetyChecks(patient, medication, dosage);
            
            // アレルギーチェック
            checkAllergyCompatibility(patient, medication);
            
            // 薬剤相互作用チェック  
            checkDrugInteractions(patient, medication);
            
            // 用量適正性チェック
            validateDosage(patient, medication, dosage);
            
            // 処方権限チェック
            validatePrescriptionAuthority(prescriber, medication);
            
            // 保険適用チェック
            checkInsuranceCoverage(patient, medication);
            
            // 処方実行
            Prescription prescription = createPrescription(patient, medication, dosage, prescriber);
            
            // 服薬指導情報生成
            MedicationGuidance guidance = generateMedicationGuidance(prescription);
            
            // 調剤システムに送信
            pharmacySystem.sendPrescription(prescription);
            
            log.info("処方完了: Patient={}, Medication={}", 
                patient.getId(), medication.getName());
            
            return PrescriptionResult.success(prescription, guidance);
            
        } catch (AllergyConflictException e) {
            // アレルギー禁忌
            log.error("アレルギー禁忌検出: Patient={}, Allergen={}", 
                patient.getId(), e.getAllergen());
            
            // 代替薬検索
            try {
                List<Medication> alternatives = findAlternativeMedications(
                    medication, patient.getAllergies());
                    
                if (!alternatives.isEmpty()) {
                    MedicationRecommendation recommendation = 
                        new MedicationRecommendation(alternatives, 
                            "アレルギー回避のため代替薬を推奨");
                    
                    return PrescriptionResult.rejected(e.getMessage(), recommendation);
                } else {
                    // 代替薬なし、専門医紹介
                    SpecialistReferral referral = createSpecialistReferral(
                        patient, medication, "アレルギー専門医への紹介");
                    
                    return PrescriptionResult.requiresSpecialist(e.getMessage(), referral);
                }
                
            } catch (DrugDatabaseException dbe) {
                // 薬剤DB障害
                emergencyDrugSafetyProtocol.activate(patient, medication, e);
                return PrescriptionResult.systemError("薬剤安全性確認システム障害");
            }
            
        } catch (DrugInteractionException e) {
            // 薬剤相互作用
            log.warn("薬剤相互作用検出: {}", e.getInteractionDetails());
            
            InteractionSeverity severity = e.getSeverity();
            switch (severity) {
                case CONTRAINDICATED:
                    // 絶対禁忌
                    return PrescriptionResult.rejected(
                        "重篤な薬剤相互作用のため処方できません: " + e.getMessage());
                    
                case MAJOR:
                    // 重要な相互作用、慎重投与必要
                    CautiousMonitoring monitoring = createCautiousMonitoring(e);
                    return PrescriptionResult.requiresMonitoring(
                        "重要な薬剤相互作用あり。慎重な監視が必要", monitoring);
                    
                case MODERATE:
                    // 中等度相互作用、注意深い観察
                    PatientEducation education = createInteractionEducation(e);
                    return PrescriptionResult.requiresEducation(
                        "薬剤相互作用の可能性あり", education);
                    
                default:
                    // 軽微な相互作用、情報提供のみ
                    return PrescriptionResult.withWarning(
                        "軽微な薬剤相互作用の可能性があります", 
                        createPrescription(patient, medication, dosage, prescriber));
            }
            
        } catch (DosageException e) {
            // 用量エラー
            log.error("用量エラー: {}", e.getMessage());
            
            DosageRecommendation correction = calculateCorrectDosage(
                patient, medication, e.getProposedDosage());
            
            return PrescriptionResult.dosageCorrection(
                "用量調整が必要: " + e.getMessage(), correction);
                
        } catch (PrescriptionAuthorityException e) {
            // 処方権限なし
            log.error("処方権限エラー: Doctor={}, Medication={}", 
                prescriber.getId(), medication.getName());
            
            // 適切な処方権限を持つ医師の紹介
            List<Doctor> authorizedDoctors = findAuthorizedDoctors(medication);
            
            return PrescriptionResult.requiresAuthorization(
                "処方権限が必要: " + e.getMessage(), authorizedDoctors);
                
        } catch (PatientNotFoundException e) {
            // 患者情報なし
            log.error("患者情報不明: {}", e.getPatientId());
            
            // 緊急時処理判定
            if (isEmergencyCase()) {
                EmergencyTreatmentProtocol emergency = activateEmergencyProtocol();
                return PrescriptionResult.emergencyTreatment(
                    "緊急時処理として最小限の処方を実行", emergency);
            } else {
                return PrescriptionResult.failed("患者情報の確認が必要");
            }
            
        } catch (SystemUnavailableException e) {
            // システム障害
            log.error("医療システム障害: {}", e.getMessage());
            
            // 緊急時フォールバック
            try {
                OfflineEmergencySystem offline = activateOfflineMode();
                EmergencyPrescription emergencyRx = offline.createEmergencyPrescription(
                    patient, medication, dosage, prescriber);
                
                // 手動での安全確認要求
                ManualSafetyCheck manual = requestManualSafetyCheck(emergencyRx);
                
                return PrescriptionResult.manualVerificationRequired(
                    "システム障害のため手動確認が必要", manual);
                    
            } catch (OfflineSystemException ose) {
                // オフラインシステムも障害
                log.fatal("全医療システム障害: {}", ose.getMessage());
                
                // 紙ベース緊急処方
                PaperBasedPrescription paper = createPaperPrescription(
                    patient, medication, dosage, prescriber);
                
                return PrescriptionResult.paperFallback(
                    "システム完全障害、紙ベース処方に移行", paper);
            }
            
        } catch (Exception e) {
            // 予期しない例外
            log.fatal("予期しない医療システムエラー: {}", e.getMessage());
            
            // 患者安全最優先の緊急対応
            emergencyPatientSafetyProtocol.activate();
            
            // 医療安全委員会への緊急報告
            medicalSafetyCommittee.reportCriticalIncident(patient, medication, e);
            
            return PrescriptionResult.criticalError(
                "重大なシステムエラーのため医療安全委員会による確認が必要");
        }
    }
}
```

処方結果:
注意: 薬剤相互作用検出
相互作用: セフェム系 ↔ ワルファリン
重要度: MODERATE (中等度)
対応: 患者教育 + 定期検査

推奨事項:
- PT-INR値監視 (週1回)
- 出血症状の確認
- 服薬間隔の調整 (2時間以上)

=== 緊急医療対応 ===
救急医療システム:

緊急事例: 心筋梗塞疑い
患者: 佐藤花子 (ID: P87654321)
搬送時刻: 14:30:45
症状: 胸痛、冷汗、呼吸困難

緊急情報アクセス:
```java
@EmergencyMedicalSystem
public class EmergencyResponseSystem {
    
    public EmergencyTreatmentPlan handleEmergencyPatient(
            Patient patient, 
            EmergencySymptoms symptoms, 
            AmbulanceInfo ambulance) {
        
        try {
            // 緊急度判定 (Triage)
            TriageLevel triage = assessTriageLevel(symptoms);
            
            // 過去病歴の緊急取得
            MedicalHistory history = getMedicalHistory(patient.getId());
            
            // 緊急治療計画策定
            EmergencyTreatmentPlan plan = createEmergencyPlan(patient, symptoms, history, triage);
            
            // 医療チーム召集
            MedicalTeam team = assembleMedicalTeam(triage, plan);
            
            // 医療リソース確保
            MedicalResources resources = reserveEmergencyResources(plan);
            
            // 治療開始
            initiateEmergencyTreatment(plan, team, resources);
            
            return plan;
            
        } catch (PatientHistoryUnavailableException e) {
            // 病歴不明
            log.warn("患者病歴不明、症状ベース治療: {}", e.getMessage());
            
            // 症状ベース緊急治療
            SymptomBasedTreatment treatment = createSymptomBasedTreatment(symptoms);
            
            // 並行して病歴調査
            initiateParallelHistoryInvestigation(patient);
            
            return EmergencyTreatmentPlan.fromSymptoms(treatment);
            
        } catch (MedicalTeamUnavailableException e) {
            // 医療チーム不足
            log.error("医療チーム不足: {}", e.getMessage());
            
            try {
                // 他病院への転送検討
                HospitalTransfer transfer = findAlternativeHospital(triage);
                
                if (transfer != null) {
                    // 転送実行
                    initiatePatientTransfer(patient, transfer);
                    return EmergencyTreatmentPlan.transfer(transfer);
                } else {
                    // 転送先なし、限定的治療
                    LimitedTreatment limited = createLimitedTreatment(symptoms);
                    return EmergencyTreatmentPlan.limited(limited);
                }
                
            } catch (TransferSystemException tse) {
                // 転送システム障害
                log.fatal("転送システム障害: {}", tse.getMessage());
                
                // 最小限の生命維持治療
                LifeSupportTreatment lifeSupport = activateLifeSupport(patient);
                return EmergencyTreatmentPlan.lifeSupport(lifeSupport);
            }
            
        } catch (MedicalResourcesException e) {
            // 医療リソース不足
            log.error("医療リソース不足: {}", e.getMessage());
            
            // リソース優先度調整
            ResourceReallocation reallocation = reallocateResources(e.getRequiredResources());
            
            if (reallocation.isSuccessful()) {
                // リソース確保成功
                return createEmergencyPlan(patient, symptoms, 
                    getMedicalHistory(patient.getId()), triage);
            } else {
                // 代替治療法検討
                AlternativeTreatment alternative = findAlternativeTreatment(symptoms);
                return EmergencyTreatmentPlan.alternative(alternative);
            }
            
        } catch (CriticalSystemFailureException e) {
            // 重要システム障害
            log.fatal("重要医療システム障害: {}", e.getMessage());
            
            // 完全手動モード
            ManualEmergencyMode manual = activateManualEmergencyMode();
            
            // 経験豊富な医師による判断
            SeniorPhysicianConsultation consultation = 
                requestSeniorPhysicianEmergencyConsultation(patient, symptoms);
            
            return EmergencyTreatmentPlan.manual(consultation);
            
        } catch (Exception e) {
            // その他予期しない例外
            log.fatal("緊急医療システム予期しないエラー: {}", e.getMessage());
            
            // 最終安全措置
            UltimateSafetyProtocol safety = activateUltimateSafetyProtocol();
            
            // 生命維持最優先
            LifeMaintenanceProtocol lifeMaintenance = prioritizeLifeMaintenance(patient);
            
            return EmergencyTreatmentPlan.ultimateSafety(lifeMaintenance);
        }
    }
}
```

緊急対応結果:
トリアージ: レベル1 (最優先)
対応時間: 3分12秒
治療チーム: 循環器専門医、看護師4名
使用リソース: CCU、心電図、血液検査

=== 医療品質管理 ===
継続的品質改善:

品質指標監視:
患者安全: インシデント 0件/月
医療過誤: 0件/年
感染制御: 院内感染 0.02%
患者満足: 4.8/5.0

医療安全システム:
```java
@MedicalQualitySystem
public class MedicalQualityManager {
    
    public void monitorMedicalQuality() {
        try {
            // 医療品質指標の収集
            QualityMetrics metrics = collectQualityMetrics();
            
            // 異常値の検出
            List<QualityAlert> alerts = detectQualityAnomalies(metrics);
            
            for (QualityAlert alert : alerts) {
                handleQualityAlert(alert);
            }
            
            // 品質改善提案の生成
            List<QualityImprovement> improvements = generateImprovementSuggestions(metrics);
            
            // 医療安全委員会への報告
            reportToMedicalSafetyCommittee(metrics, alerts, improvements);
            
        } catch (QualityDataException e) {
            log.error("品質データ収集エラー: {}", e.getMessage());
            
            // 手動品質確認への切替
            activateManualQualityAssurance();
            
        } catch (Exception e) {
            log.fatal("品質管理システムエラー: {}", e.getMessage());
            
            // 緊急品質管理プロトコル
            activateEmergencyQualityProtocol();
        }
    }
    
    private void handleQualityAlert(QualityAlert alert) {
        switch (alert.getSeverity()) {
            case CRITICAL:
                // 即座の医療チーム通知
                emergencyNotifyMedicalTeam(alert);
                
                // 関連患者の緊急チェック
                performEmergencyPatientCheck(alert.getAffectedPatients());
                break;
                
            case HIGH:
                // 医療安全委員会通知
                notifyMedicalSafetyCommittee(alert);
                
                // 改善計画の即座策定
                createImmediateImprovementPlan(alert);
                break;
                
            case MEDIUM:
                // 部門責任者通知
                notifyDepartmentHead(alert);
                break;
        }
    }
}
```

=== システム統計 ===
医療システム性能:

患者安全指標:
医療過誤: 0件 (24ヶ月連続)
薬剤事故: 0件 (システム導入後)
アレルギー反応: 防止率 100%
適切な処方: 99.97%

システム可用性:
稼働率: 99.999% (医療特化)
平均応答時間: 0.8秒
緊急時応答: 0.2秒
データ整合性: 100%

法的コンプライアンス:
HIPAA準拠: 完全対応
個人情報保護: 違反 0件
医療記録保持: 法定期間完全管理
監査対応: 100%合格

医療効率:
診療時間短縮: 35%
薬剤処方効率: +45%
検査重複削減: 28%
医療コスト削減: 22%

患者満足度:
総合満足度: 4.8/5.0
システム使いやすさ: 4.7/5.0
情報アクセス性: 4.9/5.0
安全性信頼度: 5.0/5.0
```

**評価ポイント**:
- 患者安全最優先の例外処理
- 医療法規制対応
- 緊急時システム継続性
- 医療品質保証

## 実装のヒント

### 課題1のヒント
```java
class BankingTransactionManager {
    @Transactional
    public TransactionResult executeTransfer(TransferRequest request) {
        TransactionContext context = createTransactionContext(request);
        
        try (DistributedTransaction dtx = transactionManager.begin()) {
            // Phase 1: Validation
            validateTransferRequest(request);
            
            // Phase 2: Account Operations
            debitAccount(request.getFromAccount(), request.getAmount(), dtx);
            creditAccount(request.getToAccount(), request.getAmount(), dtx);
            
            // Phase 3: Fee Processing
            processTransferFee(request.getFromAccount(), request.getFee(), dtx);
            
            // Phase 4: Audit Logging
            auditLogger.logTransaction(context, TransactionStatus.PENDING);
            
            // Phase 5: Commit
            dtx.commit();
            
            auditLogger.logTransaction(context, TransactionStatus.COMPLETED);
            return TransactionResult.success(context.getTransactionId());
            
        } catch (BusinessException e) {
            return handleBusinessException(e, context);
        } catch (SystemException e) {
            return handleSystemException(e, context);
        } catch (Exception e) {
            return handleUnexpectedException(e, context);
        }
    }
    
    private TransactionResult handleBusinessException(BusinessException e, TransactionContext context) {
        auditLogger.logFailedTransaction(context, e);
        
        if (e instanceof InsufficientFundsException) {
            notificationService.sendInsufficientFundsNotification(context.getFromAccount());
            return TransactionResult.businessFailure("残高が不足しています");
        } else if (e instanceof AccountNotFoundException) {
            securityService.logSuspiciousActivity(context);
            return TransactionResult.businessFailure("指定された口座が見つかりません");
        }
        
        return TransactionResult.businessFailure(e.getMessage());
    }
    
    private TransactionResult handleSystemException(SystemException e, TransactionContext context) {
        log.error("System error during transaction: {}", e.getMessage(), e);
        
        if (e instanceof DatabaseConnectionException) {
            boolean failoverSuccess = databaseFailoverManager.attemptFailover();
            if (failoverSuccess) {
                return retryTransaction(context);
            }
        } else if (e instanceof NetworkTimeoutException) {
            CircuitBreaker circuitBreaker = circuitBreakerManager.getCircuitBreaker("network");
            if (circuitBreaker.getState() == CircuitBreaker.State.CLOSED) {
                return retryWithBackoff(context);
            }
        }
        
        return TransactionResult.systemFailure("システム障害により処理できません");
    }
}
```

### 課題2のヒント
```java
class AutonomousVehicleSafetySystem {
    @RealTimeExecution(deadline = "10ms")
    public SafetyDecision makeSafetyDecision(VehicleState currentState, EnvironmentData environment) {
        try {
            // Sensor validation
            validateSensorData(environment.getSensorData());
            
            // Threat assessment
            List<Threat> threats = threatDetector.assessThreats(currentState, environment);
            
            // Safety decision
            if (threats.isEmpty()) {
                return SafetyDecision.CONTINUE_NORMAL_OPERATION;
            }
            
            // Select optimal avoidance strategy
            AvoidanceStrategy strategy = selectOptimalAvoidanceStrategy(threats, currentState);
            
            return SafetyDecision.EXECUTE_AVOIDANCE(strategy);
            
        } catch (SensorFailureException e) {
            return handleSensorFailure(e, currentState);
        } catch (ActuatorFailureException e) {
            return handleActuatorFailure(e, currentState);
        } catch (SystemFailureException e) {
            return handleSystemFailure(e, currentState);
        } catch (Exception e) {
            // Ultimate failsafe
            log.fatal("Critical system error: {}", e.getMessage());
            return SafetyDecision.EMERGENCY_STOP;
        }
    }
    
    private SafetyDecision handleSensorFailure(SensorFailureException e, VehicleState state) {
        SensorType failedSensor = e.getFailedSensorType();
        
        // Check redundancy availability
        boolean hasRedundancy = redundancyManager.hasWorkingRedundancy(failedSensor);
        
        if (hasRedundancy) {
            redundancyManager.switchToBackupSensor(failedSensor);
            return SafetyDecision.CONTINUE_WITH_REDUCED_CAPABILITY;
        }
        
        // Determine if safe operation is possible
        MinimumSensorRequirements minReq = getMinimumSensorRequirements();
        List<SensorType> remainingSensors = getCurrentWorkingSensors();
        
        if (minReq.isSatisfiedBy(remainingSensors)) {
            activateDegradedMode();
            return SafetyDecision.CONTINUE_DEGRADED_MODE;
        } else {
            return SafetyDecision.SAFE_STOP_REQUIRED;
        }
    }
    
    private SafetyDecision handleActuatorFailure(ActuatorFailureException e, VehicleState state) {
        ActuatorType failedActuator = e.getFailedActuatorType();
        
        switch (failedActuator) {
            case STEERING:
                if (hasBackupSteering()) {
                    switchToBackupSteering();
                    return SafetyDecision.CONTINUE_WITH_BACKUP_STEERING;
                } else {
                    return SafetyDecision.EMERGENCY_STOP;
                }
                
            case PRIMARY_BRAKING:
                if (hasSecondaryBraking()) {
                    switchToSecondaryBraking();
                    return SafetyDecision.CONTINUE_WITH_SECONDARY_BRAKING;
                } else {
                    return SafetyDecision.CONTROLLED_EMERGENCY_STOP;
                }
                
            case THROTTLE:
                // Can continue with reduced capability
                return SafetyDecision.CONTINUE_COASTING_MODE;
                
            default:
                return evaluateRemainingCapabilities(failedActuator, state);
        }
    }
}
```

### 課題3のヒント
```java
class MedicalSafetySystem {
    @MedicalSafety(level = CRITICAL)
    public PrescriptionResult validatePrescription(Patient patient, Medication medication, Dosage dosage) {
        PrescriptionContext context = createPrescriptionContext(patient, medication, dosage);
        
        try {
            // Multi-layer safety validation
            performAllergyCheck(patient, medication);
            performDrugInteractionCheck(patient, medication);
            performDosageValidation(patient, medication, dosage);
            performContraindicationCheck(patient, medication);
            
            // Create safe prescription
            Prescription prescription = createPrescription(context);
            
            // Generate safety monitoring plan
            SafetyMonitoringPlan monitoring = createMonitoringPlan(prescription);
            
            return PrescriptionResult.approved(prescription, monitoring);
            
        } catch (AllergyConflictException e) {
            return handleAllergyConflict(e, context);
        } catch (DrugInteractionException e) {
            return handleDrugInteraction(e, context);
        } catch (DosageException e) {
            return handleDosageError(e, context);
        } catch (ContraindicationException e) {
            return handleContraindication(e, context);
        } catch (SystemUnavailableException e) {
            return handleSystemUnavailable(e, context);
        } catch (Exception e) {
            return handleCriticalMedicalError(e, context);
        }
    }
    
    private PrescriptionResult handleAllergyConflict(AllergyConflictException e, PrescriptionContext context) {
        // Log critical safety event
        medicalSafetyLogger.logCriticalEvent(context, e);
        
        // Find alternative medications
        AlternativeMedicationFinder finder = new AlternativeMedicationFinder();
        List<Medication> alternatives = finder.findAlternatives(
            context.getMedication(), 
            context.getPatient().getAllergies()
        );
        
        if (!alternatives.isEmpty()) {
            SafetyRecommendation recommendation = createSafetyRecommendation(
                "Allergy conflict detected", alternatives);
            return PrescriptionResult.alternativeRequired(recommendation);
        } else {
            // No alternatives, require specialist consultation
            SpecialistConsultation consultation = requestSpecialistConsultation(
                context, "Allergy specialist consultation required");
            return PrescriptionResult.specialistRequired(consultation);
        }
    }
    
    private PrescriptionResult handleSystemUnavailable(SystemUnavailableException e, PrescriptionContext context) {
        // In medical systems, patient safety is paramount
        if (isEmergencyCase(context)) {
            // Emergency protocols allow limited override
            EmergencyProtocol emergency = activateEmergencyProtocol(context);
            
            // Minimal safe prescription based on standard protocols
            MinimalSafePrescription minimal = createMinimalSafePrescription(context);
            
            // Require immediate physician review
            PhysicianReview review = scheduleImmediatePhysicianReview(context);
            
            return PrescriptionResult.emergencyApproval(minimal, review);
        } else {
            // Non-emergency: defer until system available
            return PrescriptionResult.defer("System unavailable, please retry when system is restored");
        }
    }
    
    private PrescriptionResult handleCriticalMedicalError(Exception e, PrescriptionContext context) {
        // Critical medical errors require immediate escalation
        medicalSafetyCommittee.reportCriticalIncident(context, e);
        
        // Activate medical emergency protocol
        MedicalEmergencyProtocol protocol = activateMedicalEmergencyProtocol();
        
        // Immediate physician notification
        emergencyNotificationSystem.notifyAttendingPhysician(context, e);
        
        // Fail safe - no prescription without explicit physician override
        return PrescriptionResult.criticalFailure(
            "Critical system error - immediate physician review required");
    }
}
```

## 応用のポイント

1. **安全性優先**: 例外処理において安全性を最優先に考慮
2. **多層防御**: 複数の例外処理層による堅牢性確保
3. **自動復旧**: 可能な限り自動的な回復メカニズム
4. **graceful degradation**: 段階的な機能縮退による継続性
5. **監査と追跡**: 完全な例外ログとトレーサビリティ

## ○ 完了チェックリスト

- [ ] 課題1: 高可用性銀行システムが正常に動作する
- [ ] 課題2: 自動運転車制御システムが実装できた
- [ ] 課題3: 医療情報統合システムが動作する
- [ ] 例外処理が包括的に実装されている
- [ ] 安全性が十分に考慮されている
- [ ] システムの継続性が保証されている

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！