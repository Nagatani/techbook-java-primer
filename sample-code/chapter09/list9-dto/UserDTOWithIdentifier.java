/**
 * UserDTOWithIdentifierレコード
 * 
 * 元ファイル: chapter09-records.md (2298行目)
 * 
 * recordの組み合わせによる設計例
 */
public record UserDTOWithIdentifier(EntityIdentifier identifier, String username, String email) {
    public String id() {
        return identifier.id();
    }
}