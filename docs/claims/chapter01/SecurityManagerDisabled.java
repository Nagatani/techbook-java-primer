// 第1章：セキュリティマネージャーは無効になった（JEP 486、Java 24）
public class SecurityManagerDisabled {
    @SuppressWarnings("removal")
    public static void main(String[] args) {
        try {
            System.setSecurityManager(new SecurityManager());
            System.out.println("set");
        } catch (UnsupportedOperationException e) {
            System.out.println("UnsupportedOperationException: " + e.getMessage());
        }
    }
}
