/**
 * UserDTOレコード
 * 
 * 元ファイル: chapter09-records.md (2314行目)
 * 
 * DTOパターンをrecordで実装した例
 */
public record UserDTO(String id, String username, String email) implements Identifiable {}