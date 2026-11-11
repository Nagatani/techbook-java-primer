public class EmployeeTest {
    public static void main(String[] args) {
        // Managerの使用例
        Manager manager = new Manager();
        manager.employeeId = "M001";
        manager.name = "山田部長";
        manager.work();  // 継承したメソッド
        manager.conductMeeting();  // Manager固有のメソッド
        
        IO.println();
        
        // Developerの使用例
        Developer developer = new Developer();
        developer.employeeId = "D001";
        developer.name = "鈴木開発者";
        developer.primaryLanguage = "Java";
        developer.work();  // 継承したメソッド
        developer.writeCode();  // Developer固有のメソッド
    }
}
