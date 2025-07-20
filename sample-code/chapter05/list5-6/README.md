# リスト5-6: 継承を使った子クラスの再実装

## 概要
リスト5-5で作成したDocumentクラスを継承し、各ドキュメント型に特化した機能を実装した例です。コードの重複を排除しつつ、各クラスの独自性を保っています。

## クラス構成
- **Document.java**: 親クラス（list5-5と同じ）
- **WordDocument.java**: Word文書クラス（Documentを継承）
- **PDFDocument.java**: PDF文書クラス（Documentを継承、saveメソッドをオーバーライド）
- **ExcelDocument.java**: Excel文書クラス（Documentを継承、saveメソッドをオーバーライド）

## 実装のポイント
1. **super()の使用**: 親クラスのコンストラクタを適切に呼び出し
2. **@Overrideアノテーション**: メソッドのオーバーライドを明示
3. **固有機能の追加**: 各子クラスに特有のメソッドを追加
   - WordDocument: applyTemplate()
   - PDFDocument: generatePDF()
   - ExcelDocument: createChart()
4. **メソッドのカスタマイズ**: 必要に応じてsave()メソッドをオーバーライド

## 改善効果
- コード重複の完全な排除
- 各クラスの責務が明確
- 新しいドキュメント型の追加が容易
- 保守性の大幅な向上