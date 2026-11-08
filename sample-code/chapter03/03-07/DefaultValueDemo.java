public class DefaultValueDemo {
    // 数値型のデフォルト値
    int intValue;         // 0
    double doubleValue;   // 0.0
    
    // その他の型のデフォルト値
    boolean boolValue;    // false
    char charValue;       // '\u0000' (空文字)
    String stringValue;   // null
    
    public void showDefaults() {
        System.out.println("int: " + intValue);        // 0
        System.out.println("double: " + doubleValue);  // 0.0
        System.out.println("boolean: " + boolValue);   // false
        System.out.println("String: " + stringValue);  // null
    }
    
    public static void main(String[] args) {
        DefaultValueDemo demo = new DefaultValueDemo();
        demo.showDefaults();
    }
}
