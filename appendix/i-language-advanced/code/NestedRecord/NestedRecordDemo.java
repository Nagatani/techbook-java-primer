public class NestedRecordDemo {
    public static void main(String[] args) {
        Address address = new Address("丸の内1-1", "東京", "日本");
        Company company = new Company("サンプル商事", address);
        Employee employee = new Employee("山田太郎", "E001", company);

        IO.println(employee);
        IO.println(employee.getFullAddress());
        // アクセサーをつないで、内側のRecordの値を取り出す
        IO.println("所在地の都市: "
                + employee.company().headquarters().city());
    }
}
