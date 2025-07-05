/**
 * 値を持つEnum
 * 太陽系の惑星の質量と半径を管理
 */
public enum Planet {
    /** 水星 */
    MERCURY(3.303e+23, 2.4397e6),
    /** 金星 */
    VENUS(4.869e+24, 6.0518e6),
    /** 地球 */
    EARTH(5.976e+24, 6.37814e6),
    /** 火星 */
    MARS(6.421e+23, 3.3972e6),
    /** 木星 */
    JUPITER(1.9e+27, 7.1492e7),
    /** 土星 */
    SATURN(5.688e+26, 6.0268e7),
    /** 天王星 */
    URANUS(8.686e+25, 2.5559e7),
    /** 海王星 */
    NEPTUNE(1.024e+26, 2.4746e7);
    
    private final double mass;    // 質量（kg）
    private final double radius;  // 半径（m）
    
    /** 万有引力定数 */
    private static final double G = 6.67300E-11;
    
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    
    /**
     * 質量を取得
     * @return 質量（kg）
     */
    public double getMass() {
        return mass;
    }
    
    /**
     * 半径を取得
     * @return 半径（m）
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * 表面重力を計算
     * @return 表面重力（m/s²）
     */
    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }
    
    /**
     * 指定された質量の物体の重量を計算
     * @param otherMass 物体の質量（kg）
     * @return 重量（N）
     */
    public double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
    
    /**
     * 惑星の日本語名を取得
     * @return 日本語名
     */
    public String getJapaneseName() {
        return switch (this) {
            case MERCURY -> "水星";
            case VENUS -> "金星";
            case EARTH -> "地球";
            case MARS -> "火星";
            case JUPITER -> "木星";
            case SATURN -> "土星";
            case URANUS -> "天王星";
            case NEPTUNE -> "海王星";
        };
    }
    
    /**
     * 地球からの距離順位を取得
     * @return 順位（1が最も近い）
     */
    public int getDistanceRank() {
        return switch (this) {
            case VENUS -> 1;
            case MARS -> 2;
            case MERCURY -> 3;
            case JUPITER -> 4;
            case SATURN -> 5;
            case URANUS -> 6;
            case NEPTUNE -> 7;
            case EARTH -> 0; // 地球は自分自身
        };
    }
}