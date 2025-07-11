/**
 * リスト9-36
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (1742行目)
 */

// 将来のValue Records（概念的な例）
public value record ComplexNumber(double real, double imaginary) {
    // Value Typeとして最適化される
    // - ヒープではなくスタックに配置可能
    // - 配列がフラットに格納される
    // - オブジェクトヘッダーのオーバーヘッド除去
    
    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(
            real + other.real,
            imaginary + other.imaginary
        );
    }
    
    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(
            real * other.real - imaginary * other.imaginary,
            real * other.imaginary + imaginary * other.real
        );
    }
    
    public double magnitude() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }
}

// Inline Classesとの統合（将来の機能）
public inline record Vec3(float x, float y, float z) {
    // 完全にスタック割り当て
    // 配列がcontiguousに配置される
    // C/C++並みのパフォーマンス
    
    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }
    
    public float dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
}