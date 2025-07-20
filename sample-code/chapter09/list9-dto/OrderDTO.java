/**
 * OrderDTOレコード
 * 
 * 元ファイル: chapter09-records.md (2315行目)
 * 
 * DTOパターンをrecordで実装した例
 */
import java.time.LocalDateTime;

public record OrderDTO(String id, String userId, LocalDateTime orderDate) implements Identifiable {}