/**
 * リスト9-39
 * InteroperabilityEnhancementsクラス
 * 
 * 元ファイル: chapter09-records.md (1856行目)
 */

// 他言語・フレームワークとの統合改善
public class InteroperabilityEnhancements {
    
    // Native interopの改善
    @Foreign
    public record NativePoint(int x, int y) {
        // C/C++構造体との直接マッピング
    }
    
    // JSON Schemaとの自動統合
    @JsonSchema
    public record ApiRequest(
        @NotNull String operation,
        @Valid RequestData data,
        @Pattern(regexp = "v\\d+") String version
    ) {}
    
    // データベースマッピングの改善
    @Entity
    public record UserEntity(
        @Id String id,
        @Column("user_name") String name,
        @Column("created_at") Instant createdAt
    ) {}
}