/**
 * 第7章 課題1: 図形クラスの継承と面積計算 - 解答例
 * 
 * 抽象クラスShapeの実装
 * 
 * 学習ポイント:
 * - 抽象クラスの定義と使用方法
 * - 抽象メソッドの実装強制
 * - 共通処理と個別処理の分離
 */
public abstract class Shape {
    // 共通フィールド
    protected String color;
    protected double x, y;  // 位置座標
    
    // 解答例 1: 基本的な抽象クラス実装
    
    /**
     * コンストラクタ
     * @param color 色
     */
    public Shape(String color) {
        this.color = color;
        this.x = 0.0;
        this.y = 0.0;
    }
    
    /**
     * 位置指定コンストラクタ
     * @param color 色
     * @param x X座標
     * @param y Y座標
     */
    public Shape(String color, double x, double y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }
    
    /**
     * 面積を計算する抽象メソッド
     * サブクラスで必ず実装する必要がある
     * @return 面積
     */
    public abstract double calculateArea();
    
    /**
     * 周囲の長さを計算する抽象メソッド
     * @return 周囲の長さ
     */
    public abstract double calculatePerimeter();
    
    // 解答例 2: 共通メソッドの実装
    
    /**
     * 図形の基本情報を表示（共通処理）
     */
    public void displayBasicInfo() {
        System.out.println("図形の種類: " + getShapeType());
        System.out.println("色: " + color);
        System.out.println("位置: (" + x + ", " + y + ")");
        System.out.println("面積: " + String.format("%.2f", calculateArea()));
        System.out.println("周囲: " + String.format("%.2f", calculatePerimeter()));
    }
    
    /**
     * 図形の種類を返す（サブクラスでオーバーライド推奨）
     * @return 図形の種類
     */
    public String getShapeType() {
        return "図形";
    }
    
    /**
     * 図形を移動させる
     * @param deltaX X方向の移動量
     * @param deltaY Y方向の移動量
     */
    public void move(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;
        System.out.println(getShapeType() + "を移動しました: (" + x + ", " + y + ")");
    }
    
    /**
     * 色を変更する
     * @param newColor 新しい色
     */
    public void changeColor(String newColor) {
        String oldColor = this.color;
        this.color = newColor;
        System.out.println(getShapeType() + "の色を" + oldColor + "から" + newColor + "に変更しました");
    }
    
    // 解答例 3: 比較・判定メソッド
    
    /**
     * 他の図形と面積を比較
     * @param other 比較対象の図形
     * @return 面積差（この図形 - 他の図形）
     */
    public double compareAreaWith(Shape other) {
        if (other != null) {
            return this.calculateArea() - other.calculateArea();
        }
        return 0.0;
    }
    
    /**
     * より大きな面積を持つかを判定
     * @param other 比較対象の図形
     * @return より大きな面積の場合true
     */
    public boolean isLargerThan(Shape other) {
        return other != null && this.calculateArea() > other.calculateArea();
    }
    
    /**
     * 同じ色かどうかを判定
     * @param other 比較対象の図形
     * @return 同じ色の場合true
     */
    public boolean isSameColor(Shape other) {
        if (other != null && this.color != null && other.color != null) {
            return this.color.equals(other.color);
        }
        return false;
    }
    
    /**
     * 指定した点が図形内に含まれるかを判定（抽象メソッド）
     * @param pointX 点のX座標
     * @param pointY 点のY座標
     * @return 含まれる場合true
     */
    public abstract boolean contains(double pointX, double pointY);
    
    // 解答例 4: ユーティリティメソッド
    
    /**
     * 図形の詳細情報を文字列で返す
     * @return 詳細情報の文字列
     */
    @Override
    public String toString() {
        return String.format("%s(色=%s, 位置=(%.1f,%.1f), 面積=%.2f)", 
                           getShapeType(), color, x, y, calculateArea());
    }
    
    /**
     * 面積密度を計算（面積/周囲の比率）
     * @return 面積密度
     */
    public double calculateAreaDensity() {
        double perimeter = calculatePerimeter();
        return perimeter > 0 ? calculateArea() / perimeter : 0.0;
    }
    
    /**
     * 図形をスケール（拡大・縮小）する抽象メソッド
     * @param factor 倍率
     */
    public abstract void scale(double factor);
    
    /**
     * 図形を複製する抽象メソッド
     * @return 複製された図形
     */
    public abstract Shape clone();
    
    // Getterメソッド
    public String getColor() {
        return color;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    /**
     * 位置を設定する
     * @param x X座標
     * @param y Y座標
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

/*
 * 抽象クラス設計のポイント:
 * 
 * 1. 抽象メソッドの定義
 *    - 必ずサブクラスで実装されるべきメソッド
 *    - calculateArea(), calculatePerimeter()など
 *    - 図形ごとに計算方法が異なる処理
 * 
 * 2. 共通処理の実装
 *    - すべてのサブクラスで共通する処理
 *    - displayBasicInfo(), move(), changeColor()など
 *    - コードの重複を避ける
 * 
 * 3. protectedフィールド
 *    - サブクラスからアクセス可能
 *    - 外部からは直接アクセス不可
 *    - カプセル化を保ちつつ継承を活用
 * 
 * 4. テンプレートメソッドパターン
 *    - displayBasicInfo()内でcalculateArea()を呼び出し
 *    - 共通の処理フローを定義
 *    - 具体的な実装はサブクラスに委譲
 * 
 * よくある設計上の注意点:
 * 
 * 1. 抽象クラス vs インターフェイス
 *    - 抽象クラス: 共通の実装とデータを持つ
 *    - インターフェイス: 契約（メソッドのシグネチャ）のみ
 * 
 * 2. 継承階層の設計
 *    - 「is-a」関係を表現
 *    - Circle is a Shape, Rectangle is a Shape
 *    - 過度に深い継承は避ける
 * 
 * 3. 抽象メソッドの粒度
 *    - 適切な抽象レベルを保つ
 *    - あまりに細かすぎると使いにくい
 *    - あまりに粗すぎると柔軟性がない
 * 
 * 実世界での応用:
 * - GUI部品の基底クラス
 * - ゲームキャラクターの基底クラス
 * - データベースDAO（Data Access Object）の基底クラス
 * - 計算エンジンの基底クラス
 */