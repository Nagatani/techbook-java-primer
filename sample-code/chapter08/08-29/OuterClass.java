public class OuterClass {
    private String outerField = "外部フィールド";
    
    // メンバー内部クラス
    public class InnerClass {
        public void printOuterField() {
            // 外部クラスのprivateメンバーにアクセス可能
            System.out.println("外部クラスのフィールド: " + outerField);
        }
    }
    
    public static void main(String[] args) {
        OuterClass outer = new OuterClass();
        // 内部クラスのインスタンス化には外部クラスのインスタンスが必要
        OuterClass.InnerClass inner = outer.new InnerClass();
        inner.printOuterField();
    }
}
