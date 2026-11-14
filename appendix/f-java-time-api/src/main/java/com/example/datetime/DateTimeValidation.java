package com.example.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DateTimeValidation {
    public static void main(String[] args) {
        IO.println("=== 日付時刻の妥当性検証デモ ===\n");
        
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
        IO.println("1. 基本的な日付妥当性チェック");
        IO.println("-".repeat(40));
        
        String[] testDates = {
            "2024-02-29",  // うるう年の2月29日
            "2023-02-29",  // 平年の2月29日（無効）
            "2024-13-01",  // 無効な月
            "2024-12-32",  // 無効な日
            "2024-00-15",  // 月が0
            "2024-12-00"   // 日が0
        };
        
        // ISO_LOCAL_DATEなどの定義済みフォーマッターは、STRICTで日付を検証する
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (String dateStr : testDates) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                IO.println(dateStr + " → 有効 (" + date + ")");
            } catch (DateTimeParseException e) {
                IO.println(dateStr + " → 無効 (理由: " + 
                    e.getMessage().split("\n")[0] + ")");
            }
        }
        IO.println();
    }
    
    private static void demonstrateStrictValidation() {
        IO.println("2. 解決方法（ResolverStyle）による違い");
        IO.println("-".repeat(40));
        
        // ofPatternの既定（SMART）: 月末を超えた日は、その月の末日に丸められる
        DateTimeFormatter smartFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        
        // 厳密なフォーマッター（日付の妥当性を厳格にチェック）
        DateTimeFormatter strictFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
        
        // 寛容なフォーマッター（ある程度の補正を許可）
        DateTimeFormatter lenientFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withResolverStyle(ResolverStyle.LENIENT);
        
        String[] testCases = {
            "2024-02-30",  // 2月30日
            "2024-04-31",  // 4月31日
            "2024-12-32"   // 12月32日
        };
        
        for (String dateStr : testCases) {
            IO.println("\n入力: " + dateStr);
            
            // 既定（SMART）の検証
            try {
                LocalDate smartDate = LocalDate.parse(dateStr, smartFormatter);
                IO.println("  SMART（既定）: エラーにならず丸められる (" + smartDate + ")");
            } catch (DateTimeParseException e) {
                IO.println("  SMART（既定）: 無効");
            }
            
            // 厳密な検証
            try {
                LocalDate strictDate = LocalDate.parse(dateStr, strictFormatter);
                IO.println("  STRICT: 有効 (" + strictDate + ")");
            } catch (DateTimeParseException e) {
                IO.println("  STRICT: 無効");
            }
            
            // 寛容な検証
            try {
                LocalDate lenientDate = LocalDate.parse(dateStr, lenientFormatter);
                IO.println("  LENIENT: 補正されて有効 (" + lenientDate + ")");
            } catch (DateTimeParseException e) {
                IO.println("  LENIENT: 無効");
            }
        }
        IO.println();
    }
    
    private static void demonstrateCustomValidation() {
        IO.println("3. カスタム検証ロジック");
        IO.println("-".repeat(40));
        
        // 実行した日で結果が変わらないように、今日の代わりの基準日を決める
        LocalDate baseDate = LocalDate.of(2024, 1, 1);
        IO.println("基準日（今日の代わり）: " + baseDate);
        
        // テストケース
        LocalDate[] dates = {
            LocalDate.of(2024, 1, 1),    // 月曜日（元日）
            LocalDate.of(2024, 1, 5),    // 金曜日
            LocalDate.of(2024, 1, 6),    // 土曜日
            LocalDate.of(2024, 3, 15),   // 金曜日（3か月以内）
            LocalDate.of(2024, 6, 3),    // 月曜日（3か月より先）
            LocalDate.of(2100, 1, 1),    // 遠い未来
            LocalDate.of(1900, 1, 1)     // 遠い過去
        };
        
        for (LocalDate date : dates) {
            IO.println("\n日付: " + date + " (" + 
                date.getDayOfWeek() + ")");
            IO.println("  営業日？ " + isBusinessDay(date));
            IO.println("  予約可能？ " + isReservationAvailable(date, baseDate));
            IO.println("  有効範囲内？ " + isWithinValidRange(date));
        }
        IO.println();
    }
    
    private static void demonstrateInteractiveValidation() {
        IO.println("4. 対話的な日付入力検証");
        IO.println("-".repeat(40));
        IO.println("日付を入力してください（形式: yyyy-MM-dd）");
        IO.println("終了するには 'quit' と入力してください。\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE
            .withResolverStyle(ResolverStyle.STRICT);
        
        while (true) {
            String line = IO.readln("日付を入力: ");
            if (line == null) {
                break;  // 入力の終わり（Ctrl+DやCtrl+Z）でも終了する
            }
            String input = line.trim();
            
            if ("quit".equalsIgnoreCase(input)) {
                break;
            }
            
            ValidationResult result = validateDateInput(input, formatter);
            
            if (result.isValid()) {
                LocalDate date = result.getDate();
                IO.println("✓ 有効な日付です: " + date);
                IO.println("  曜日: " + date.getDayOfWeek());
                IO.println("  今日から: " + 
                    Period.between(LocalDate.now(), date));
                IO.println("  年齢計算: " + 
                    calculateAge(date) + "歳");
            } else {
                IO.println("✗ 無効な日付です: " + result.getErrorMessage());
            }
            IO.println();
        }
    }
    
    // カスタム検証メソッド
    private static boolean isBusinessDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && 
               dayOfWeek != DayOfWeek.SUNDAY &&
               !isJapaneseHoliday(date);
    }
    
    private static boolean isJapaneseHoliday(LocalDate date) {
        // 簡易的な祝日判定（例として元日だけを祝日にする）
        // 実際には祝日の一覧を年ごとに持つ必要がある（BusinessDateCalculator.javaを参照）
        return date.getMonthValue() == 1 && date.getDayOfMonth() == 1;  // 元日
    }
    
    // 予約できるのは、基準日（ふつうは今日）の翌日から3か月以内
    private static boolean isReservationAvailable(LocalDate date, LocalDate today) {
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