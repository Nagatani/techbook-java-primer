public record Temperature(double value, String unit) {
    // 静的ファクトリメソッド
    public static Temperature ofCelsius(double celsius) {
        return new Temperature(celsius, "C");
    }
    
    public static Temperature ofFahrenheit(double fahrenheit) {
        return new Temperature(fahrenheit, "F");
    }
    
    public static Temperature ofKelvin(double kelvin) {
        return new Temperature(kelvin, "K");
    }
    
    // 摂氏に変換
    public double toCelsius() {
        return switch (unit) {
            case "C" -> value;
            case "F" -> (value - 32) * 5 / 9;
            case "K" -> value - 273.15;
            default -> throw new IllegalStateException("未知の単位: " + unit);
        };
    }
}
