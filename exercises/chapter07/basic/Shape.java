package chapter07.basic;

/**
 * 図形を表す抽象クラス
 * 
 * このクラスは全ての図形に共通する属性と操作を定義します。
 * 具体的な図形クラス（Circle, Rectangle等）はこのクラスを継承して実装してください。
 */
public abstract class Shape {
    // 図形の名前
    protected String name;
    
    /**
     * コンストラクタ
     * @param name 図形の名前
     */
    public Shape(String name) {
        this.name = name;
    }
    
    /**
     * 面積を計算する抽象メソッド
     * TODO: 各具体的な図形クラスでこのメソッドを実装してください
     * @return 図形の面積
     */
    public abstract double calculateArea();
    
    /**
     * 周囲の長さを計算する抽象メソッド
     * TODO: 各具体的な図形クラスでこのメソッドを実装してください
     * @return 図形の周囲の長さ
     */
    public abstract double calculatePerimeter();
    
    /**
     * 図形の情報を表示するメソッド
     * TODO: 必要に応じてオーバーライドしてください
     */
    public void displayInfo() {
        System.out.println("図形: " + name);
        System.out.println("面積: " + calculateArea());
        System.out.println("周囲の長さ: " + calculatePerimeter());
    }
    
    /**
     * 図形の名前を取得する
     * @return 図形の名前
     */
    public String getName() {
        return name;
    }
}