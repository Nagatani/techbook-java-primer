# 第9章 応用課題

## 🎯 学習目標
- Mapの高度な活用技術
- 効率的なハッシュテーブル実装
- 複雑なデータ関係の管理
- アルゴリズムの最適化
- 実用的なシステム設計

## 📝 課題一覧

### 課題1: 多言語翻訳システム
**ファイル名**: `MultilingualTranslationSystem.java`

HashMap/TreeMapを使用した高性能翻訳システムを作成してください。

**要求仕様**:
- 辞書ベース翻訳エンジン
- 機械学習による翻訳品質向上
- 多言語同時対応
- リアルタイム翻訳

**翻訳機能**:
- 単語・フレーズ・文章翻訳
- 文脈を考慮した翻訳
- 専門用語辞書対応
- 翻訳学習機能

**実行例**:
```
=== 多言語翻訳システム ===

🌍 UniversalTranslator Pro v4.0

=== 辞書システム ===
📚 多言語辞書管理:

対応言語: 25言語
- 主要言語: 英語, 日本語, 中国語, 韓国語
- ヨーロッパ: フランス語, ドイツ語, スペイン語, イタリア語
- その他: アラビア語, ヒンディー語, ロシア語, ポルトガル語

辞書統計:
総エントリ数: 15,000,000語
- 一般辞書: 8,500,000語
- 専門辞書: 3,200,000語
- 俗語辞書: 1,800,000語
- 固有名詞: 1,500,000語

データ構造:
基本辞書: HashMap<String, TranslationEntry>
文脈辞書: TreeMap<String, Map<String, Translation>>
フレーズ辞書: HashMap<String, PhraseTranslation>
専門用語: HashMap<Domain, HashMap<String, TermTranslation>>

メモリ使用量:
辞書データ: 2.4GB
インデックス: 890MB
キャッシュ: 512MB
総使用量: 3.8GB

=== 翻訳エンジン ===
🔄 高精度翻訳処理:

翻訳例1: 日本語 → 英語
入力: "機械学習は人工知能の一分野です。"

処理工程:
1. 形態素解析:
   機械/名詞 学習/名詞 は/助詞 人工/名詞 知能/名詞 の/助詞 一/数詞 分野/名詞 です/助動詞

2. 構文解析:
   主語: 機械学習
   述語: です
   目的語: 一分野
   修飾語: 人工知能の

3. 単語翻訳:
   機械学習 → machine learning (技術用語辞書)
   人工知能 → artificial intelligence (専門用語)
   分野 → field (一般辞書)

4. 文法変換:
   SOV → SVO構造変換
   助詞処理: の → of
   語順調整: 修飾関係保持

5. 最終翻訳:
   "Machine learning is a field of artificial intelligence."

翻訳精度:
BLEU スコア: 0.847 (高精度)
文法正確性: 96%
語彙適切性: 94%
自然さ: 89%

=== 文脈考慮翻訳 ===
🧠 コンテキスト解析:

多義語翻訳例:
入力: "銀行の近くの川の銀行で休憩した。"

文脈解析:
1. "銀行" 候補:
   - bank (金融機関)
   - bank (川岸)

2. 文脈手がかり:
   - "近く" → 地理的関係
   - "川の" → 川に関連
   - "休憩" → 場所としての機能

3. 文脈スコア計算:
   bank(金融) + 川: スコア 0.2
   bank(川岸) + 川: スコア 0.9

4. 最適選択:
   1番目の"銀行" → bank (金融機関)
   2番目の"銀行" → bank (river bank)

最終翻訳:
"I took a break on the bank of the river near the bank."

文脈辞書活用:
文脈パターン数: 2,500,000
平均解決率: 87%
曖昧性除去: 92%成功

=== 専門分野翻訳 ===
🔬 ドメイン特化翻訳:

専門分野辞書:
医学: 450,000用語
法律: 320,000用語
工学: 280,000用語
IT: 380,000用語
金融: 220,000用語

医学翻訳例:
入力: "患者は急性心筋梗塞の症状を示している。"

専門用語解析:
- 急性心筋梗塞: acute myocardial infarction (正確な医学用語)
- 症状: symptoms (一般用語だが医学文脈で最適化)
- 示している: showing/presenting (医学文脈でのより適切な表現選択)

翻訳結果:
"The patient is presenting symptoms of acute myocardial infarction."

専門性評価:
医学用語正確性: 98%
専門表現適切性: 95%
医学文献標準: 準拠

IT翻訳例:
入力: "このAPIはRESTfulアーキテクチャに基づいています。"

技術用語処理:
- API: API (借用語として維持)
- RESTful: RESTful (技術用語)
- アーキテクチャ: architecture (技術分野での標準翻訳)

翻訳結果:
"This API is based on RESTful architecture."

=== フレーズ翻訳 ===
💬 慣用表現・フレーズ処理:

フレーズ辞書:
慣用句: 45,000表現
ことわざ: 12,000表現
定型文: 78,000表現
コロケーション: 230,000組み合わせ

慣用句翻訳例:
入力: "彼は猫の手も借りたいほど忙しい。"

フレーズ検出:
"猫の手も借りたい" → 慣用句として認識
文字通り翻訳: "want to borrow even a cat's paws"
慣用句翻訳: "extremely busy" または "swamped with work"

文脈考慮選択:
前後文脈: ビジネス状況
適切な表現: "swamped with work"

最終翻訳:
"He is swamped with work."

コロケーション例:
入力: "強い雨が降っている。"
- 強い + 雨: heavy rain (コロケーション辞書)
- 降る: fall/pour (雨の文脈で pour が自然)

翻訳: "It is pouring with heavy rain."

=== リアルタイム翻訳 ===
⚡ 高速翻訳処理:

パフォーマンス最適化:
キャッシュ戦略: LRU キャッシュ (100,000エントリ)
ヒット率: 78% (よく使われる表現)
並列処理: 文単位での並列翻訳
前処理最適化: 形態素解析結果キャッシュ

リアルタイム統計:
処理速度: 2,340文/秒
平均レスポンス: 0.15秒/文
同時翻訳数: 500セッション
キュー待機時間: 0.02秒

チャット翻訳例:
[09:30:15] 田中: "プロジェクトの進捗はいかがですか？"
[09:30:15] Translation: "How is the project progress?"
[09:30:18] Smith: "We're on track and should finish by Friday."
[09:30:18] 翻訳: "順調に進んでおり、金曜日までに完了予定です。"

ストリーミング翻訳:
音声入力 → リアルタイム文字起こし → 即座翻訳
遅延時間: 0.8秒 (音声→翻訳完了)
正確性: 92% (音声認識95% × 翻訳97%)

=== 機械学習強化 ===
🤖 AI による翻訳品質向上:

学習データ:
並列コーパス: 50億文ペア
ユーザー修正: 250万件
専門文書: 180万文書
Web クロール: 12億文

ニューラル翻訳モデル:
アーキテクチャ: Transformer (6層)
パラメータ数: 2.1億個
学習時間: 240時間 (GPU x 8)
更新頻度: 週1回

品質改善例:
従来辞書翻訳:
"彼は医者になりたがっている。"
→ "He wants to become a doctor."

ニューラル強化:
文脈: 若い人について話している
→ "He wants to become a doctor." (基本)
→ "He aspires to be a doctor." (より自然)

BLEU スコア改善:
辞書ベース: 0.672
ニューラル強化: 0.847
改善率: +26%

=== 翻訳学習機能 ===
📈 適応型翻訳システム:

ユーザー学習:
修正フィードバック: 蓄積・学習
専門用語追加: ユーザー辞書
翻訳パターン: 個人最適化

学習例:
ユーザー修正:
システム翻訳: "meeting"
ユーザー修正: "会議" → "ミーティング"
学習効果: 同ユーザーの今後の翻訳で "meeting" = "ミーティング" を優先

ドメイン適応:
ユーザー特定分野 (例: 医学)
専門用語使用頻度学習
文体傾向学習 (論文調 vs 会話調)

企業用語集連携:
社内用語: 自動学習・適用
ブランド名: 固有の翻訳維持
表記統一: 企業スタイルガイド準拠

=== 多言語同時対応 ===
🌐 マルチリンガル処理:

言語検出:
自動言語識別: 99.3%精度
混在言語対応: 文単位で言語切り替え
不明語処理: 音写・借用語として処理

多言語翻訳例:
入力 (日英混在): "このAPIのresponse timeは約100msです。"

言語検出結果:
"この": 日本語
"API": 英語 (借用語)
"の": 日本語
"response time": 英語
"は約": 日本語
"100ms": 英語 (単位)
"です": 日本語

統合翻訳:
→ "The response time of this API is approximately 100ms."

言語ペア対応:
直接翻訳: 25×24 = 600ペア
間接翻訳: 英語経由での三角翻訳
品質: 直接 > 間接 (平均-8% BLEU)

=== 翻訳品質評価 ===
📊 自動品質チェック:

評価指標:
BLEU スコア: 機械評価
人手評価: クラウドワーカー評価
流暢性: 1-5段階評価
正確性: 意味保持度評価

品質チェック例:
翻訳: "彼は走っている。" → "He is running."
BLEU: 1.0 (完全一致)
流暢性: 5/5 (完全に自然)
正確性: 5/5 (意味完全保持)

低品質検出:
翻訳: "複雑な文章..." → "Complex sentence..." (不完全)
品質スコア: 2.1/5.0 (低品質)
自動フラグ: 要人手確認

改善提案:
システム提案: "Complex sentences require..."
代替翻訳: 3候補提示
ユーザー選択: 最適翻訳採用

=== システム統計 ===
📈 運用パフォーマンス:

処理統計 (過去1ヶ月):
総翻訳数: 45,600,000文
言語ペア: 78組み合わせ
ユーザー数: 234,000人
平均セッション: 15分

性能指標:
平均応答時間: 0.15秒
95%tile応答時間: 0.31秒
99%tile応答時間: 0.58秒
可用性: 99.97%

品質指標:
平均BLEU: 0.847
ユーザー満足度: 4.6/5.0
修正率: 12% (修正が必要な翻訳)
学習改善率: +15% (月間品質向上)

言語別性能:
英↔日: BLEU 0.923 (最高)
中↔英: BLEU 0.856
韓↔日: BLEU 0.798
独↔英: BLEU 0.834

リソース使用量:
メモリ: 3.8GB (辞書) + 2.1GB (モデル)
CPU: 45% (24コア)
ストレージ: 450GB (全データ)
ネットワーク: 1.2Gbps (ピーク時)
```

