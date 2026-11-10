public class DefaultValueDemo {
    // 数値型のデフォルト値
    int intValue;         // 0
    double doubleValue;   // 0.0
    
    // その他の型のデフォルト値
    boolean boolValue;    // false
    char charValue;       // '\u0000' (空文字)
    String stringValue;   // null
    
    public void showDefaults() {
        IO.println("int: " + intValue);        // 0
        IO.println("double: " + doubleValue);  // 0.0
        IO.println("boolean: " + boolValue);   // false
        IO.println("String: " + stringValue);  // null
    }
    
    public static void main(String[] args) {
        DefaultValueDemo demo = new DefaultValueDemo();
        demo.showDefaults();
    }
}
