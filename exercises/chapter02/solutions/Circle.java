/**
 * 第2章 課題3: Circleクラスの実装 - 解答例
 * 
 * 円を表すCircleクラスの実装
 * 
 * 学習ポイント:
 * - double型を使った精密な計算
 * - Math.PIの使用
 * - 数学的な計算処理の実装
 */
public class Circle {
    // インスタンス変数
    double radius;
    
    // 解答例 1: 基本的な実装
    
    /**
     * 面積を計算するメソッド
     * @return 円の面積
     */
    double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    /**
     * 円周を計算するメソッド
     * @return 円の円周
     */
    double calculateCircumference() {
        return 2 * Math.PI * radius;
    }
    
    /**
     * 円の情報を表示するメソッド
     */
    void displayInfo() {
        System.out.println("円の情報:");
        System.out.println("半径: " + radius + "cm");
        System.out.println("面積: " + String.format("%.2f", calculateArea()) + "cm²");
        System.out.println("円周: " + String.format("%.2f", calculateCircumference()) + "cm");
    }
    
    // 解答例 2: より詳細な実装
    
    /**
     * 詳細な円の情報を表示するメソッド
     */
    void displayDetailedInfo() {
        System.out.println("=== 円の詳細情報 ===");
        System.out.println("半径: " + radius + "cm");
        
        double area = calculateArea();
        double circumference = calculateCircumference();
        double diameter = calculateDiameter();
        
        System.out.println("直径: " + String.format("%.2f", diameter) + "cm");
        System.out.println("面積: " + String.format("%.2f", area) + "cm²");
        System.out.println("円周: " + String.format("%.2f", circumference) + "cm");
        
        // 追加情報
        System.out.println("面積/円周比: " + String.format("%.2f", area / circumference));
        
        // 円のカテゴリ
        String category = getCircleCategory();
        System.out.println("サイズ分類: " + category);
    }
    
    /**
     * 直径を計算するメソッド
     * @return 円の直径
     */
    double calculateDiameter() {
        return 2 * radius;
    }
    
    /**
     * 円のサイズカテゴリを判定するメソッド
     * @return サイズカテゴリの文字列
     */
    String getCircleCategory() {
        if (radius < 5.0) {
            return "小さな円";
        } else if (radius < 15.0) {
            return "中くらいの円";
        } else {
            return "大きな円";
        }
    }
    
    /**
     * 別の円と面積を比較するメソッド
     * @param other 比較対象の円
     * @return 面積差（この円の面積 - 相手の面積）
     */
    double compareAreaWith(Circle other) {
        if (other != null) {
            return this.calculateArea() - other.calculateArea();
        }
        return 0.0;
    }
    
    /**
     * より大きな円かどうかを判定するメソッド
     * @param other 比較対象の円
     * @return より大きな面積の場合true
     */
    boolean isLarger(Circle other) {
        return other != null && this.calculateArea() > other.calculateArea();
    }
    
    /**
     * 同じ大きさの円かどうかを判定するメソッド（誤差許容）
     * @param other 比較対象の円
     * @param tolerance 許容誤差
     * @return 同じ大きさの場合true
     */
    boolean isSameSize(Circle other, double tolerance) {
        if (other != null) {
            double difference = Math.abs(this.radius - other.radius);
            return difference <= tolerance;
        }
        return false;
    }
    
    /**
     * 半径を設定するメソッド（妥当性チェック付き）
     * @param newRadius 新しい半径
     */
    void setRadius(double newRadius) {
        if (newRadius > 0) {
            this.radius = newRadius;
            System.out.println("半径を " + newRadius + "cm に設定しました。");
        } else {
            System.out.println("半径は正の値である必要があります。");
        }
    }
    
    /**
     * 半径を拡大・縮小するメソッド
     * @param factor 倍率（1.0より大きいと拡大、小さいと縮小）
     */
    void scale(double factor) {
        if (factor > 0) {
            double oldRadius = this.radius;
            this.radius *= factor;
            System.out.println("半径を " + String.format("%.2f", oldRadius) + 
                             "cm から " + String.format("%.2f", this.radius) + 
                             "cm に変更しました（" + factor + "倍）");
        } else {
            System.out.println("倍率は正の値である必要があります。");
        }
    }
    
    /**
     * 指定した面積になるように半径を調整するメソッド
     * @param targetArea 目標面積
     */
    void adjustToArea(double targetArea) {
        if (targetArea > 0) {
            double oldRadius = this.radius;
            this.radius = Math.sqrt(targetArea / Math.PI);
            System.out.println("面積 " + String.format("%.2f", targetArea) + 
                             "cm² になるように半径を " + String.format("%.2f", oldRadius) + 
                             "cm から " + String.format("%.2f", this.radius) + 
                             "cm に調整しました。");
        } else {
            System.out.println("面積は正の値である必要があります。");
        }
    }
    
    /**
     * 円に内接する正方形の一辺の長さを計算
     * @return 内接正方形の一辺の長さ
     */
    double calculateInscribedSquareSide() {
        return radius * Math.sqrt(2);
    }
    
    /**
     * 円に外接する正方形の一辺の長さを計算
     * @return 外接正方形の一辺の長さ
     */
    double calculateCircumscribedSquareSide() {
        return 2 * radius;
    }
    
    /**
     * 円の簡潔な文字列表現を返すメソッド
     * @return 円の文字列表現
     */
    String toString() {
        return String.format("Circle(radius=%.2f, area=%.2f, circumference=%.2f)", 
                           radius, calculateArea(), calculateCircumference());
    }
}

/*
 * 数学的計算クラスの設計ポイント:
 * 
 * 1. 精密な計算
 *    - double型の使用で小数点以下の精度を確保
 *    - Math.PIで円周率の正確な値を使用
 *    - String.format()で適切な桁数で表示
 * 
 * 2. 数学公式の実装
 *    - 面積: π × r²
 *    - 円周: 2 × π × r
 *    - 直径: 2 × r
 * 
 * 3. 妥当性チェック
 *    - 半径は正の値のみ受け入れ
 *    - 倍率も正の値のみ受け入れ
 *    - null参照のチェック
 * 
 * 4. 幾何学的関係
 *    - 内接・外接正方形の計算
 *    - 面積比、円周比の計算
 *    - スケーリング処理
 * 
 * よくある計算上の注意点:
 * 
 * 1. 浮動小数点の精度
 *    - double型でも完全に正確ではない
 *    - 比較時は許容誤差を考慮
 *    - 表示時は適切な桁数に丸める
 * 
 * 2. Math.PI の活用
 *    × double pi = 3.14;        // 精度が低い
 *    ○ double pi = Math.PI;     // より正確な値
 * 
 * 3. 平方根の計算
 *    - Math.sqrt()を使用
 *    - 負の値を渡さないよう注意
 * 
 * 4. 数値のフォーマット
 *    - String.format("%.2f", value) で小数第2位まで表示
 *    - 用途に応じて適切な精度を選択
 * 
 * 実世界での応用例:
 * - 建築設計での円形構造物の計算
 * - 製造業での円形部品の設計
 * - 地理情報システムでの円形領域の計算
 * - ゲーム開発での当たり判定（円の重なり）
 * 
 * 数学クラス設計のベストプラクティス:
 * - 計算メソッドは純粋関数として設計
 * - 入力値の妥当性を厳密にチェック
 * - 複雑な計算は段階的に分解
 * - 結果の精度と表示形式を明確に定義
 */