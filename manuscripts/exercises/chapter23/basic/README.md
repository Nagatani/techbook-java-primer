# 第23章 基礎課題: JARファイルの作成と配布

## 課題1: シンプルな実行可能JARの作成

### 背景
基本的なJavaアプリケーションを実行可能なJARファイルとしてパッケージングする方法を学びます。

### 要件
以下の仕様を満たす計算機アプリケーションを作成し、実行可能JARとして配布してください。

#### アプリケーションの仕様
```java
public class CalculatorApp {
    public static void main(String[] args) {
        // コマンドライン引数から計算を実行
        // 例: java -jar calculator.jar add 10 20
        // 例: java -jar calculator.jar multiply 5 6
    }
}
```

サポートする操作:
- `add`: 加算
- `subtract`: 減算
- `multiply`: 乗算
- `divide`: 除算（ゼロ除算のエラー処理含む）

### 実装手順
1. ソースコードの作成
2. コンパイル（`javac`）
3. マニフェストファイルの作成
4. JARファイルの作成（`jar`コマンド）
5. 実行テスト

### 提出物
- ソースコード（`CalculatorApp.java`）
- マニフェストファイル（`manifest.txt`）
- ビルドスクリプト（`build.sh`または`build.bat`）
- 作成されたJARファイル
- 実行例のスクリーンショット

---

## 課題2: リソースファイルを含むJARの作成

### 背景
実際のアプリケーションには、設定ファイルや画像などのリソースが含まれます。

### 要件
設定ファイルとアイコンを含むGUIアプリケーションを作成してください。

#### プロジェクト構造
```
project/
├── src/
│   ├── MessageApp.java
│   └── resources/
│       ├── config.properties
│       ├── messages_ja.properties
│       ├── messages_en.properties
│       └── icon.png
└── build/
```

#### 機能要件
1. 設定ファイルから言語設定を読み込む
2. 選択された言語でメッセージを表示
3. ウィンドウにアイコンを設定

#### config.properties
```properties
app.title=Message Viewer
app.version=1.0.0
app.language=ja
```

#### MessageApp.java（骨組み）
```java
public class MessageApp extends JFrame {
    private Properties config;
    private ResourceBundle messages;
    
    public MessageApp() {
        loadConfiguration();
        loadMessages();
        initializeUI();
    }
    
    private void loadConfiguration() {
        // クラスパスからconfig.propertiesを読み込む
    }
    
    private void loadMessages() {
        // 設定された言語のメッセージを読み込む
    }
    
    private void initializeUI() {
        // GUIの初期化、アイコンの設定
    }
}
```

### 評価ポイント
- リソースの正しい読み込み方法
- JARファイル内でのリソースアクセス
- 国際化対応の実装

---

## 課題3: 外部ライブラリを含むFat JARの作成

### 背景
外部ライブラリを使用するアプリケーションの配布方法を学びます。

### 要件
JSONデータを処理するコンソールアプリケーションを作成してください。

#### 使用ライブラリ
- Gson（JSONパース）
- Apache Commons CLI（コマンドライン解析）

#### 機能要件
```bash
# JSONファイルの整形
java -jar json-tool.jar format input.json output.json

# JSONの検証
java -jar json-tool.jar validate data.json

# JSONからCSVへの変換
java -jar json-tool.jar convert --from json --to csv data.json data.csv
```

### 実装のヒント
1. ライブラリのJARファイルをダウンロード
2. すべてのクラスを1つのJARにまとめる方法を調査
3. または、Class-Pathをマニフェストに設定

### 提出物
- 完全なソースコード
- 依存ライブラリの管理方法の説明
- Fat JARの作成手順書
- 動作確認のスクリーンショット

---

## 課題4: ビルド自動化スクリプトの作成

### 背景
手動でのビルドは面倒でエラーが起きやすいため、自動化が重要です。

### 要件
課題1-3のいずれかのプロジェクトに対して、ビルド自動化スクリプトを作成してください。

#### スクリプトの機能
1. クリーン（前回のビルド結果を削除）
2. コンパイル
3. リソースのコピー
4. JARファイルの作成
5. 簡単な動作テスト

#### Windows用（build.bat）
```batch
@echo off
echo Building Calculator App...

rem クリーン
if exist build rmdir /s /q build
mkdir build

rem コンパイル
echo Compiling...
javac -d build src/*.java

rem 以下、続きを実装
```

#### Unix/Linux/Mac用（build.sh）
```bash
#!/bin/bash
echo "Building Calculator App..."

# クリーン
rm -rf build
mkdir -p build

# コンパイル
echo "Compiling..."
javac -d build src/*.java

# 以下、続きを実装
```

### 追加課題
- エラー処理の実装
- ビルド番号の自動インクリメント
- ビルド日時の記録

## 提出方法
1. 各課題ごとにディレクトリを作成
2. READMEファイルに実行手順を記載
3. ビルドと実行の様子を動画で録画（オプション）

## 評価基準
- JARファイルが正しく作成されているか
- 実行可能で、期待通りに動作するか
- リソースファイルが適切に含まれているか
- ドキュメントの充実度