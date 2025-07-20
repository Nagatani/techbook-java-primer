# リスト7-5: 複数インターフェイスの実装

## 概要
Documentクラスが複数のインターフェイス（ExportableとArchivable）を実装する例です。これにより、ドキュメントはエクスポート機能とアーカイブ機能の両方を持つことができます。

## ファイル構成
- **Document.java**: ExportableとArchivableを実装するクラス
- **Exportable.java**: エクスポート機能のインターフェイス
- **Archivable.java**: アーカイブ機能のインターフェイス

## 実装のポイント
1. **複数インターフェイスの実装**: `implements Exportable, Archivable`
2. **すべてのメソッドの実装**: 両インターフェイスのメソッドを実装
3. **@Overrideアノテーション**: インターフェイスメソッドの実装を明示

## 学習ポイント
- Javaのクラスは複数のインターフェイスを実装可能（多重継承の代替）
- 各インターフェイスのすべての抽象メソッドを実装する必要がある
- インターフェイスにより、異なる機能を柔軟に組み合わせられる

## 使用例
```java
Document doc = new Document("技術仕様書", "内容...");
doc.exportToFormat("PDF");        // Exportable機能
doc.archiveToStorage("クラウド");  // Archivable機能
```