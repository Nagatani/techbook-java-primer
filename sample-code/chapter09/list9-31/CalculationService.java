/**
 * リスト9-31
 * CalculationServiceクラス
 * 
 * 元ファイル: chapter09-records.md (1433行目)
 */

// 短命なオブジェクトとしてのRecord活用
public class CalculationService {
    
    public record CalculationResult(double value, boolean isValid, String message) {}
    
    public CalculationResult calculate(double input) {
        if (input < 0) {
            return new CalculationResult(0.0, false, "Negative input not allowed");
        }
        
        double result = Math.sqrt(input) * 2.5;
        return new CalculationResult(result, true, "Success");
    }
    
    // 大量のRecord作成でもGC効率が良い
    public List<CalculationResult> processBatch(List<Double> inputs) {
        return inputs.stream()
            .map(this::calculate)
            .collect(Collectors.toList());
    }
}