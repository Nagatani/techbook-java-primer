# フェーズ2: 継承とポリモーフィズム

このフェーズでは、継承、抽象クラス、インターフェイスを使用してより高度なオブジェクト指向設計を実装します。

## 実行方法

```bash
# コンパイル
javac *.java

# 実行
java Phase2Test
```

## 学習ポイント

- 抽象クラス（Person）による共通機能の定義
- 継承による機能の拡張（Student、GraduateStudent）
- インターフェイス（Gradeable）による契約の定義
- ポリモーフィズムの活用

## ファイル構成

- `Person.java`: 抽象基底クラス
- `Student.java`: Personを継承した学生クラス
- `GraduateStudent.java`: Studentを継承した大学院生クラス
- `Gradeable.java`: 成績管理インターフェイス
- `GradeableStudent.java`: Gradeableを実装した学生クラス
- `Phase2Test.java`: 動作確認用のテストプログラム