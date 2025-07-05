/**
 * 第1章 応用課題2: 個人情報管理システム
 * 
 * 個人の詳細情報を管理し、様々な計算を行うプログラム。
 * 基本情報から健康指標や統計情報を自動計算します。
 * 
 * 学習ポイント:
 * - 複数のデータ型の組み合わせ使用
 * - 条件による文字列の変更
 * - 数値計算と文字列操作の組み合わせ
 * - String.length()メソッドの使用
 * - 実用的な計算式の実装
 */

public class PersonalInfo {
    public static void main(String[] args) {
        // 個人の基本情報を定義
        String firstName = "太郎";
        String lastName = "田中";
        String fullName = lastName + firstName;
        int age = 22;
        double height = 170.5; // cm
        double weight = 65.0;  // kg
        String bloodType = "A";
        String gender = "男性";
        
        // プログラムのタイトル表示
        System.out.println("=== 個人情報管理システム ===");
        System.out.println();
        
        // 基本情報の表示
        displayBasicInfo(fullName, firstName, lastName, age, height, weight, bloodType, gender);
        
        // 健康指標の計算と表示
        displayHealthIndicators(height, weight, age, gender);
        
        // 文字列解析の表示
        displayStringAnalysis(fullName, firstName, lastName);
        
        // 年齢関連計算の表示
        displayAgeCalculations(age);
        
        // 身体的特徴の分析
        displayPhysicalAnalysis(height, weight, gender);
    }
    
    /**
     * 基本情報を表示します
     */
    private static void displayBasicInfo(String fullName, String firstName, String lastName, 
                                       int age, double height, double weight, 
                                       String bloodType, String gender) {
        System.out.println("基本情報:");
        System.out.println("氏名: " + fullName + " (" + fullName.length() + "文字)");
        System.out.println("姓: " + lastName + " (" + lastName.length() + "文字)");
        System.out.println("名: " + firstName + " (" + firstName.length() + "文字)");
        
        // イニシャルの生成
        String initials = getInitials(firstName, lastName);
        System.out.println("イニシャル: " + initials);
        
        System.out.println("年齢: " + age + "歳");
        System.out.println("身長: " + height + "cm");
        System.out.println("体重: " + weight + "kg");
        System.out.println("血液型: " + bloodType + "型");
        System.out.println("性別: " + gender);
        System.out.println();
    }
    
    /**
     * 健康指標を計算して表示します
     */
    private static void displayHealthIndicators(double height, double weight, int age, String gender) {
        System.out.println("健康指標:");
        
        // BMI計算
        double heightInMeters = height / 100.0;
        double bmi = weight / (heightInMeters * heightInMeters);
        String bmiCategory = getBMICategory(bmi);
        System.out.println("BMI: " + String.format("%.1f", bmi) + " (" + bmiCategory + ")");
        
        // 理想体重計算（複数の方式）
        double idealWeight1 = (height - 100) * 0.9; // 簡易式
        double idealWeight2 = heightInMeters * heightInMeters * 22; // BMI 22基準
        System.out.println("理想体重 (簡易式): " + String.format("%.1f", idealWeight1) + "kg");
        System.out.println("理想体重 (BMI22): " + String.format("%.1f", idealWeight2) + "kg");
        
        // 体重差
        double weightDiff1 = weight - idealWeight1;
        double weightDiff2 = weight - idealWeight2;
        System.out.println("理想体重との差 (簡易式): " + 
                          (weightDiff1 >= 0 ? "+" : "") + String.format("%.1f", weightDiff1) + "kg");
        System.out.println("理想体重との差 (BMI22): " + 
                          (weightDiff2 >= 0 ? "+" : "") + String.format("%.1f", weightDiff2) + "kg");
        
        // 成人判定
        String adultStatus = getAdultStatus(age);
        System.out.println("成人判定: " + adultStatus);
        
        // 基礎代謝計算（Harris-Benedict式）
        double bmr = calculateBMR(weight, height, age, gender);
        System.out.println("基礎代謝: " + String.format("%.0f", bmr) + " kcal/日");
        
        System.out.println();
    }
    