**評価ポイント**:
- Mapの効果的な活用
- 自然言語処理の理解
- 機械学習との統合
- 実用的なシステム設計

---

### 課題2: ソーシャルネットワーク分析システム
**ファイル名**: `SocialNetworkAnalyzer.java`

大規模ソーシャルネットワークを分析するシステムを作成してください。

**要求仕様**:
- グラフデータの効率的管理
- 影響力分析アルゴリズム
- コミュニティ検出
- リアルタイム分析

**分析機能**:
- 中心性指標の計算
- 情報拡散シミュレーション
- トレンド分析
- 異常検知

**実行例**:
```
=== ソーシャルネットワーク分析システム ===

📱 SocialGraph Analytics v5.0

=== ネットワーク構成 ===
🌐 大規模グラフ管理:

ネットワーク統計:
総ユーザー数: 50,000,000人
総フォロー関係: 2,500,000,000関係
平均次数: 50.0 (フォロー/フォロワー)
最大次数: 125,000 (有名人アカウント)

データ構造:
ユーザー管理: HashMap<UserID, UserProfile>
フォロー関係: HashMap<UserID, Set<UserID>>
逆引きマップ: HashMap<UserID, Set<UserID>> (フォロワー)
重み付きエッジ: HashMap<EdgeID, InteractionWeight>

グラフ特性:
直径: 6.2 (6次の隔たり理論)
クラスタリング係数: 0.23
密度: 0.000001 (スパースグラフ)
コンポーネント数: 1 (巨大連結成分)

メモリ最適化:
ユーザーデータ: 4.2GB
関係データ: 32.5GB (圧縮後)
インデックス: 8.7GB
総使用量: 45.4GB

=== 中心性分析 ===
📊 影響力指標計算:

PageRank計算:
アルゴリズム: 分散PageRank
反復回数: 100回 (収束)
ダンピング係数: 0.85
計算時間: 45分 (並列処理)

PageRank上位ランキング:
1. @techguru (PR: 0.0045): テクノロジー評論家
2. @newscaster (PR: 0.0041): ニュースアンカー  
3. @popstar_jp (PR: 0.0038): ポップシンガー
4. @comedian_a (PR: 0.0035): コメディアン
5. @athlete_runner (PR: 0.0033): マラソン選手

中心性指標比較:
次数中心性 (単純フォロワー数):
@popstar_jp: 125,000フォロワー (1位)

PageRank (影響力):
@techguru: 高エンゲージメント (1位)

媒介中心性 (橋渡し役):
@journalist_b: 異分野間の情報仲介 (1位)

近接中心性 (情報到達速度):
@news_bot: 自動投稿による高速拡散 (1位)

固有ベクトル中心性:
影響力のある人からのフォローを重視
結果: PageRankと87%相関

=== コミュニティ検出 ===
👥 グループ分析:

クラスタリングアルゴリズム:
Louvain法: モジュラリティ最大化
Leiden法: 高精度コミュニティ検出
Label Propagation: 高速処理

検出結果:
総コミュニティ数: 15,847個
最大コミュニティ: 2,340,000人 (一般ユーザー)
平均サイズ: 3,156人
モジュラリティ: 0.67 (良好な分離)

主要コミュニティ:
1. テクノロジー (1,890,000人):
   - キーワード: AI, プログラミング, スタートアップ
   - 中心人物: @techguru, @ai_researcher
   - 特徴: 高エンゲージメント、情報共有活発

2. エンターテイメント (2,340,000人):
   - キーワード: 音楽, 映画, ドラマ
   - 中心人物: @popstar_jp, @actor_famous
   - 特徴: 拡散力強い、トレンド発信源

3. スポーツ (1,567,000人):
   - キーワード: サッカー, 野球, オリンピック
   - 中心人物: @athlete_runner, @coach_pro
   - 特徴: イベント連動、季節性あり

コミュニティ重複:
単一所属: 78% (明確な所属)
複数所属: 22% (橋渡し役)
最大重複数: 5コミュニティ

=== 情報拡散分析 ===
🔄 バイラル伝播モデル:

拡散モデル:
SIR モデル (感染症モデル応用):
- S: Susceptible (感受性)
- I: Infected (感染・共有済み)
- R: Recovered (免疫・無関心)

SIR パラメータ:
感染率 β: 0.03 (3%が共有)
回復率 γ: 0.1 (10日で関心失う)
基本再生産数 R0: 1.8

バイラル投稿例:
投稿: "新技術発表！革命的なAIアルゴリズム"
初期投稿者: @techguru (フォロワー 89,000)

拡散シミュレーション:
Day 0: 1投稿 (初期)
Day 1: 267共有 (直接フォロワー)
Day 2: 3,456共有 (2次拡散)
Day 3: 45,678共有 (3次拡散)
Day 7: 234,567共有 (ピーク)
Day 14: 189,234共有 (収束)

影響要因分析:
コンテンツ要因: 技術性 (+30%)
投稿者要因: 権威性 (+45%)
タイミング要因: 平日昼間 (+15%)
ネットワーク要因: コミュニティ密度 (+25%)

拡散経路分析:
主要経路1: テクノロジーコミュニティ (67%)
主要経路2: ビジネスコミュニティ (23%)
クロスオーバー: エンタメ→テック (10%)

=== インフルエンサー分析 ===
⭐ 影響力詳細分析:

インフルエンサー分類:
メガインフルエンサー: フォロワー100万+
マクロインフルエンサー: フォロワー10-100万
マイクロインフルエンサー: フォロワー1-10万
ナノインフルエンサー: フォロワー1000-1万

エンゲージメント分析:
@techguru:
- フォロワー数: 89,000
- 平均いいね: 2,340 (エンゲージメント率: 2.6%)
- 平均リツイート: 456
- 平均コメント: 234
- エンゲージメント品質: 高 (有意義な議論)

@popstar_jp:
- フォロワー数: 1,250,000
- 平均いいね: 15,600 (エンゲージメント率: 1.2%)
- 平均リツイート: 3,400
- 平均コメント: 890
- エンゲージメント品質: 中 (感情的反応多)

影響力スコア算出:
算出式: (フォロワー数 × 0.3) + (エンゲージメント率 × 0.4) + 
        (拡散力 × 0.2) + (権威性 × 0.1)

トップ影響力ランキング:
1. @techguru: スコア 87.4
2. @newscaster: スコア 85.1  
3. @economist_dr: スコア 82.7
4. @popstar_jp: スコア 78.9
5. @athlete_runner: スコア 76.3

=== トレンド分析 ===
📈 リアルタイムトレンド検出:

トレンド検出アルゴリズム:
時系列分析: 投稿頻度の急激な変化
異常検知: 統計的外れ値検出
自然言語処理: キーワード抽出・分析
感情分析: ポジティブ/ネガティブ判定

現在のトレンド (リアルタイム):
1. #新技術発表 (急上昇 +340%)
   - 投稿数: 45,678件/時間
   - 感情: ポジティブ 78%
   - 主要コミュニティ: テクノロジー

2. #映画レビュー (安定継続 +15%)
   - 投稿数: 23,456件/時間
   - 感情: 中立 45%, ポジティブ 35%
   - 主要コミュニティ: エンターテイメント

3. #経済ニュース (緊急上昇 +567%)
   - 投稿数: 67,890件/時間
   - 感情: ネガティブ 62%
   - 主要コミュニティ: ビジネス, 政治

トレンド予測:
機械学習モデル: LSTM + Transformer
予測精度: 76% (6時間先)
特徴量: 投稿数, エンゲージメント, 外部イベント

予測結果 (次6時間):
#新技術発表: ピーク維持 → 緩やか減少
#経済ニュース: 継続上昇 → 政府発表待ち
#スポーツ結果: 新規上昇 → 今夜の試合

=== 異常検知 ===
🚨 不正・異常行動検出:

異常パターン:
1. ボットアカウント:
   - 異常な投稿パターン (毎時正確に投稿)
   - コンテンツ類似性 (99%一致投稿)
   - フォロー行動異常 (1日1000フォロー)

2. 情報操作:
   - 協調的非真正行動
   - 同時多数アカウントでの同一投稿
   - 人工的なトレンド操作

3. スパム行動:
   - 無関係リプライ大量送信
   - 商用リンク多数投稿
   - ハッシュタグ乱用

検出例:
疑惑アカウント群: 1,234アカウント
検出理由: 15分以内に同一投稿を共有
内容: 特定製品の宣伝
判定: 協調的スパム行動

対応アクション:
レベル1: 警告表示
レベル2: 投稿制限
レベル3: アカウント一時停止
レベル4: 永久停止

検出精度:
真陽性率: 94% (実際の不正を正しく検出)
偽陽性率: 3% (正常行動を誤検出)
処理速度: 23,456アカウント/秒

=== ネットワーク進化分析 ===
📅 時系列ネットワーク分析:

成長パターン:
ユーザー増加: 指数関数的 → 線形 → 飽和
関係増加: べき乗則 (優先的結合)
コミュニティ: 分裂・統合の繰り返し

時系列統計 (過去5年):
2020: 5M ユーザー, 125M 関係
2021: 12M ユーザー, 480M 関係  
2022: 25M ユーザー, 1.2B 関係
2023: 40M ユーザー, 2.0B 関係
2024: 50M ユーザー, 2.5B 関係

成長モデル:
バラバシ・アルバート モデル:
新ノード接続確率 ∝ 既存ノードの次数
結果: スケールフリーネットワーク
冪指数: -2.3 (理論値 -3 に近似)

ネットワーク堅牢性:
ランダム削除: 50%削除でも連結性維持
標的攻撃: 上位1%削除で分裂
臨界閾値: 2.4% (ハブノード削除)

=== リアルタイム分析 ===
⚡ ストリーミング分析:

データストリーム:
投稿ストリーム: 45,678投稿/秒
フォローストリーム: 2,340フォロー/秒
いいねストリーム: 234,567いいね/秒

リアルタイム処理:
ウィンドウサイズ: 5分間
更新頻度: 30秒
レイテンシ: 2.3秒 (データ受信→結果表示)

ダッシュボード表示:
現在のアクティブユーザー: 2,340,000人
リアルタイム投稿数: 45,678/分
トレンドワード: #新技術発表, #映画レビュー
異常検知アラート: 0件

ストリーム分析例:
時刻: 14:30:00
検出: 投稿急増 (+234% from baseline)
キーワード: "地震", "安全確認"
判定: 緊急事態発生の可能性
アクション: 関連当局に自動通知

=== システム性能 ===
📊 分析性能統計:

計算性能:
PageRank計算: 45分 (5,000万ノード)
コミュニティ検出: 1.2時間
中心性指標: 15分 (並列計算)
異常検知: リアルタイム (2.3秒)

メモリ使用量:
グラフデータ: 45.4GB
計算バッファ: 32.1GB
結果キャッシュ: 12.7GB
総使用量: 90.2GB

スケーラビリティ:
最大ノード数: 1億ノード (検証済み)
最大エッジ数: 100億エッジ
処理時間: O(E log V) (効率的)
メモリ要件: O(V + E) (線形)

可用性:
稼働率: 99.95%
平均復旧時間: 3.2分
バックアップ頻度: 1時間
災害復旧: 15分
```

