# セキュリティポリシー

## サポートされているバージョン

以下のバージョンが現在セキュリティアップデートでサポートされています：

| バージョン | サポート状況     |
| ---------- | ---------------- |
| 1.0.x      | :white_check_mark: |
| < 1.0      | :x:              |

## 脆弱性の報告

このプロジェクトでセキュリティの脆弱性を発見した場合は、責任を持って開示していただくようお願いします。

### 報告方法

1. **公開しない**: GitHub Issuesやその他の公開フォーラムでセキュリティの脆弱性を報告しないでください。

2. **メールで報告**: security@example.com に以下の情報を含めてメールを送信してください：
   - 脆弱性の説明
   - 再現手順
   - 影響を受けるバージョン
   - 可能であれば、修正案

3. **暗号化**: 可能であれば、以下のPGP公開鍵を使用してメールを暗号化してください：
   ```
   -----BEGIN PGP PUBLIC KEY BLOCK-----
   [PGP公開鍵をここに挿入]
   -----END PGP PUBLIC KEY BLOCK-----
   ```

### 対応プロセス

1. **確認**: 48時間以内に報告の受領を確認します
2. **調査**: 脆弱性を調査し、影響を評価します
3. **修正**: パッチを開発し、内部でテストします
4. **通知**: 修正の準備ができたら報告者に通知します
5. **公開**: セキュリティアドバイザリを公開し、パッチをリリースします

### 期待されるタイムライン

- **初回応答**: 48時間以内
- **調査完了**: 7日以内
- **パッチ開発**: 重要度に応じて14-30日
- **公開開示**: パッチリリース後

## セキュリティベストプラクティス

### 依存関係の管理

1. **定期的な更新**: 依存関係を定期的に更新します
   ```bash
   mvn versions:display-dependency-updates
   mvn versions:use-latest-versions
   ```

2. **脆弱性スキャン**: OWASP Dependency Checkを使用
   ```bash
   mvn dependency-check:check
   ```

3. **ライセンスチェック**: ライセンスの互換性を確認
   ```bash
   mvn license:check
   ```

### コードセキュリティ

1. **静的解析**: SpotBugsとSonarQubeを使用
   ```bash
   mvn spotbugs:check
   ```

2. **セキュアコーディング**:
   - 入力検証を必ず実施
   - SQLインジェクション対策
   - XSS対策
   - 適切な認証・認可

### 例：セキュアな入力検証

```java
public class SecureValidator {
    private static final Pattern SAFE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final int MAX_LENGTH = 255;
    
    public static String validateInput(String input) {
        Objects.requireNonNull(input, "Input cannot be null");
        
        // 長さチェック
        if (input.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Input too long");
        }
        
        // パターンチェック
        if (!SAFE_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("Invalid characters in input");
        }
        
        // サニタイズ
        return input.trim();
    }
}
```

## セキュリティチェックリスト

新しいコードをマージする前に：

- [ ] 入力検証が実装されている
- [ ] 機密情報がログに出力されていない
- [ ] 依存関係が最新である
- [ ] セキュリティテストが追加されている
- [ ] SpotBugsの警告がない
- [ ] OWASP Dependency Checkが成功する

## セキュリティ関連のリソース

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [CWE/SANS Top 25](https://cwe.mitre.org/top25/)
- [CERT Secure Coding Standards](https://www.securecoding.cert.org/)
- [Java Security Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)

## 責任ある開示

私たちは責任ある開示を実践し、以下を約束します：

1. セキュリティ研究者の作業を尊重します
2. 善意で行動する研究者に対して法的措置を取りません
3. 修正が利用可能になった後、研究者にクレジットを提供します

## 連絡先

セキュリティチーム: security@example.com

緊急の場合: security-urgent@example.com