    /**
     * 文字列解析結果を表示します
     */
    private static void displayStringAnalysis(String fullName, String firstName, String lastName) {
        System.out.println("文字列解析:");
        
        // 文字数分析
        int totalChars = fullName.length();
        int firstNameChars = firstName.length();
        int lastNameChars = lastName.length();
        
        System.out.println("総文字数: " + totalChars + "文字");
        System.out.println("姓の文字数: " + lastNameChars + "文字");
        System.out.println("名の文字数: " + firstNameChars + "文字");
        
        // 文字数比率
        double firstNameRatio = (double) firstNameChars / totalChars * 100;
        double lastNameRatio = (double) lastNameChars / totalChars * 100;
        System.out.println("名の比率: " + String.format("%.1f", firstNameRatio) + "%");
        System.out.println("姓の比率: " + String.format("%.1f", lastNameRatio) + "%");
        
        // 名前の特徴
        if (firstNameChars == lastNameChars) {
            System.out.println("特徴: 姓と名の文字数が同じです");
        } else if (firstNameChars > lastNameChars) {
            System.out.println("特徴: 名の方が長いです");
        } else {
            System.out.println("特徴: 姓の方が長いです");
        }
        
        System.out.println();
    }
    
    /**
     * 年齢関連の計算を表示します
     */
    private static void displayAgeCalculations(int age) {
        System.out.println("年齢関連計算:");
        
        // 日数計算（概算）
        int daysLived = age * 365; // うるう年は考慮しない簡易計算
        System.out.println(age + "歳 = 約" + String.format("%,d", daysLived) + "日");
        
        // 時間計算
        long hoursLived = daysLived * 24L;
        System.out.println("生まれてから約" + String.format("%,d", hoursLived) + "時間");
        
        // 分計算
        long minutesLived = hoursLived * 60L;
        System.out.println("生まれてから約" + String.format("%,d", minutesLived) + "分");
        
        // 世代分析
        String generation = getGeneration(age);
        System.out.println("世代: " + generation);
        
        // 人生の段階
        String lifeStage = getLifeStage(age);
        System.out.println("人生段階: " + lifeStage);
        
        // 年齢グループ
        String ageGroup = getAgeGroup(age);
        System.out.println("年齢グループ: " + ageGroup);
        
        System.out.println();
    }
    
    /**
     * 身体的特徴の分析を表示します
     */
    private static void displayPhysicalAnalysis(double height, double weight, String gender) {
        System.out.println("身体的特徴分析:");
        
        // 身長カテゴリ
        String heightCategory = getHeightCategory(height, gender);
        System.out.println("身長カテゴリ: " + heightCategory);
        
        // 体重カテゴリ
        String weightCategory = getWeightCategory(weight);
        System.out.println("体重カテゴリ: " + weightCategory);
        
        // 体格指数（ローレル指数）
        double rohrer = weight / Math.pow(height, 3) * 10000000;
        String rohrerCategory = getRohrerCategory(rohrer);
        System.out.println("ローレル指数: " + String.format("%.2f", rohrer) + " (" + rohrerCategory + ")");
        
        // 平均との比較（仮想データ）
        double avgHeight = gender.equals("男性") ? 171.0 : 158.0;
        double avgWeight = gender.equals("男性") ? 68.0 : 52.0;
        
        double heightDiff = height - avgHeight;
        double weightDiff = weight - avgWeight;
        
        System.out.println("平均身長との差: " + 
                          (heightDiff >= 0 ? "+" : "") + String.format("%.1f", heightDiff) + "cm");
        System.out.println("平均体重との差: " + 
                          (weightDiff >= 0 ? "+" : "") + String.format("%.1f", weightDiff) + "kg");
        
        System.out.println();
    }
    
    // ===== ユーティリティメソッド =====
    
    /**
     * イニシャルを生成します
     */
    private static String getInitials(String firstName, String lastName) {
        String firstInitial = lastName.length() > 0 ? lastName.substring(0, 1) : "";
        String secondInitial = firstName.length() > 0 ? firstName.substring(0, 1) : "";
        return firstInitial + "." + secondInitial + ".";
    }
    
