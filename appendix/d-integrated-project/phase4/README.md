# フェーズ4: 例外処理

このフェーズでは、独自の例外クラスとエラー処理を実装し、誤ったデータが来ても止まらないアプリケーションを構築します。

## 実行方法

```bash
# コンパイル
javac *.java

# 実行
java Phase4Test
```

`Phase4Test`は、作業用のCSVファイル（`test_students.csv`、`exported_students.csv`）を作り、最後に削除します。
ログ（`java.util.logging`）はコンソールに日時付きで出力されます。

## 学習ポイント

- 独自の例外クラス（検査例外）の設計
- 標準の非検査例外（`IllegalArgumentException`）を、意味のわかる検査例外（`DuplicateStudentException`）に変換して投げ直す方法（元の例外は原因として引き継ぐ）
- ログ出力によるデバッグとトラブルシューティング
- ファイル入出力（第14章）での例外処理（try-with-resources）
- CSVの1行ごとのエラーを記録して次の行へ進み、成功とエラーの件数を返す方法

## ファイル構成

- `StudentNotFoundException.java`: 学生が見つからない場合の例外
- `DuplicateStudentException.java`: 学生IDが重複した場合の例外
- `InvalidGradeException.java`: 無効な成績値の例外
- `StudentService.java`: 例外処理とCSVのインポート・エクスポートを追加したサービスクラス
- `Phase4Test.java`: 例外処理を確かめるプログラム
- フェーズ3までのファイルも複製して含めている

## CSVファイルの形式

インポートするCSVファイルはUTF-8で保存し、1行目をヘッダーにします。

```
学生ID,氏名,年齢,学部
S002,山田花子,21,理学部
```

エラーのログに出る「行 N」は、ヘッダーを1行目とするファイルの行番号です。
