// switch文の基本的な使い方
public class SwitchExample {
    public static void main(String[] args) {
        int grade = 85;
        String evaluation;
        
        switch (grade / 10) {
            case 10:
            case 9:
                evaluation = "優秀";
                break;
            case 8:
                evaluation = "良い";
                break;
            case 7:
                evaluation = "普通";
                break;
            default:
                evaluation = "要努力";
                break;
        }
        
        IO.println("評価: " + evaluation);
    }
}
