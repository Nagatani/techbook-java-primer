/**
 * リストAC-17
 * DataProcessorクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (443行目)
 */

// 出力が次の入力になる処理の連鎖
public class DataProcessor {
    public ProcessedData process(RawData raw) {
        CleanedData cleaned = clean(raw);
        ValidatedData validated = validate(cleaned);
        return transform(validated);
    }
    
    private CleanedData clean(RawData raw) { /* */ }
    private ValidatedData validate(CleanedData cleaned) { /* */ }
    private ProcessedData transform(ValidatedData validated) { /* */ }
}