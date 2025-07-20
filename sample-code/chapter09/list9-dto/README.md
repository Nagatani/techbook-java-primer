# Records を使用したDTOパターンの実装

## 概要
Java RecordsとDTOパターンを組み合わせた実装例です。recordの継承制限を回避する方法も示しています。

## ファイル構成
- **Identifiable.java**: IDを持つオブジェクトのインターフェイス
- **UserDTO.java**: ユーザー情報のDTO（Identifiableを実装）
- **OrderDTO.java**: 注文情報のDTO（Identifiableを実装）
- **EntityIdentifier.java**: IDを持つrecord
- **UserDTOWithIdentifier.java**: 組み合わせによる設計例

## 設計パターン

### 1. インターフェイスによる共通化
```java
public interface Identifiable {
    String id();
}

public record UserDTO(String id, String username, String email) implements Identifiable {}
public record OrderDTO(String id, String userId, LocalDateTime orderDate) implements Identifiable {}
```

### 2. 組み合わせによる設計
```java
public record EntityIdentifier(String id) {}
public record UserDTOWithIdentifier(EntityIdentifier identifier, String username, String email) {
    public String id() {
        return identifier.id();
    }
}
```

## 学習ポイント
- recordはクラスを継承できない（finalクラス）
- recordはインターフェイスを実装できる
- 継承の代わりに組み合わせ（コンポジション）を使用
- DTOパターンの実装にrecordは最適

## 使用例
```java
// インターフェイスベース
UserDTO user = new UserDTO("USR-001", "john_doe", "john@example.com");
OrderDTO order = new OrderDTO("ORD-001", user.id(), LocalDateTime.now());

// 組み合わせベース
UserDTOWithIdentifier user2 = new UserDTOWithIdentifier(
    new EntityIdentifier("USR-002"), 
    "jane_doe", 
    "jane@example.com"
);
```