public class ObjectMethodsDemo {
    public static void main(String[] args) {
        Student s1 = new Student("S001", "田中太郎");
        Student s2 = new Student("S001", "田中太郎");
        Student s3 = new Student("S002", "佐藤花子");
        
        IO.println(s1);                 // toString()が呼ばれる
        IO.println("s1 == s2: " + (s1 == s2));
        IO.println("s1.equals(s2): " + s1.equals(s2));
        IO.println("s1.equals(s3): " + s1.equals(s3));
        IO.println("ハッシュ値が同じ: " + (s1.hashCode() == s2.hashCode()));
    }
}
