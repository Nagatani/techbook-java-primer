public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6),
    MARS(6.421e+23, 3.3972e6);
    
    private final double mass;   // 質量（kg）
    private final double radius; // 半径（m）
    
    // コンストラクター（暗黙的にprivate）
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    
    public double getMass() {
        return mass;
    }
    
    public double getRadius() {
        return radius;
    }
    
    // 表面重力を計算
    public double surfaceGravity() {
        double G = 6.67300E-11; // 万有引力定数
        return G * mass / (radius * radius);
    }
}
