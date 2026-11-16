// H2RepositoryCheck.java - H2に保存する実装を、StudentServiceと組み合わせて確かめる
public class H2RepositoryCheck {
    public static void main(String[] args) throws DuplicateStudentException {
        // メモリ上のH2データベース。ファイルに保存するなら jdbc:h2:./students
        try (H2StudentRepository repository = new H2StudentRepository("jdbc:h2:mem:students")) {
            StudentService service = new StudentService(repository);
            service.register(new Student("S002", "佐藤花子", "理学部", new Undergraduate(2024)));
            service.register(new Student("G001", "鈴木一郎", "工学研究科", new Graduate("機械学習", "高橋教授")));
            service.register(new Student("S001", "山田太郎", "工学部", new Undergraduate(2025)));
            service.recordGrade("S001", new Grade("プログラミング演習", 2, 4.0));
            service.recordGrade("S001", new Grade("線形代数", 4, 3.0));
            service.recordGrade("S001", new Grade("線形代数", 4, 3.5)); // 訂正（同じ科目を置き換える）
            service.recordGrade("G001", new Grade("情報理論特論", 2, 3.5));

            for (Student s : repository.findAll()) {
                IO.println("%s %s %s 科目数=%d GPA=%.2f".formatted(s.getId(), s.getName(),
                    s.describeEnrollment(), s.getGrades().size(), s.gpa()));
            }
            check(repository.findAll().getFirst().getId().equals("S002"), "登録した順に返す");
            check(repository.findById("S001").orElseThrow().getGrades().size() == 2, "訂正しても科目数は2");
            check(repository.findById("G001").orElseThrow().getEnrollment() instanceof Graduate g
                && g.advisor().equals("高橋教授"), "大学院生の指導教員を読み込める");

            try {
                service.register(new Student("S001", "田中次郎", "文学部", new Undergraduate(2024)));
                throw new AssertionError("重複したIDを登録できてしまった");
            } catch (DuplicateStudentException e) {
                IO.println("重複を検出: " + e.getMessage());
            }
            check(repository.deleteById("S001"), "削除できる");
            check(repository.findById("S001").isEmpty(), "削除した学生は見つからない");
            IO.println("すべての確認に成功しました");
        }
    }

    private static void check(boolean ok, String label) {
        if (!ok) {
            throw new AssertionError("失敗: " + label);
        }
    }
}
