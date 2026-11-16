// RepositoryDemo.java - サービスにメモリ上のリポジトリを渡して使う
public class RepositoryDemo {
    public static void main(String[] args) throws DuplicateStudentException {
        StudentRepository repository = new InMemoryStudentRepository();
        StudentService service = new StudentService(repository);

        service.register(new Student("S001", "山田太郎", "工学部", new Undergraduate(2025)));
        service.register(new Student("S002", "佐藤花子", "理学部", new Undergraduate(2024)));
        service.register(new Student("S003", "鈴木一郎", "工学部", new Undergraduate(2025)));
        service.recordGrade("S001", new Grade("線形代数", 4, 3.0));
        service.recordGrade("S003", new Grade("線形代数", 4, 4.0));

        IO.println("工学部: " + service.findByDepartment("工学部").stream()
            .map(Student::getName).toList());
        IO.println("GPAの上位1人: " + service.topStudents(1).getFirst().getName());

        try {
            service.register(new Student("S002", "田中次郎", "文学部", new Undergraduate(2024)));
        } catch (DuplicateStudentException e) {
            IO.println("登録できません: " + e.getMessage());
        }
    }
}
