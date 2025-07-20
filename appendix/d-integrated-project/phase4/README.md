# フェーズ4: 例外処理

このフェーズでは、カスタム例外とエラーハンドリングを実装し、堅牢なアプリケーションを構築します。

## 実行方法

```bash
# コンパイル
javac *.java

# 実行
java Phase4Test
```

## 学習ポイント

- カスタム例外クラスの設計
- checked例外とunchecked例外の使い分け
- ログ出力によるデバッグとトラブルシューティング
- ファイルI/O操作での例外処理
- エラー回復処理の実装

## ファイル構成

- `StudentNotFoundException.java`: 学生が見つからない場合の例外
- `DuplicateStudentException.java`: 学生IDが重複した場合の例外
- `InvalidGradeException.java`: 無効な成績値の例外
- `StudentService.java`: 例外処理を追加したサービスクラス
- `Phase4Test.java`: 例外処理のテストプログラム
- （フェーズ3のクラスファイルも必要）