# 第23章 ビルドとデプロイ - 解答例

本ディレクトリには、第23章の課題に対する解答例が含まれています。

## プロジェクト構成

```
solutions/
├── CalculatorApp.java      # 課題1: シンプルな計算機アプリ
├── manifest.txt            # 課題1: マニフェストファイル
├── build.sh               # 課題1: Unix/Linux/Mac用ビルドスクリプト
├── build.bat              # 課題1: Windows用ビルドスクリプト
├── MessageApp.java        # 課題2: リソース付きGUIアプリ
├── resources/             # 課題2: リソースファイル
│   ├── config.properties
│   ├── messages.properties
│   ├── messages_ja.properties
│   ├── messages_en.properties
│   └── icon.png (実際には含まれていません)
├── JsonTool.java          # 課題3: 外部ライブラリ使用アプリ
├── build-fatjar.sh        # 課題3: Fat JAR作成スクリプト
└── README.md              # このファイル
```

## 課題1: シンプルな実行可能JARの作成

### 実装のポイント
- コマンドライン引数の処理
- BigDecimalを使用した高精度計算
- エラーハンドリング（ゼロ除算、無効な引数）
- 使いやすいヘルプメッセージ

### ビルドと実行

**Unix/Linux/Mac:**
```bash
# ビルドスクリプトの実行
./build.sh

# または手動でビルド
javac -d build CalculatorApp.java
cd build
jar cfm ../dist/calculator.jar ../manifest.txt chapter23/solutions/*.class
cd ..

# 実行
java -jar dist/calculator.jar add 10 20
java -jar dist/calculator.jar multiply 5.5 3.2
```

**Windows:**
```batch
rem ビルドスクリプトの実行
build.bat

rem 実行
java -jar dist\calculator.jar add 10 20
```

### ビルドスクリプトの特徴
- クリーン処理（前回のビルド結果を削除）
- エラーチェック付きコンパイル
- JARファイルの作成と検証
- 自動テストの実行
- カラー出力（Unix/Linux/Mac版）

## 課題2: リソースファイルを含むJARの作成

### 実装のポイント
- クラスパスからのリソース読み込み
- ResourceBundleを使用した国際化
- プロパティファイルによる設定管理
- 動的な言語切り替え
- プログラムによるデフォルトアイコン生成

### リソースの構成
```
resources/
├── config.properties       # アプリケーション設定
├── messages.properties     # デフォルトメッセージ（日本語）
├── messages_ja.properties  # 日本語メッセージ
├── messages_en.properties  # 英語メッセージ
└── icon.png               # アプリケーションアイコン（要追加）
```

### ビルド方法
```bash
# コンパイル
javac -d build MessageApp.java

# リソースをコピー
cp -r resources build/

# マニフェストファイルを作成
echo "Manifest-Version: 1.0" > manifest-gui.txt
echo "Main-Class: chapter23.solutions.MessageApp" >> manifest-gui.txt

# JARファイルを作成
cd build
jar cfm ../message-app.jar ../manifest-gui.txt *
cd ..

# 実行
java -jar message-app.jar
```

### リソース読み込みのベストプラクティス
1. `getClass().getResourceAsStream()`を使用
2. リソースパスは`/`で始める
3. 存在しない場合のフォールバック処理
4. try-with-resourcesでストリームを管理

## 課題3: 外部ライブラリを含むFat JARの作成

### 実装のポイント
- Apache Commons CLIによるコマンドライン解析
- GsonによるJSON処理
- JSON⇔CSV相互変換
- エラーハンドリングと検証

### 使用ライブラリ
- Gson 2.10.1
- Apache Commons CLI 1.6.0

### Fat JAR作成方法

**自動ビルド:**
```bash
# ビルドスクリプトの実行（ライブラリの自動ダウンロード含む）
./build-fatjar.sh
```

**手動ビルド:**
```bash
# 1. ライブラリをダウンロード
mkdir lib
curl -L https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar -o lib/gson.jar
curl -L https://repo1.maven.org/maven2/commons-cli/commons-cli/1.6.0/commons-cli-1.6.0.jar -o lib/commons-cli.jar

# 2. コンパイル
javac -cp "lib/*" -d build JsonTool.java

# 3. Fat JARの作成
mkdir temp
cd temp
cp -r ../build/* .
jar xf ../lib/gson.jar
jar xf ../lib/commons-cli.jar
rm -rf META-INF/*.SF META-INF/*.DSA META-INF/*.RSA

# 4. マニフェストを作成
echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF
echo "Main-Class: chapter23.solutions.JsonTool" >> META-INF/MANIFEST.MF

# 5. JARファイルを作成
jar cf ../json-tool.jar *
cd ..
rm -rf temp
```

### 使用例

```bash
# JSONファイルの整形
java -jar json-tool.jar format input.json output.json

# JSONファイルの検証
java -jar json-tool.jar validate data.json

# JSONからCSVへの変換
java -jar json-tool.jar convert --from json --to csv users.json users.csv

# CSVからJSONへの変換
java -jar json-tool.jar convert --from csv --to json users.csv users.json

# ヘルプの表示
java -jar json-tool.jar --help
```

### サンプルデータ

**input.json:**
```json
[
  {"id": 1, "name": "Alice", "age": 25, "email": "alice@example.com"},
  {"id": 2, "name": "Bob", "age": 30, "email": "bob@example.com"},
  {"id": 3, "name": "Charlie", "age": 35, "email": "charlie@example.com"}
]
```

**output.csv:**
```csv
id,name,age,email
1,Alice,25,alice@example.com
2,Bob,30,bob@example.com
3,Charlie,35,charlie@example.com
```

## 学習のポイント

### 1. JARファイルの基礎
- マニフェストファイルの役割
- Main-Classの指定
- クラスパスの管理

### 2. リソース管理
- JARファイル内のリソースアクセス
- 国際化とリソースバンドル
- 設定ファイルの活用

### 3. 依存関係管理
- 外部ライブラリの組み込み
- Fat JAR vs 通常のJAR
- Class-Pathマニフェスト属性

### 4. ビルド自動化
- シェルスクリプト/バッチファイル
- エラーハンドリング
- クロスプラットフォーム対応

## 発展的な学習

1. **ビルドツールの活用**
   - Maven/Gradleによる自動化
   - 依存関係の宣言的管理
   - プラグインによる拡張

2. **配布形式の選択**
   - ネイティブインストーラー（jpackage）
   - Web Start/JNLP
   - Docker コンテナ

3. **継続的インテグレーション**
   - 自動ビルド
   - 自動テスト
   - リリース管理

## トラブルシューティング

### よくある問題と解決方法

1. **「Main-Classが見つかりません」エラー**
   - マニフェストファイルの最後に改行があることを確認
   - パッケージ名を含む完全修飾クラス名を指定

2. **リソースが読み込めない**
   - リソースパスが正しいか確認（先頭の`/`）
   - JARファイル内にリソースが含まれているか確認

3. **NoClassDefFoundError**
   - 依存ライブラリがクラスパスに含まれているか確認
   - Fat JARの場合、すべてのクラスが展開されているか確認

4. **文字化け**
   - ソースファイルのエンコーディング（UTF-8）
   - プロパティファイルのエンコーディング
   - コンパイル時の`-encoding`オプション

## まとめ

JARファイルの作成と配布は、Javaアプリケーション開発の重要なスキルです。
基本的な実行可能JARから、リソースを含むアプリケーション、外部ライブラリを
統合したFat JARまで、様々な形式を理解することで、適切な配布方法を
選択できるようになります。