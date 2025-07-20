# 抽象クラスとインターフェイスの適切な使い分け例

## 概要
サービス管理システムにおける抽象クラスとインターフェイスの適切な使い分けを示す例です。

## ファイル構成
- **BaseService.java**: 抽象クラス。共通の状態（フィールド）と実装を持つ
- **Monitorable.java**: インターフェイス。行動の契約のみを定義
- **EmailService.java**: BaseServiceを継承し、Monitorableを実装

## 設計のポイント

### 抽象クラス（BaseService）を使う理由
1. **共通の状態を持つ**: serviceId、isActive、startTime
2. **共通の実装がある**: getUptime()メソッドなど
3. **is-a関係**: EmailService is-a BaseService

### インターフェイス（Monitorable）を使う理由
1. **状態を持たない**: 純粋に振る舞いの契約のみ
2. **can-do関係**: EmailService can-do Monitoring
3. **複数実装可能**: 他のインターフェイスも同時に実装できる

## 学習ポイント
- 状態（フィールド）と共通実装がある場合は抽象クラス
- 振る舞いの契約のみを定義する場合はインターフェイス
- クラスは1つの抽象クラスを継承し、複数のインターフェイスを実装可能
- defaultメソッドにより、インターフェイスでも共通実装を提供可能