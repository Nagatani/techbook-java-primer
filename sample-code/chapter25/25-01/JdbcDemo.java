// JdbcDemo.java - サービスはそのままで、保存先だけをSQLiteに替える
public class JdbcDemo {
    public static void main(String[] args) throws DuplicateStudentException {
        // メモリ上のデータベース（終了すると消える）。ファイルに保存するなら jdbc:sqlite:students.db
        try (JdbcStudentRepository repository = new JdbcStudentRepository("jdbc:sqlite::memory:")) {
            StudentService service = new StudentService(repository);

            service.register(new Student("S001", "山田太郎", "工学部", new Undergraduate(2025)));
            service.register(new Student("G001", "佐藤花子", "工学研究科",
                new Graduate("機械学習", "鈴木教授")));
            service.recordGrade("S001", new Grade("プログラミング演習", 2, 4.0));
            service.recordGrade("S001", new Grade("線形代数", 4, 3.0));
            service.recordGrade("G001", new Grade("情報理論特論", 2, 3.5));

            // データベースから読み直した学生を表示する
            for (Student s : repository.findAll()) {
                IO.println("%s %s %s 科目数=%d GPA=%.2f".formatted(s.getId(), s.getName(),
                    s.describeEnrollment(), s.getGrades().size(), s.gpa()));
            }
            IO.println("S001を削除: " + repository.deleteById("S001"));
            IO.println("残り: " + repository.findAll().size() + "人");
        }
    }
}
