import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class DateTimeValidation {
    public static void main(String[] args) {
        System.out.println("=== 日付時刻の妥当性検証デモ ===\n");
        
        // 1. 基本的な日付妥当性チェック
        demonstrateBasicValidation();
        
        // 2. 厳密な日付検証
        demonstrateStrictValidation();
        
        // 3. カスタム検証ロジック
        demonstrateCustomValidation();
        
        // 4. 対話的な日付入力検証
        demonstrateInteractiveValidation();
    }
    
    private static void demonstrateBasicValidation() {
        System.out.println("1. 基本的な日付妥当性チェック");
        System.out.println("-".repeat(40));
        
        String[] testDates = {
            "2024-02-29",  // うるう年の2月29日
            "2023-02-29",  // 平年の2月29日（無効）
            "2024-13-01",  // 無効な月
            "2024-12-32",  // 無効な日
            "2024-00-15",  // 月が0
            "2024-12-00"   // 日が0
        };
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        for (String dateStr : testDates) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                System.out.println(dateStr + " → 有効 (" + date + ")");
            } catch (DateTimeParseException e) {
                System.out.println(dateStr + " → 無効 (理由: " + 
                    e.getMessage().split("\n")[0] + ")");
            }
        }
        System.out.println();
    }
    
    private static void demonstrateStrictValidation() {
        System.out.println("2. 厳密な日付検証（ResolverStyle.STRICT使用）");
        System.out.println("-".repeat(40));
        
        // 厳密なフォーマッタ（日付の妥当性を厳格にチェック）
        DateTimeFormatter strictFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
        
        // 寛容なフォーマッタ（ある程度の補正を許可）
        DateTimeFormatter lenientFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withResolverStyle(ResolverStyle.LENIENT);
        
        String[] testCases = {
            "2024-02-30",  // 2月30日
            "2024-04-31",  // 4月31日
            "2024-12-32"   // 12月32日
        };
        
        for (String dateStr : testCases) {
            System.out.println("\n入力: " + dateStr);
            
            // 厳密な検証
            try {
                LocalDate strictDate = LocalDate.parse(dateStr, strictFormatter);
                System.out.println("  STRICT: 有効 (" + strictDate + ")");
            } catch (DateTimeParseException e) {
                System.out.println("  STRICT: 無効");
            }
            
            // 寛容な検証
            try {
                LocalDate lenientDate = LocalDate.parse(dateStr, lenientFormatter);
                System.out.println("  LENIENT: 補正されて有効 (" + lenientDate + ")");
            } catch (DateTimeParseException e) {
                System.out.println("  LENIENT: 無効");
            }
        }
        System.out.println();
    }
    
    private static void demonstrateCustomValidation() {
        System.out.println("3. カスタム検証ロジック");
        System.out.println("-".repeat(40));
        
        // テストケース
        LocalDate[] dates = {
            LocalDate.of(2024, 1, 1),    // 月曜日
            LocalDate.of(2024, 1, 6),    // 土曜日
            LocalDate.of(2024, 1, 7),    // 日曜日
            LocalDate.of(2024, 12, 25),  // クリスマス
            LocalDate.of(2100, 1, 1),    // 遠い未来
            LocalDate.of(1900, 1, 1)     // 遠い過去
        };
        
        for (LocalDate date : dates) {
            System.out.println("\n日付: " + date + " (" + 
                date.getDayOfWeek() + ")");
            System.out.println("  営業日？ " + isBusinessDay(date));
            System.out.println("  予約可能？ " + isReservationAvailable(date));
            System.out.println("  有効範囲内？ " + isWithinValidRange(date));
        }
        System.out.println();
    }
    
    private static void demonstrateInteractiveValidation() {
        System.out.println("4. 対話的な日付入力検証");
        System.out.println("-".repeat(40));
        System.out.println("日付を入力してください（形式: yyyy-MM-dd）");
        System.out.println("終了するには 'quit' と入力してください。\n");
        
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE
            .withResolverStyle(ResolverStyle.STRICT);
        
        while (true) {
            System.out.print("日付を入力: ");
            String input = scanner.nextLine().trim();
            
            if ("quit".equalsIgnoreCase(input)) {
                break;
            }
            
            ValidationResult result = validateDateInput(input, formatter);
            
            if (result.isValid()) {
                LocalDate date = result.getDate();
                System.out.println("✓ 有効な日付です: " + date);
                System.out.println("  曜日: " + date.getDayOfWeek());
                System.out.println("  今日から: " + 
                    Period.between(LocalDate.now(), date));
                System.out.println("  年齢計算: " + 
                    calculateAge(date) + "歳");
            } else {
                System.out.println("✗ 無効な日付です: " + result.getErrorMessage());
            }
            System.out.println();
        }
        
        scanner.close();
    }
    
    // カスタム検証メソッド
    private static boolean isBusinessDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && 
               dayOfWeek != DayOfWeek.SUNDAY &&
               !isJapaneseHoliday(date);
    }
    
    private static boolean isJapaneseHoliday(LocalDate date) {
        // 簡易的な祝日判定（実際にはより複雑な実装が必要）
        return (date.getMonthValue() == 1 && date.getDayOfMonth() == 1) ||  // 元日
               (date.getMonthValue() == 12 && date.getDayOfMonth() == 25);  // クリスマス
    }
    
    private static boolean isReservationAvailable(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate maxReservationDate = today.plusMonths(3);
        
        return date.isAfter(today) && date.isBefore(maxReservationDate);
    }
    
    private static boolean isWithinValidRange(LocalDate date) {
        LocalDate minDate = LocalDate.of(2000, 1, 1);
        LocalDate maxDate = LocalDate.of(2050, 12, 31);
        
        return !date.isBefore(minDate) && !date.isAfter(maxDate);
    }
    
    private static ValidationResult validateDateInput(String input, 
                                                    DateTimeFormatter formatter) {
        try {
            LocalDate date = LocalDate.parse(input, formatter);
            
            // 追加の検証
            if (date.isAfter(LocalDate.now().plusYears(100))) {
                return ValidationResult.error("100年以上先の日付は入力できません");
            }
            
            if (date.isBefore(LocalDate.of(1900, 1, 1))) {
                return ValidationResult.error("1900年より前の日付は入力できません");
            }
            
            return ValidationResult.success(date);
        } catch (DateTimeParseException e) {
            return ValidationResult.error("日付の形式が正しくありません");
        }
    }
    
    private static int calculateAge(LocalDate birthDate) {
        if (birthDate.isAfter(LocalDate.now())) {
            return 0;  // 未来の日付の場合
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    // 検証結果を表すヘルパークラス
    static class ValidationResult {
        private final boolean valid;
        private final LocalDate date;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, LocalDate date, String errorMessage) {
            this.valid = valid;
            this.date = date;
            this.errorMessage = errorMessage;
        }
        
        static ValidationResult success(LocalDate date) {
            return new ValidationResult(true, date, null);
        }
        
        static ValidationResult error(String message) {
            return new ValidationResult(false, null, message);
        }
        
        boolean isValid() { return valid; }
        LocalDate getDate() { return date; }
        String getErrorMessage() { return errorMessage; }
    }
}