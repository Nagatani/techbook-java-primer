# 第7章 発展課題：抽象クラスとインターフェイスの応用

## 課題概要

基礎課題で学んだ概念をさらに発展させ、より実践的で複雑な設計パターンを実装します。Java 8以降の高度な機能を活用し、プロダクションレベルのコード設計を学びます。

## 課題1：ゲームキャラクターシステム

### 目的
- 複雑な継承階層の設計
- Mixinパターンの実装
- インターフェイスのdefaultメソッドを使った機能の組み合わせ

### 要求仕様

1. **基本設計**
   ```java
   // 基本的な能力を表すインターフェイス
   interface HealthSystem {
       int getMaxHealth();
       int getCurrentHealth();
       void takeDamage(int damage);
       
       default boolean isAlive() {
           return getCurrentHealth() > 0;
       }
       
       default double getHealthPercentage() {
           return (double) getCurrentHealth() / getMaxHealth() * 100;
       }
   }
   
   interface MagicUser {
       int getMana();
       void useMana(int amount);
       
       default boolean canCastSpell(int manaCost) {
           return getMana() >= manaCost;
       }
   }
   
   interface PhysicalAttacker {
       int getAttackPower();
       
       default int calculateDamage(boolean isCritical) {
           return isCritical ? getAttackPower() * 2 : getAttackPower();
       }
   }
   ```

2. **抽象クラス`GameCharacter`**
   - 基本的なキャラクター情報（名前、レベル）
   - レベルアップシステム
   - 経験値管理

3. **複合的なキャラクタークラス**
   - `Warrior`：PhysicalAttacker、HealthSystem
   - `Mage`：MagicUser、HealthSystem
   - `Paladin`：PhysicalAttacker、MagicUser、HealthSystem（ハイブリッド）

### 実装のヒント
- インターフェイスの多重継承を活用
- defaultメソッドでの共通機能の提供
- ダイヤモンド継承問題への対処

### 評価ポイント
- 複雑な継承関係の適切な設計
- Mixinパターンの理解と実装
- defaultメソッドの効果的な活用
- コードの保守性と拡張性

## 課題2：イベント駆動システム

### 目的
- オブザーバーパターンの実装
- 関数型インターフェイスとの組み合わせ
- ジェネリクスを使った型安全な設計

### 要求仕様

1. **イベントシステムの設計**
   ```java
   interface EventListener<T extends Event> {
       void onEvent(T event);
       
       default int getPriority() {
           return 0;  // デフォルト優先度
       }
   }
   
   interface EventEmitter<T extends Event> {
       void addEventListener(EventListener<T> listener);
       void removeEventListener(EventListener<T> listener);
       void emit(T event);
   }
   ```

2. **イベントタイプ**
   - `MouseEvent`：マウス操作イベント
   - `KeyboardEvent`：キーボード操作イベント
   - `GameEvent`：ゲーム内イベント（レベルアップ、アイテム取得等）

3. **実装要件**
   - 優先度に基づくイベント処理
   - イベントのキャンセル機能
   - イベントの伝播制御

### 評価ポイント
- デザインパターンの適切な実装
- ジェネリクスを使った型安全性
- 拡張可能なイベントシステム
- パフォーマンスを考慮した実装

## 課題3：データ検証フレームワーク

### 目的
- アノテーションとの連携
- チェーン・オブ・レスポンシビリティパターン
- 流暢なインターフェイス（Fluent Interface）の実装

### 要求仕様

1. **検証インターフェイス**
   ```java
   interface Validator<T> {
       ValidationResult validate(T value);
       
       default Validator<T> and(Validator<T> other) {
           return value -> {
               ValidationResult result = this.validate(value);
               if (!result.isValid()) {
                   return result;
               }
               return other.validate(value);
           };
       }
       
       default Validator<T> or(Validator<T> other) {
           return value -> {
               ValidationResult result = this.validate(value);
               if (result.isValid()) {
                   return result;
               }
               return other.validate(value);
           };
       }
   }
   ```

2. **具体的な検証ルール**
   - `StringValidator`：文字列の長さ、パターンマッチング
   - `NumberValidator`：範囲チェック、整数/小数チェック
   - `EmailValidator`：メールアドレスの形式チェック
   - `CompositeValidator`：複数の検証を組み合わせ

3. **使用例**
   ```java
   Validator<String> passwordValidator = StringValidator.minLength(8)
       .and(StringValidator.containsUpperCase())
       .and(StringValidator.containsDigit())
       .and(StringValidator.containsSpecialChar());
   ```

### 評価ポイント
- 流暢なインターフェイスの設計
- 関数型プログラミングの活用
- 再利用可能なコンポーネント設計
- エラーメッセージの適切な管理

## 提出方法

1. 各課題の完全な実装
2. ユニットテストの作成（JUnit使用）
3. 設計ドキュメント（クラス図、設計意図の説明）
4. パフォーマンステストの結果（該当する場合）

## 発展学習の提案

- **高度なデザインパターン**：ビジターパターン、デコレーターパターンの実装
- **リフレクションAPI**：インターフェイスとアノテーションの動的な処理
- **関数型プログラミング**：高階関数としてのインターフェイス活用
- **並行プログラミング**：スレッドセーフなインターフェイス設計

## 参考リソース

- Java言語仕様（JLS）：インターフェイスの章
- Modern Java in Action（Raoul-Gabriel Urma他）
- Java Concurrency in Practice（Brian Goetz他）