**評価ポイント**:
- グラフアルゴリズムの実装
- 大規模データの効率的処理
- 社会ネットワーク理論の理解
- リアルタイム分析の実現

---

### 課題3: 推薦システムエンジン
**ファイル名**: `RecommendationEngine.java`

高度な推薦アルゴリズムを実装したシステムを作成してください。

**要求仕様**:
- 協調フィルタリング
- コンテンツベース推薦
- ハイブリッド推薦
- リアルタイム学習

**推薦機能**:
- パーソナライゼーション
- セレンディピティ確保
- 多様性最適化
- 説明可能性

**実行例**:
```
=== 推薦システムエンジン ===

🎯 SmartRecommender AI v6.0

=== ユーザー・アイテム管理 ===
📊 大規模データ管理:

システム規模:
ユーザー数: 25,000,000人
アイテム数: 5,000,000個
評価データ: 2.5億件
行動ログ: 500億イベント

データ構造:
ユーザープロファイル: HashMap<UserID, UserProfile>
アイテム特徴: HashMap<ItemID, ItemFeatures>
評価マトリックス: SparseMatrix<UserID, ItemID, Rating>
行動履歴: HashMap<UserID, List<UserAction>>

スパース性:
評価密度: 0.002% (非常にスパース)
アクティブユーザー: 65% (月1回以上活動)
ロングテール: 80%のアイテムが低頻度

メモリ最適化:
ユーザーデータ: 8.9GB
アイテムデータ: 12.4GB
評価データ: 45.7GB (圧縮後)
総使用量: 67.0GB

=== 協調フィルタリング ===
🤝 ユーザー・アイテム協調:

Matrix Factorization:
アルゴリズム: Alternating Least Squares (ALS)
潜在因子数: 128次元
正則化パラメータ: λ = 0.01
反復回数: 25回

性能指標:
RMSE: 0.847 (5段階評価)
MAE: 0.623
Precision@10: 0.234
Recall@10: 0.156
F1-Score: 0.189

ユーザーベース協調フィルタリング:
類似度計算: ピアソン相関係数
近傍ユーザー数: 50人
計算例:

ユーザー12345の推薦:
類似ユーザートップ5:
1. ユーザー67890: 類似度 0.87
2. ユーザー54321: 類似度 0.84
3. ユーザー98765: 類似度 0.81
4. ユーザー13579: 類似度 0.79
5. ユーザー24680: 類似度 0.77

推薦アイテム生成:
未評価アイテムから、類似ユーザーが
高評価したアイテムを重み付き平均

推薦結果:
1. アイテムA: 予測評価 4.3
2. アイテムB: 予測評価 4.1  
3. アイテムC: 予測評価 4.0
4. アイテムD: 予測評価 3.9
5. アイテムE: 予測評価 3.8

アイテムベース協調フィルタリング:
アイテム間類似度: コサイン類似度
計算効率: 事前計算 + インクリメンタル更新
更新頻度: 6時間毎

=== コンテンツベース推薦 ===
📝 特徴量ベース推薦:

アイテム特徴量:
映画の例:
- ジャンル: アクション, コメディ, ドラマ (One-hot)
- 出演者: 俳優ID (Multi-hot)
- 監督: 監督ID
- 公開年: 正規化数値
- 評価: 平均評価, 評価数
- キーワード: TF-IDF ベクトル (1000次元)

ユーザープロファイル構築:
視聴履歴から特徴量の重み付き平均
重み: 評価値 × 時間減衰 × 視聴完了率

例: ユーザー12345のプロファイル
- アクション: 0.78 (高い嗜好)
- コメディ: 0.45 (中程度)
- ドラマ: 0.23 (低い嗜好)
- 好きな俳優: [俳優A: 0.89, 俳優B: 0.67]
- 好きな監督: [監督X: 0.76, 監督Y: 0.54]

推薦計算:
コサイン類似度(ユーザープロファイル, アイテム特徴)

推薦結果:
1. "アクション映画Z": 類似度 0.92
2. "俳優Aの新作": 類似度 0.88
3. "監督Xのコメディ": 類似度 0.84

特徴重要度分析:
ジャンル: 35%の影響力
出演者: 28%の影響力
監督: 22%の影響力
その他: 15%の影響力

=== ハイブリッド推薦 ===
🔀 複数手法統合:

統合戦略:
1. 加重結合:
   最終スコア = 0.4×協調 + 0.3×コンテンツ + 0.2×深層学習 + 0.1×人気度

2. 切り替え:
   新規ユーザー → コンテンツベース
   十分なデータ → 協調フィルタリング
   コールドスタート → 人気度ベース

3. 混合:
   各手法から独立に推薦生成 → 多様性確保

推薦例 (ユーザー12345):
協調フィルタリング推薦:
1. 映画A (スコア: 4.3)
2. 映画B (スコア: 4.1)
3. 映画C (スコア: 4.0)

コンテンツベース推薦:
1. 映画D (スコア: 0.92)
2. 映画E (スコア: 0.88)
3. 映画A (スコア: 0.84)

統合後最終推薦:
1. 映画A: 統合スコア 8.94 (協調+コンテンツ高評価)
2. 映画F: 統合スコア 7.23 (深層学習推薦)
3. 映画B: 統合スコア 6.87
4. 映画D: 統合スコア 6.45
5. 映画G: 統合スコア 6.12

=== 深層学習推薦 ===
🧠 ニューラル協調フィルタリング:

Neural Collaborative Filtering (NCF):
アーキテクチャ:
- 埋め込み層: ユーザー・アイテム埋め込み (128次元)
- MLP: 4層全結合 [256, 128, 64, 32]
- 出力層: sigmoid (0-1スコア)

Wide & Deep モデル:
Wide部分: 特徴量クロス (記憶)
Deep部分: 深層ニューラル (汎化)

AutoEncoder 推薦:
入力: ユーザーの評価ベクトル (スパース)
出力: 復元された評価ベクトル (欠損値予測)
中間層: 256 → 128 → 64 → 128 → 256

学習統計:
訓練データ: 2億評価
検証データ: 2500万評価
エポック数: 50
バッチサイズ: 1024
学習時間: 48時間 (GPU × 8)

性能改善:
従来協調フィルタリング: RMSE 0.847
深層学習モデル: RMSE 0.723
改善率: 14.6%

=== リアルタイム学習 ===
⚡ オンライン学習システム:

ストリーミング更新:
新規評価: リアルタイム反映
モデル更新: ミニバッチ学習
更新頻度: 10分毎
レイテンシ: 50ms (推薦生成)

インクリメンタル学習:
Matrix Factorization: 新規ユーザー/アイテム追加
特徴量更新: 新規データでの重み調整
モデル再学習: 週1回フル更新

リアルタイム例:
14:30:00 - ユーザー12345が映画Xを5つ星評価
14:30:01 - ユーザープロファイル更新
14:30:02 - 類似ユーザー再計算
14:30:03 - 推薦リスト更新
14:30:04 - 推薦表示 (4秒後に反映)

行動学習:
クリック: +0.1ポイント
視聴開始: +0.3ポイント  
視聴完了: +0.8ポイント
評価入力: +1.0ポイント

学習効果測定:
新規推薦精度: 67% → 82% (1週間後)
クリック率: 2.3% → 3.8%
視聴完了率: 45% → 58%

=== 多様性・セレンディピティ ===
🌈 推薦多様性確保:

多様性指標:
ジャンル多様性: エントロピー計算
類似度分散: アイテム間平均距離
カバレッジ: 推薦されるアイテムの割合

多様性最適化:
MMR (Maximal Marginal Relevance):
次のアイテム選択 = λ×関連度 - (1-λ)×既選択アイテムとの類似度

推薦結果比較:
精度重視推薦:
1. アクション映画A (類似度: 0.95)
2. アクション映画B (類似度: 0.93)
3. アクション映画C (類似度: 0.91)
→ ジャンル多様性: 低

多様性重視推薦:
1. アクション映画A (類似度: 0.95)
2. コメディ映画D (類似度: 0.78)
3. ドラマ映画E (類似度: 0.72)
→ ジャンル多様性: 高

セレンディピティ測定:
予期しない発見度 = 1 - ユーザープロファイル類似度
セレンディピティ推薦: 意図的に異なるジャンル提示

バランス調整:
精度: 70%
多様性: 20%
セレンディピティ: 10%

=== 説明可能な推薦 ===
💡 推薦理由説明:

説明生成:
協調フィルタリング:
"あなたと似た嗜好のユーザーが高く評価しました"
"この映画を好きな人は○○も好む傾向があります"

コンテンツベース:
"あなたが好きな俳優Aが出演しています"
"あなたがよく見るアクション映画です"
"監督Xの作品をよく視聴されています"

説明例:
推薦: "映画タイタニック"
説明: "あなたと91%類似した嗜好のユーザーが★5評価しました。また、あなたが好きなロマンス映画で、これまで視聴した類似作品の平均評価は4.2です。"

信頼度表示:
推薦精度: 85%
根拠の強さ: 強 (類似ユーザー多数)
過去的中率: 78%

ユーザー調査結果:
説明ありの受入率: 73%
説明なしの受入率: 45%
満足度向上: +28%

=== A/Bテスト・評価 ===
📊 推薦性能評価:

オフライン評価:
Hold-out法: 80% 訓練, 20% テスト
Cross-validation: 5-fold
時系列分割: 過去データで未来予測

評価指標:
精度指標:
- RMSE: 0.723 (予測誤差)
- MAE: 0.534 (平均絶対誤差)
- Precision@K: 0.245
- Recall@K: 0.189
- NDCG@K: 0.678

多様性指標:
- Intra-list Diversity: 0.456
- Coverage: 0.234 (全アイテムの23.4%が推薦)
- Gini Index: 0.345 (推薦分布の偏り)

オンライン評価:
A/Bテスト設定:
コントロール群: 従来推薦システム (50%)
実験群: 新推薦システム (50%)
期間: 4週間
対象ユーザー: 100万人

結果:
クリック率: +15.7% (2.3% → 2.7%)
視聴率: +12.4% (45% → 51%)
評価入力率: +8.9% (12% → 13%)
ユーザー満足度: +11.2% (3.8 → 4.2/5.0)

統計的有意性:
p-value < 0.001 (高度に有意)
信頼区間: 95%
効果サイズ: 中程度 (Cohen's d = 0.42)

=== システム性能 ===
⚡ 処理性能統計:

推薦生成性能:
平均応答時間: 50ms
95%tile応答時間: 120ms
99%tile応答時間: 250ms
スループット: 50,000 req/sec

メモリ使用量:
推薦モデル: 67GB
キャッシュ: 32GB
インデックス: 24GB
総使用量: 123GB

スケーラビリティ:
最大ユーザー数: 1億人 (検証済み)
最大アイテム数: 1000万個
最大同時リクエスト: 100,000
レスポンス時間: 対数的増加

可用性:
稼働率: 99.99%
平均復旧時間: 1.2分
フェイルオーバー: 30秒
データ損失: 0件
```

