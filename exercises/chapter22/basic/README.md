# 第22章 基礎課題 - ドキュメンテーションとライブラリ

## 課題概要
Javadocとドキュメンテーションの基礎を学ぶための課題です。

## 課題リスト

### 課題1: Javadocコメントの作成
`MathUtils.java`クラスに完全なJavadocコメントを追加してください。

**要件:**
- クラスレベルのドキュメント
- メソッドの説明、パラメータ、戻り値、例外
- 使用例を含む`@code`タグ
- `@since`、`@author`タグの使用

**サンプルコード:**
```java
/**
 * 数学的なユーティリティメソッドを提供するクラス
 * 
 * <p>このクラスは基本的な数学演算を提供します。
 * すべてのメソッドは静的メソッドとして実装されています。</p>
 * 
 * <p>使用例:</p>
 * <pre>{@code
 * int result = MathUtils.factorial(5);
 * System.out.println(result); // 120
 * }</pre>
 * 
 * @author Your Name
 * @since 1.0
 * @version 1.0
 */
public class MathUtils {
    // メソッドの実装
}
```

### 課題2: APIドキュメントの設計
`StringProcessor.java`インターフェースのAPIドキュメントを設計してください。

**要件:**
- インターフェースの目的と使用場面の説明
- メソッドコントラクトの明確な記述
- 実装時の注意事項
- 関連クラスへの参照

### 課題3: 使用例の作成
作成したクラスの使用例を示すサンプルコードを作成してください。

**要件:**
- 実践的な使用シナリオ
- エラーハンドリングの例
- ベストプラクティスの提示

### 課題4: READMEファイルの作成
ライブラリのREADME.mdファイルを作成してください。

**含めるべき内容:**
- プロジェクトの概要
- インストール方法
- 基本的な使用方法
- APIリファレンスへのリンク
- ライセンス情報

## Javadocの生成方法

```bash
# 単一ファイルの場合
javadoc -d docs MathUtils.java

# パッケージ全体の場合
javadoc -d docs -sourcepath src -subpackages com.example

# より詳細な設定
javadoc -d docs \
  -windowtitle "My Library API" \
  -doctitle "My Library Documentation" \
  -header "My Library v1.0" \
  -author \
  -version \
  -use \
  MathUtils.java
```

## 評価基準
- Javadocコメントの完全性
- 説明の明確さと正確さ
- 使用例の実用性
- ドキュメント構成の論理性