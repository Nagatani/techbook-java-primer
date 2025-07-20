# フェーズ6: データベース統合

このフェーズでは、SQLiteデータベースを使用してデータの永続化を実装します。

## 実行方法

```bash
# コンパイル（SQLite JDBCドライバが必要）
javac -cp ".:sqlite-jdbc-3.xx.x.jar" *.java

# 実行
java -cp ".:sqlite-jdbc-3.xx.x.jar" Phase6Test

# GUIアプリケーションの実行
java -cp ".:sqlite-jdbc-3.xx.x.jar" StudentManagementGUI
```

注：SQLite JDBCドライバは以下からダウンロードできます：
https://github.com/xerial/sqlite-jdbc/releases

## 学習ポイント

- JDBCを使用したデータベース接続
- SQLの実行（CREATE, INSERT, SELECT, UPDATE, DELETE）
- トランザクション管理
- PreparedStatementによるSQLインジェクション対策
- データベースとオブジェクトのマッピング

## ファイル構成

- `DatabaseManager.java`: データベース操作クラス
- `StudentService.java`: データベース対応版サービスクラス
- `Phase6Test.java`: データベース統合のテストプログラム
- `StudentTest.java`: 単体テストの例
- （フェーズ5のクラスファイルも必要）

## データベース構造

- `students`: 学生基本情報テーブル
- `grades`: 成績情報テーブル
- `graduate_students`: 大学院生追加情報テーブル