    /**
     * BMIカテゴリを取得します
     */
    private static String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "低体重";
        } else if (bmi < 25.0) {
            return "標準体重";
        } else if (bmi < 30.0) {
            return "肥満（1度）";
        } else if (bmi < 35.0) {
            return "肥満（2度）";
        } else {
            return "肥満（3度）";
        }
    }
    
    /**
     * 成人判定を取得します
     */
    private static String getAdultStatus(int age) {
        if (age >= 20) {
            return "成人";
        } else if (age >= 18) {
            return "18歳以上（選挙権あり）";
        } else {
            return "未成年";
        }
    }
    
    /**
     * 基礎代謝を計算します（Harris-Benedict式）
     */
    private static double calculateBMR(double weight, double height, int age, String gender) {
        if (gender.equals("男性")) {
            return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
    }
    
    /**
     * 世代を取得します
     */
    private static String getGeneration(int age) {
        if (age <= 12) {
            return "ジェネレーションα";
        } else if (age <= 27) {
            return "ジェネレーションZ";
        } else if (age <= 42) {
            return "ミレニアル世代";
        } else if (age <= 57) {
            return "ジェネレーションX";
        } else {
            return "ベビーブーマー";
        }
    }
    
    /**
     * 人生段階を取得します
     */
    private static String getLifeStage(int age) {
        if (age <= 6) {
            return "乳幼児期";
        } else if (age <= 12) {
            return "学童期";
        } else if (age <= 18) {
            return "青年期前期";
        } else if (age <= 30) {
            return "青年期後期";
        } else if (age <= 65) {
            return "成人期";
        } else {
            return "高齢期";
        }
    }
    
    /**
     * 年齢グループを取得します
     */
    private static String getAgeGroup(int age) {
        return (age / 10) * 10 + "代";
    }
    
    /**
     * 身長カテゴリを取得します
     */
    private static String getHeightCategory(double height, String gender) {
        if (gender.equals("男性")) {
            if (height < 160) return "低身長";
            else if (height < 170) return "やや低身長";
            else if (height < 180) return "標準身長";
            else return "高身長";
        } else {
            if (height < 150) return "低身長";
            else if (height < 160) return "やや低身長";
            else if (height < 170) return "標準身長";
            else return "高身長";
        }
    }
    
    /**
     * 体重カテゴリを取得します
     */
    private static String getWeightCategory(double weight) {
        if (weight < 50) {
            return "軽体重";
        } else if (weight < 70) {
            return "標準体重";
        } else if (weight < 90) {
            return "重体重";
        } else {
            return "非常に重体重";
        }
    }
    
    /**
     * ローレル指数カテゴリを取得します
     */
    private static String getRohrerCategory(double rohrer) {
        if (rohrer < 100) {
            return "やせ型";
        } else if (rohrer < 115) {
            return "やせ気味";
        } else if (rohrer < 145) {
            return "標準";
        } else if (rohrer < 160) {
            return "太り気味";
        } else {
            return "太り型";
        }
    }
}

/*
 * 実行結果例:
 * 
 * === 個人情報管理システム ===
 * 
 * 基本情報:
 * 氏名: 田中太郎 (4文字)
 * 姓: 田中 (2文字)
 * 名: 太郎 (2文字)
 * イニシャル: 田.太.
 * 年齢: 22歳
 * 身長: 170.5cm
 * 体重: 65.0kg
 * 血液型: A型
 * 性別: 男性
 * 
 * 健康指標:
 * BMI: 22.4 (標準体重)
 * 理想体重 (簡易式): 63.5kg
 * 理想体重 (BMI22): 63.9kg
 * 理想体重との差 (簡易式): +1.5kg
 * 理想体重との差 (BMI22): +1.1kg
 * 成人判定: 成人
 * 基礎代謝: 1665 kcal/日
 * 
 * 文字列解析:
 * 総文字数: 4文字
 * 姓の文字数: 2文字
 * 名の文字数: 2文字
 * 名の比率: 50.0%
 * 姓の比率: 50.0%
 * 特徴: 姓と名の文字数が同じです
 * 
 * 年齢関連計算:
 * 22歳 = 約8,030日
 * 生まれてから約192,720時間
 * 生まれてから約11,563,200分
 * 世代: ジェネレーションZ
 * 人生段階: 青年期後期
 * 年齢グループ: 20代
 * 
 * 身体的特徴分析:
 * 身長カテゴリ: 標準身長
 * 体重カテゴリ: 標準体重
 * ローレル指数: 130.95 (標準)
 * 平均身長との差: -0.5cm
 * 平均体重との差: -3.0kg
 * 
 * 学習のポイント:
 * 1. データ型の組み合わせ：String, int, double を組み合わせた情報管理
 * 2. 条件分岐：年齢や数値に応じた文字列の切り替え
 * 3. 文字列操作：length(), substring() メソッドの活用
 * 4. 数値計算：実用的な計算式の実装
 * 5. フォーマット：String.format() での表示調整
 */