**評価ポイント**:
- 推薦アルゴリズムの深い理解
- 機械学習の効果的統合
- 大規模システムの設計
- ユーザーエクスペリエンスの考慮

## 💡 実装のヒント

### 課題1のヒント
```java
class MultilingualTranslationSystem {
    private Map<LanguagePair, HashMap<String, TranslationEntry>> dictionaries;
    private Map<String, TreeMap<String, ContextTranslation>> contextDictionaries;
    private NeuralTranslationModel neuralModel;
    
    public Translation translate(String text, Language source, Language target) {
        // 文分割
        List<String> sentences = splitSentences(text);
        List<Translation> translations = new ArrayList<>();
        
        for (String sentence : sentences) {
            Translation sentenceTranslation = translateSentence(sentence, source, target);
            translations.add(sentenceTranslation);
        }
        
        return combineTranslations(translations);
    }
    
    private Translation translateSentence(String sentence, Language source, Language target) {
        // 形態素解析
        List<Morpheme> morphemes = morphologicalAnalysis(sentence, source);
        
        // 文脈解析
        Context context = analyzeContext(morphemes);
        
        // 単語翻訳
        List<WordTranslation> wordTranslations = new ArrayList<>();
        for (Morpheme morpheme : morphemes) {
            WordTranslation translation = translateWord(morpheme, context, source, target);
            wordTranslations.add(translation);
        }
        
        // 文法変換
        List<WordTranslation> reordered = reorderWords(wordTranslations, source, target);
        
        // 文生成
        String result = generateSentence(reordered, target);
        
        return new Translation(result, calculateConfidence(wordTranslations));
    }
    
    private WordTranslation translateWord(Morpheme morpheme, Context context, 
                                        Language source, Language target) {
        LanguagePair pair = new LanguagePair(source, target);
        HashMap<String, TranslationEntry> dictionary = dictionaries.get(pair);
        
        // 基本辞書検索
        TranslationEntry entry = dictionary.get(morpheme.getSurface());
        if (entry == null) {
            // 語幹検索
            entry = dictionary.get(morpheme.getBase());
        }
        
        if (entry != null && entry.hasMultipleMeanings()) {
            // 文脈考慮翻訳
            return resolveAmbiguity(entry, context);
        }
        
        // ニューラル翻訳にフォールバック
        return neuralModel.translate(morpheme, context);
    }
    
    private WordTranslation resolveAmbiguity(TranslationEntry entry, Context context) {
        TreeMap<String, ContextTranslation> contextMap = 
            contextDictionaries.get(entry.getWord());
        
        double bestScore = 0;
        ContextTranslation bestTranslation = null;
        
        for (ContextTranslation candidate : contextMap.values()) {
            double score = calculateContextSimilarity(candidate.getContext(), context);
            if (score > bestScore) {
                bestScore = score;
                bestTranslation = candidate;
            }
        }
        
        return new WordTranslation(bestTranslation.getTranslation(), bestScore);
    }
}
```

