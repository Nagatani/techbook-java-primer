// Phase3Test.java - フェーズ3のテスト
public class Phase3Test {
    public static void main(String[] args) {
        System.out.println("=== フェーズ3: コレクションとジェネリクスのテスト ===\n");
        
        StudentService service = new StudentService();
        
        // 学生の登録
        GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        student1.addGrade("数学", 3.5);
        student1.addGrade("物理", 4.0);
        student1.addGrade("英語", 3.0);
        
        GradeableStudent student2 = new GradeableStudent("S002", "山田花子", 21, "理学部");
        student2.addGrade("数学", 4.0);
        student2.addGrade("化学", 3.8);
        student2.addGrade("生物", 3.5);
        
        GradeableStudent student3 = new GradeableStudent("S003", "佐藤次郎", 22, "工学部");
        student3.addGrade("プログラミング", 3.8);
        student3.addGrade("データベース", 3.2);
        
        GradeableStudent student4 = new GradeableStudent("S004", "鈴木美咲", 19, "文学部");
        student4.addGrade("英文学", 3.9);
        student4.addGrade("日本文学", 3.7);
        
        service.registerStudent(student1);
        service.registerStudent(student2);
        service.registerStudent(student3);
        service.registerStudent(student4);
        
        // 個別の成績レポート
        System.out.println(service.generateGradeReport("S001"));
        System.out.println();
        
        // 統計情報
        System.out.println(service.getStatisticsReport());
        
        // 条件検索のテスト
        System.out.println("\n=== 条件検索のテスト ===");
        
        System.out.println("\n工学部の学生:");
        service.findStudentsWithConditions("工学部", null, null)
            .forEach(s -> System.out.println("  " + s.getName() + " (GPA: " + 
                String.format("%.2f", s.getGpa()) + ")"));
        
        System.out.println("\nGPA 3.5以上の学生:");
        service.findStudentsWithConditions(null, 3.5, null)
            .forEach(s -> System.out.println("  " + s.getName() + " (" + 
                s.getDepartment() + ", GPA: " + String.format("%.2f", s.getGpa()) + ")"));
        
        System.out.println("\n20歳以上の工学部学生:");
        service.findStudentsWithConditions("工学部", null, 20)
            .forEach(s -> System.out.println("  " + s.getName() + " (" + 
                s.getAge() + "歳)"));
    }
}