### 課題2のヒント
```java
class SocialNetworkAnalyzer {
    private HashMap<Integer, Set<Integer>> adjacencyList;
    private HashMap<Integer, UserProfile> users;
    private HashMap<Integer, Double> pageRankScores;
    
    public Map<Integer, Double> calculatePageRank(double dampingFactor, int maxIterations) {
        int numNodes = adjacencyList.size();
        Map<Integer, Double> currentScores = new HashMap<>();
        Map<Integer, Double> newScores = new HashMap<>();
        
        // 初期化
        for (Integer nodeId : adjacencyList.keySet()) {
            currentScores.put(nodeId, 1.0 / numNodes);
        }
        
        for (int iter = 0; iter < maxIterations; iter++) {
            // 新しいスコア計算
            for (Integer nodeId : adjacencyList.keySet()) {
                double score = (1.0 - dampingFactor) / numNodes;
                
                // 入次リンクからのスコア計算
                for (Integer neighbor : getIncomingNeighbors(nodeId)) {
                    Set<Integer> outgoing = adjacencyList.get(neighbor);
                    score += dampingFactor * currentScores.get(neighbor) / outgoing.size();
                }
                
                newScores.put(nodeId, score);
            }
            
            // 収束判定
            if (hasConverged(currentScores, newScores)) {
                break;
            }
            
            currentScores.clear();
            currentScores.putAll(newScores);
            newScores.clear();
        }
        
        return currentScores;
    }
    
    public List<Community> detectCommunities() {
        // Louvain法によるコミュニティ検出
        Map<Integer, Integer> nodeToComm = new HashMap<>();
        Map<Integer, Set<Integer>> communities = new HashMap<>();
        
        // 初期化: 各ノードが独自のコミュニティ
        for (Integer nodeId : adjacencyList.keySet()) {
            nodeToComm.put(nodeId, nodeId);
            communities.put(nodeId, new HashSet<>(Arrays.asList(nodeId)));
        }
        
        boolean improved = true;
        while (improved) {
            improved = false;
            
            for (Integer nodeId : adjacencyList.keySet()) {
                int currentComm = nodeToComm.get(nodeId);
                int bestComm = currentComm;
                double bestGain = 0;
                
                // 近隣コミュニティでのモジュラリティ計算
                Set<Integer> neighborComms = getNeighborCommunities(nodeId, nodeToComm);
                for (Integer commId : neighborComms) {
                    double gain = calculateModularityGain(nodeId, commId, nodeToComm);
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestComm = commId;
                    }
                }
                
                // コミュニティ移動
                if (bestComm != currentComm) {
                    communities.get(currentComm).remove(nodeId);
                    communities.get(bestComm).add(nodeId);
                    nodeToComm.put(nodeId, bestComm);
                    improved = true;
                }
            }
        }
        
        return convertToCommunitiesList(communities);
    }
    
    private double calculateModularityGain(Integer nodeId, Integer commId, 
                                         Map<Integer, Integer> nodeToComm) {
        double ki = adjacencyList.get(nodeId).size(); // ノードの次数
        double sumIn = 0, sumTot = 0;
        
        // コミュニティ内エッジ数とコミュニティの総次数
        for (Integer member : getCommunityMembers(commId, nodeToComm)) {
            sumTot += adjacencyList.get(member).size();
            for (Integer neighbor : adjacencyList.get(member)) {
                if (nodeToComm.get(neighbor).equals(commId)) {
                    sumIn += 1;
                }
            }
        }
        sumIn /= 2; // 無向グラフなので2で割る
        
        double m = getTotalEdges() / 2.0;
        return (sumIn + getEdgesBetween(nodeId, commId)) / (2 * m) - 
               Math.pow((sumTot + ki) / (2 * m), 2);
    }
}
```

### 課題3のヒント
```java
class RecommendationEngine {
    private Map<Integer, Map<Integer, Double>> userItemRatings;
    private Map<Integer, UserProfile> userProfiles;
    private Map<Integer, ItemFeatures> itemFeatures;
    private MatrixFactorizationModel mfModel;
    
    public List<Recommendation> recommend(int userId, int numRecommendations) {
        // ハイブリッド推薦
        List<Recommendation> collaborative = collaborativeFiltering(userId, numRecommendations * 2);
        List<Recommendation> contentBased = contentBasedFiltering(userId, numRecommendations * 2);
        List<Recommendation> deepLearning = deepLearningRecommendation(userId, numRecommendations * 2);
        
        // スコア統合
        Map<Integer, Double> combinedScores = new HashMap<>();
        
        for (Recommendation rec : collaborative) {
            combinedScores.put(rec.getItemId(), 
                combinedScores.getOrDefault(rec.getItemId(), 0.0) + 0.4 * rec.getScore());
        }
        
        for (Recommendation rec : contentBased) {
            combinedScores.put(rec.getItemId(), 
                combinedScores.getOrDefault(rec.getItemId(), 0.0) + 0.3 * rec.getScore());
        }
        
        for (Recommendation rec : deepLearning) {
            combinedScores.put(rec.getItemId(), 
                combinedScores.getOrDefault(rec.getItemId(), 0.0) + 0.3 * rec.getScore());
        }
        
        // 多様性考慮
        return diversifyRecommendations(combinedScores, userId, numRecommendations);
    }
    
    private List<Recommendation> collaborativeFiltering(int userId, int numRecs) {
        // ユーザー類似度計算
        Map<Integer, Double> userSimilarities = calculateUserSimilarities(userId);
        
        // 近隣ユーザーの選択
        List<Integer> neighbors = userSimilarities.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(50)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // 推薦スコア計算
        Map<Integer, Double> scores = new HashMap<>();
        Map<Integer, Double> userRatings = userItemRatings.get(userId);
        
        for (Integer neighborId : neighbors) {
            Map<Integer, Double> neighborRatings = userItemRatings.get(neighborId);
            double similarity = userSimilarities.get(neighborId);
            
            for (Map.Entry<Integer, Double> entry : neighborRatings.entrySet()) {
                Integer itemId = entry.getKey();
                Double rating = entry.getValue();
                
                // 未評価アイテムのみ
                if (!userRatings.containsKey(itemId)) {
                    scores.put(itemId, scores.getOrDefault(itemId, 0.0) + 
                              similarity * rating);
                }
            }
        }
        
        return scores.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(numRecs)
            .map(entry -> new Recommendation(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
    
    private double calculatePearsonCorrelation(int user1, int user2) {
        Map<Integer, Double> ratings1 = userItemRatings.get(user1);
        Map<Integer, Double> ratings2 = userItemRatings.get(user2);
        
        // 共通評価アイテム
        Set<Integer> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());
        
        if (commonItems.size() < 2) return 0;
        
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;
        
        for (Integer itemId : commonItems) {
            double rating1 = ratings1.get(itemId);
            double rating2 = ratings2.get(itemId);
            
            sum1 += rating1;
            sum2 += rating2;
            sum1Sq += rating1 * rating1;
            sum2Sq += rating2 * rating2;
            pSum += rating1 * rating2;
        }
        
        double num = pSum - (sum1 * sum2 / commonItems.size());
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / commonItems.size()) *
                              (sum2Sq - sum2 * sum2 / commonItems.size()));
        
        return den == 0 ? 0 : num / den;
    }
    
    private List<Recommendation> diversifyRecommendations(Map<Integer, Double> scores, 
                                                         int userId, int numRecs) {
        List<Recommendation> result = new ArrayList<>();
        Set<Integer> selectedItems = new HashSet<>();
        
        while (result.size() < numRecs && selectedItems.size() < scores.size()) {
            double bestScore = Double.NEGATIVE_INFINITY;
            Integer bestItem = null;
            
            for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
                Integer itemId = entry.getKey();
                if (selectedItems.contains(itemId)) continue;
                
                // MMR スコア計算
                double relevance = entry.getValue();
                double diversity = calculateDiversityScore(itemId, selectedItems);
                double mmrScore = 0.7 * relevance + 0.3 * diversity;
                
                if (mmrScore > bestScore) {
                    bestScore = mmrScore;
                    bestItem = itemId;
                }
            }
            
            if (bestItem != null) {
                result.add(new Recommendation(bestItem, scores.get(bestItem)));
                selectedItems.add(bestItem);
            }
        }
        
        return result;
    }
}
```

## 🔍 応用のポイント

1. **Map構造の最適活用**: HashMap/TreeMapの特性を活かしたデータ構造設計
2. **アルゴリズムの効率化**: 大規模データに対する高速アルゴリズムの実装
3. **機械学習統合**: 実用的な機械学習アルゴリズムとの統合
4. **リアルタイム処理**: ストリーミングデータの効率的な処理
5. **システム設計**: スケーラブルで保守性の高いアーキテクチャ

## ✅ 完了チェックリスト

- [ ] 課題1: 多言語翻訳システムが正常に動作する
- [ ] 課題2: ソーシャルネットワーク分析システムが実装できた
- [ ] 課題3: 推薦システムエンジンが動作する
- [ ] Mapが効果的に活用されている
- [ ] アルゴリズムが正しく実装されている
- [ ] パフォーマンスが最適化されている

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！