# 付録D: 統合プロジェクト - 学生管理システム<small>実践的アプリケーション開発</small>

本付録では、これまでに学んだ複数の技術を統合した実践的なプロジェクトとして「学生管理システム」を段階的に構築していきます。各段階で新しい技術を追加し、システムを発展させていく過程を通じて、実践的なアプリケーション開発の流れを学びます。

## プロジェクト概要

学生管理システムは、教育機関で学生情報を管理するためのアプリケーションです。基本的なデータ管理から始めて、最終的にはGUIを持つ完全なデータベースアプリケーションへと発展させます。

### 完成時の機能
- 学生情報の登録・更新・削除
- 成績管理
- 検索・フィルタリング機能
- データの永続化（データベース）
- グラフィカルユーザーインターフェイス
- データのエクスポート・インポート

## フェーズ1: 基本的なクラス設計（第3-4章）

まず、基本的なStudentクラスを設計します。

```java
// Student.java
public class Student {
    private String studentId;
    private String name;
    private int age;
    private String department;
    private double gpa;
    
    // コンストラクタ
    public Student(String studentId, String name, int age, String department) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.department = department;
        this.gpa = 0.0;
    }
    
    // ゲッター
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
    
    // セッター
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { 
        if (age > 0) this.age = age; 
    }
    public void setDepartment(String department) { this.department = department; }
    public void setGpa(double gpa) { 
        if (gpa >= 0 && gpa <= 4.0) this.gpa = gpa; 
    }
    
    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Age=%d, Dept=%s, GPA=%.2f]",
            studentId, name, age, department, gpa);
    }
}
```

### フェーズ1実装ガイド

**実装手順**:
1. Student.javaファイルを作成
2. 基本的なフィールドとコンストラクタを実装
3. 適切なgetter/setterメソッドを追加
4. toString()メソッドをオーバーライド

**検証手順**:
```java
// StudentTest.java - 簡単な動作確認
public class StudentTest {
    public static void main(String[] args) {
        Student student = new Student("S001", "田中太郎", 20, "情報工学科");
        
        System.out.println("学生情報: " + student);
        
        student.setGpa(3.7);
        System.out.println("GPA更新後: " + student.getGpa());
        
        // 異常値のテスト
        try {
            student.setGpa(-1.0);
        } catch (IllegalArgumentException e) {
            System.out.println("正常にエラーが検出されました: " + e.getMessage());
        }
    }
}
```

**学習ポイント**:
- カプセル化の重要性（privateフィールド + public getter/setter）
- データ検証の実装（setterでの値チェック）
- コンストラクタでの初期化
- toString()メソッドによる表示機能

## フェーズ2: 継承とポリモーフィズム（第5-7章）

異なる種類の学生（学部生、大学院生）を表現するため、継承を活用します。

```java
// Person.java - 抽象基底クラス
public abstract class Person {
    protected String id;
    protected String name;
    protected int age;
    
    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    public abstract String getRole();
    
    // 共通のゲッター・セッター
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
}

// Student.java - 改良版
public class Student extends Person {
    protected String department;
    protected double gpa;
    
    public Student(String studentId, String name, int age, String department) {
        super(studentId, name, age);
        this.department = department;
        this.gpa = 0.0;
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    // Student固有のメソッド
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
}

// GraduateStudent.java - 大学院生
public class GraduateStudent extends Student {
    private String researchField;
    private String advisorName;
    
    public GraduateStudent(String studentId, String name, int age, 
                          String department, String researchField) {
        super(studentId, name, age, department);
        this.researchField = researchField;
    }
    
    @Override
    public String getRole() {
        return "Graduate Student";
    }
    
    public String getResearchField() { return researchField; }
    public void setAdvisor(String advisorName) { this.advisorName = advisorName; }
}

// Gradeable.java - インターフェイス
public interface Gradeable {
    void addGrade(String courseName, double grade);
    double calculateGPA();
    Map<String, Double> getGrades();
}

// GradeableStudent.java - Gradeableを実装
public class GradeableStudent extends Student implements Gradeable {
    private Map<String, Double> grades;
    
    public GradeableStudent(String studentId, String name, int age, String department) {
        super(studentId, name, age, department);
        this.grades = new HashMap<>();
    }
    
    @Override
    public void addGrade(String courseName, double grade) {
        grades.put(courseName, grade);
        this.gpa = calculateGPA();
    }
    
    @Override
    public double calculateGPA() {
        if (grades.isEmpty()) return 0.0;
        return grades.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }
    
    @Override
    public Map<String, Double> getGrades() {
        return new HashMap<>(grades);
    }
}
```

## フェーズ3: コレクションとジェネリクス（第10-12章）

学生を管理するためのコレクションクラスを作成します。

```java
// StudentRepository.java
public class StudentRepository<T extends Student> {
    private Map<String, T> students;
    private List<T> studentList;
    
    public StudentRepository() {
        this.students = new HashMap<>();
        this.studentList = new ArrayList<>();
    }
    
    // 基本的なCRUD操作
    public void addStudent(T student) {
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("学生ID " + student.getId() + " は既に存在します");
        }
        students.put(student.getId(), student);
        studentList.add(student);
    }
    
    public T findById(String studentId) {
        return students.get(studentId);
    }
    
    public List<T> findAll() {
        return new ArrayList<>(studentList);
    }
    
    public boolean removeStudent(String studentId) {
        T removed = students.remove(studentId);
        if (removed != null) {
            studentList.remove(removed);
            return true;
        }
        return false;
    }
    
    // 検索機能
    public List<T> findByDepartment(String department) {
        return studentList.stream()
            .filter(s -> s.getDepartment().equals(department))
            .collect(Collectors.toList());
    }
    
    public List<T> findByGPARange(double minGPA, double maxGPA) {
        return studentList.stream()
            .filter(s -> s.getGpa() >= minGPA && s.getGpa() <= maxGPA)
            .collect(Collectors.toList());
    }
    
    // Stream APIを使った統計情報
    public double getAverageGPA() {
        return studentList.stream()
            .mapToDouble(Student::getGpa)
            .average()
            .orElse(0.0);
    }
    
    public Map<String, Long> getStudentCountByDepartment() {
        return studentList.stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.counting()
            ));
    }
    
    public List<T> getTopStudents(int count) {
        return studentList.stream()
            .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
            .limit(count)
            .collect(Collectors.toList());
    }
}

// StudentService.java - ビジネスロジック層
public class StudentService {
    private StudentRepository<GradeableStudent> repository;
    
    public StudentService() {
        this.repository = new StudentRepository<>();
    }
    
    // 複雑な検索クエリ
    public List<GradeableStudent> findStudentsWithConditions(
            String department, Double minGPA, Integer minAge) {
        
        return repository.findAll().stream()
            .filter(s -> department == null || s.getDepartment().equals(department))
            .filter(s -> minGPA == null || s.getGpa() >= minGPA)
            .filter(s -> minAge == null || s.getAge() >= minAge)
            .collect(Collectors.toList());
    }
    
    // 成績レポート生成
    public String generateGradeReport(String studentId) {
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            return "学生が見つかりません: " + studentId;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== 成績レポート ===\n");
        report.append("学生ID: ").append(student.getId()).append("\n");
        report.append("氏名: ").append(student.getName()).append("\n");
        report.append("学部: ").append(student.getDepartment()).append("\n");
        report.append("\n--- 履修科目 ---\n");
        
        student.getGrades().forEach((course, grade) -> 
            report.append(String.format("%-20s: %.1f\n", course, grade))
        );
        
        report.append("\nGPA: ").append(String.format("%.2f", student.getGpa()));
        return report.toString();
    }
}
```

## フェーズ4: 例外処理（第14章）

適切な例外処理を追加してシステムの堅牢性を高めます。

```java
// カスタム例外クラス
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String studentId) {
        super("学生が見つかりません: ID=" + studentId);
    }
}

public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String studentId) {
        super("学生IDが重複しています: ID=" + studentId);
    }
}

public class InvalidGradeException extends Exception {
    public InvalidGradeException(double grade) {
        super("無効な成績です: " + grade + " (0.0-4.0の範囲で入力してください)");
    }
}

// 例外処理を追加したStudentService
public class StudentService {
    private StudentRepository<GradeableStudent> repository;
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());
    
    public void registerStudent(GradeableStudent student) throws DuplicateStudentException {
        try {
            repository.addStudent(student);
            logger.info("学生を登録しました: " + student.getId());
        } catch (IllegalArgumentException e) {
            logger.warning("学生登録エラー: " + e.getMessage());
            throw new DuplicateStudentException(student.getId());
        }
    }
    
    public void updateGrade(String studentId, String courseName, double grade) 
            throws StudentNotFoundException, InvalidGradeException {
        
        if (grade < 0.0 || grade > 4.0) {
            throw new InvalidGradeException(grade);
        }
        
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        
        student.addGrade(courseName, grade);
        logger.info(String.format("成績を更新: %s - %s: %.1f", 
            studentId, courseName, grade));
    }
    
    public void importStudentsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 4) {
                        logger.warning("行 " + lineNumber + ": データ不足");
                        continue;
                    }
                    
                    GradeableStudent student = new GradeableStudent(
                        data[0].trim(),
                        data[1].trim(),
                        Integer.parseInt(data[2].trim()),
                        data[3].trim()
                    );
                    
                    registerStudent(student);
                    
                } catch (NumberFormatException e) {
                    logger.warning("行 " + lineNumber + ": 年齢の形式エラー");
                } catch (DuplicateStudentException e) {
                    logger.warning("行 " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("CSVファイル読み込みエラー: " + e.getMessage());
        }
    }
}
```

## フェーズ5: GUI実装（第18-19章）

Swingを使用してグラフィカルユーザーインターフェイスを実装します。

```java
// StudentManagementGUI.java - メインウィンドウ
public class StudentManagementGUI extends JFrame {
    private StudentService service;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    
    public StudentManagementGUI() {
        service = new StudentService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("学生管理システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // メニューバー
        createMenuBar();
        
        // 検索パネル
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // 学生一覧テーブル
        createStudentTable();
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        // ウィンドウサイズ設定
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem importItem = new JMenuItem("インポート");
        JMenuItem exportItem = new JMenuItem("エクスポート");
        JMenuItem exitItem = new JMenuItem("終了");
        
        importItem.addActionListener(e -> importStudents());
        exportItem.addActionListener(e -> exportStudents());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("ヘルプ");
        JMenuItem aboutItem = new JMenuItem("バージョン情報");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panel.add(new JLabel("検索:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchStudents());
        panel.add(searchField);
        
        panel.add(new JLabel("学部:"));
        departmentFilter = new JComboBox<>(new String[]{"すべて", "工学部", "理学部", "文学部"});
        departmentFilter.addActionListener(e -> filterByDepartment());
        panel.add(departmentFilter);
        
        JButton searchButton = new JButton("検索");
        searchButton.addActionListener(e -> searchStudents());
        panel.add(searchButton);
        
        return panel;
    }
    
    private void createStudentTable() {
        String[] columnNames = {"学生ID", "氏名", "年齢", "学部", "GPA"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = studentTable.getSelectedRow();
                    if (row >= 0) {
                        String studentId = (String) tableModel.getValueAt(row, 0);
                        showStudentDetails(studentId);
                    }
                }
            }
        });
        
        // 列幅の調整
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(50);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        
        JButton addButton = new JButton("学生追加");
        JButton editButton = new JButton("編集");
        JButton deleteButton = new JButton("削除");
        JButton refreshButton = new JButton("更新");
        
        addButton.addActionListener(e -> showAddStudentDialog());
        editButton.addActionListener(e -> editSelectedStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        refreshButton.addActionListener(e -> refreshTable());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "学生追加", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 入力フィールド
        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(15);
        JComboBox<String> deptCombo = new JComboBox<>(
            new String[]{"工学部", "理学部", "文学部"});
        
        // レイアウト
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("学生ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("氏名:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("年齢:"), gbc);
        gbc.gridx = 1;
        dialog.add(ageField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("学部:"), gbc);
        gbc.gridx = 1;
        dialog.add(deptCombo, gbc);
        
        // ボタン
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("キャンセル");
        
        okButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String dept = (String) deptCombo.getSelectedItem();
                
                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "IDと氏名は必須です", "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GradeableStudent student = new GradeableStudent(id, name, age, dept);
                service.registerStudent(student);
                refreshTable();
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "年齢は数値で入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            } catch (DuplicateStudentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<GradeableStudent> students = service.getAllStudents();
        
        for (GradeableStudent student : students) {
            Object[] row = {
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getDepartment(),
                String.format("%.2f", student.getGpa())
            };
            tableModel.addRow(row);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementGUI gui = new StudentManagementGUI();
            gui.setVisible(true);
        });
    }
}

// GradeManagementDialog.java - 成績管理ダイアログ
public class GradeManagementDialog extends JDialog {
    private StudentService service;
    private String studentId;
    private DefaultTableModel gradeTableModel;
    private JLabel gpaLabel;
    
    public GradeManagementDialog(Frame parent, StudentService service, String studentId) {
        super(parent, "成績管理", true);
        this.service = service;
        this.studentId = studentId;
        initializeUI();
        loadGrades();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 学生情報パネル
        JPanel infoPanel = new JPanel();
        try {
            GradeableStudent student = service.getStudent(studentId);
            infoPanel.add(new JLabel("学生: " + student.getName() + " (" + studentId + ")"));
            gpaLabel = new JLabel("GPA: " + String.format("%.2f", student.getGpa()));
            infoPanel.add(gpaLabel);
        } catch (StudentNotFoundException e) {
            // エラー処理
        }
        add(infoPanel, BorderLayout.NORTH);
        
        // 成績テーブル
        String[] columns = {"科目名", "成績"};
        gradeTableModel = new DefaultTableModel(columns, 0);
        JTable gradeTable = new JTable(gradeTableModel);
        add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("成績追加");
        JButton deleteButton = new JButton("削除");
        JButton closeButton = new JButton("閉じる");
        
        addButton.addActionListener(e -> showAddGradeDialog());
        deleteButton.addActionListener(e -> deleteSelectedGrade());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(400, 300);
        setLocationRelativeTo(getParent());
    }
    
    private void showAddGradeDialog() {
        JTextField courseField = new JTextField(20);
        JTextField gradeField = new JTextField(10);
        
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("科目名:"));
        panel.add(courseField);
        panel.add(new JLabel("成績 (0.0-4.0):"));
        panel.add(gradeField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "成績追加", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String course = courseField.getText().trim();
                double grade = Double.parseDouble(gradeField.getText().trim());
                
                service.updateGrade(studentId, course, grade);
                loadGrades();
                updateGPA();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "成績は数値で入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            } catch (StudentNotFoundException | InvalidGradeException e) {
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadGrades() {
        gradeTableModel.setRowCount(0);
        try {
            GradeableStudent student = service.getStudent(studentId);
            Map<String, Double> grades = student.getGrades();
            
            for (Map.Entry<String, Double> entry : grades.entrySet()) {
                Object[] row = {entry.getKey(), entry.getValue()};
                gradeTableModel.addRow(row);
            }
        } catch (StudentNotFoundException e) {
            // エラー処理
        }
    }
    
    private void updateGPA() {
        try {
            GradeableStudent student = service.getStudent(studentId);
            gpaLabel.setText("GPA: " + String.format("%.2f", student.getGpa()));
        } catch (StudentNotFoundException e) {
            // エラー処理
        }
    }
}
```

## フェーズ6: データベース統合（第20章）

データの永続化のためにデータベースを統合します。

```java
// DatabaseManager.java
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:student_management.db";
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        createTables();
    }
    
    private void createTables() throws SQLException {
        String createStudentTable = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER NOT NULL,
                department TEXT NOT NULL,
                gpa REAL DEFAULT 0.0,
                student_type TEXT NOT NULL
            )
        """;
        
        String createGradeTable = """
            CREATE TABLE IF NOT EXISTS grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT NOT NULL,
                course_name TEXT NOT NULL,
                grade REAL NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students(student_id),
                UNIQUE(student_id, course_name)
            )
        """;
        
        String createGraduateStudentTable = """
            CREATE TABLE IF NOT EXISTS graduate_students (
                student_id TEXT PRIMARY KEY,
                research_field TEXT NOT NULL,
                advisor_name TEXT,
                FOREIGN KEY (student_id) REFERENCES students(student_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createStudentTable);
            stmt.execute(createGradeTable);
            stmt.execute(createGraduateStudentTable);
        }
    }
    
    public void saveStudent(Student student) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO students (student_id, name, age, department, gpa, student_type)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setInt(3, student.getAge());
            pstmt.setString(4, student.getDepartment());
            pstmt.setDouble(5, student.getGpa());
            pstmt.setString(6, student.getClass().getSimpleName());
            
            pstmt.executeUpdate();
            
            // 大学院生の場合は追加情報を保存
            if (student instanceof GraduateStudent) {
                saveGraduateStudentInfo((GraduateStudent) student);
            }
            
            // 成績情報を保存
            if (student instanceof GradeableStudent) {
                saveGrades((GradeableStudent) student);
            }
        }
    }
    
    private void saveGraduateStudentInfo(GraduateStudent student) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO graduate_students (student_id, research_field, advisor_name)
            VALUES (?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getResearchField());
            pstmt.setString(3, student.getAdvisorName());
            pstmt.executeUpdate();
        }
    }
    
    private void saveGrades(GradeableStudent student) throws SQLException {
        // 既存の成績を削除
        String deleteSql = "DELETE FROM grades WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setString(1, student.getId());
            pstmt.executeUpdate();
        }
        
        // 新しい成績を挿入
        String insertSql = """
            INSERT INTO grades (student_id, course_name, grade)
            VALUES (?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            Map<String, Double> grades = student.getGrades();
            for (Map.Entry<String, Double> entry : grades.entrySet()) {
                pstmt.setString(1, student.getId());
                pstmt.setString(2, entry.getKey());
                pstmt.setDouble(3, entry.getValue());
                pstmt.executeUpdate();
            }
        }
    }
    
    public List<GradeableStudent> loadAllStudents() throws SQLException {
        List<GradeableStudent> students = new ArrayList<>();
        
        String sql = """
            SELECT s.*, g.course_name, g.grade
            FROM students s
            LEFT JOIN grades g ON s.student_id = g.student_id
            ORDER BY s.student_id, g.course_name
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            GradeableStudent currentStudent = null;
            String currentId = null;
            
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                
                // 新しい学生の場合
                if (!studentId.equals(currentId)) {
                    if (currentStudent != null) {
                        students.add(currentStudent);
                    }
                    
                    currentStudent = new GradeableStudent(
                        studentId,
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("department")
                    );
                    currentId = studentId;
                }
                
                // 成績情報を追加
                String courseName = rs.getString("course_name");
                if (courseName != null) {
                    double grade = rs.getDouble("grade");
                    currentStudent.addGrade(courseName, grade);
                }
            }
            
            // 最後の学生を追加
            if (currentStudent != null) {
                students.add(currentStudent);
            }
        }
        
        return students;
    }
    
    public void deleteStudent(String studentId) throws SQLException {
        // トランザクション開始
        connection.setAutoCommit(false);
        
        try {
            // 成績情報を削除
            String deleteGrades = "DELETE FROM grades WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGrades)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            
            // 大学院生情報を削除
            String deleteGradInfo = "DELETE FROM graduate_students WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGradInfo)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            
            // 学生情報を削除
            String deleteStudent = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteStudent)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

// データベース対応版StudentService
public class StudentService {
    private StudentRepository<GradeableStudent> repository;
    private DatabaseManager dbManager;
    
    public StudentService() {
        this.repository = new StudentRepository<>();
        try {
            this.dbManager = new DatabaseManager();
            loadStudentsFromDatabase();
        } catch (SQLException e) {
            logger.severe("データベース接続エラー: " + e.getMessage());
        }
    }
    
    private void loadStudentsFromDatabase() {
        try {
            List<GradeableStudent> students = dbManager.loadAllStudents();
            for (GradeableStudent student : students) {
                repository.addStudent(student);
            }
            logger.info("データベースから " + students.size() + " 件の学生情報を読み込みました");
        } catch (SQLException e) {
            logger.severe("データベース読み込みエラー: " + e.getMessage());
        }
    }
    
    public void registerStudent(GradeableStudent student) throws DuplicateStudentException {
        repository.addStudent(student);
        
        // データベースに保存
        try {
            dbManager.saveStudent(student);
            logger.info("学生をデータベースに保存しました: " + student.getId());
        } catch (SQLException e) {
            logger.severe("データベース保存エラー: " + e.getMessage());
        }
    }
    
    public void updateGrade(String studentId, String courseName, double grade) 
            throws StudentNotFoundException, InvalidGradeException {
        
        if (grade < 0.0 || grade > 4.0) {
            throw new InvalidGradeException(grade);
        }
        
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        
        student.addGrade(courseName, grade);
        
        // データベースを更新
        try {
            dbManager.saveStudent(student);
            logger.info("成績をデータベースに保存しました: " + studentId);
        } catch (SQLException e) {
            logger.severe("データベース更新エラー: " + e.getMessage());
        }
    }
    
    public void exportToCSV(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("学生ID,氏名,年齢,学部,GPA");
            
            for (GradeableStudent student : repository.findAll()) {
                writer.printf("%s,%s,%d,%s,%.2f%n",
                    student.getId(),
                    student.getName(),
                    student.getAge(),
                    student.getDepartment(),
                    student.getGpa()
                );
            }
            
            logger.info("CSVエクスポートが完了しました: " + filePath);
        }
    }
}
```

## フェーズ7: テスト実装（第22章）

JUnitを使用した単体テストを作成します。

```java
// StudentTest.java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    private GradeableStudent student;
    
    @BeforeEach
    public void setUp() {
        student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
    }
    
    @Test
    @DisplayName("学生の基本情報が正しく設定される")
    public void testStudentCreation() {
        assertEquals("S001", student.getId());
        assertEquals("田中太郎", student.getName());
        assertEquals(20, student.getAge());
        assertEquals("工学部", student.getDepartment());
        assertEquals(0.0, student.getGpa());
    }
    
    @Test
    @DisplayName("成績追加とGPA計算が正しく動作する")
    public void testGradeAdditionAndGPACalculation() {
        student.addGrade("数学", 3.5);
        student.addGrade("物理", 4.0);
        student.addGrade("英語", 3.0);
        
        assertEquals(3.5, student.calculateGPA(), 0.01);
        assertEquals(3, student.getGrades().size());
    }
    
    @Test
    @DisplayName("年齢の不正な値は設定されない")
    public void testInvalidAge() {
        student.setAge(-5);
        assertEquals(20, student.getAge()); // 変更されない
        
        student.setAge(25);
        assertEquals(25, student.getAge()); // 正常な値は設定される
    }
    
    @Test
    @DisplayName("GPAの範囲チェックが機能する")
    public void testGPARange() {
        student.setGpa(5.0); // 範囲外
        assertEquals(0.0, student.getGpa()); // 変更されない
        
        student.setGpa(3.5); // 正常範囲
        assertEquals(3.5, student.getGpa());
    }
}

// StudentRepositoryTest.java
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class StudentRepositoryTest {
    private StudentRepository<GradeableStudent> repository;
    
    @BeforeEach
    public void setUp() {
        repository = new StudentRepository<>();
    }
    
    @Test
    @DisplayName("学生の追加と検索が正しく動作する")
    public void testAddAndFind() {
        GradeableStudent student1 = new GradeableStudent("S001", "田中", 20, "工学部");
        GradeableStudent student2 = new GradeableStudent("S002", "山田", 21, "理学部");
        
        repository.addStudent(student1);
        repository.addStudent(student2);
        
        assertEquals(student1, repository.findById("S001"));
        assertEquals(student2, repository.findById("S002"));
        assertNull(repository.findById("S999"));
    }
    
    @Test
    @DisplayName("重複する学生IDは例外を発生させる")
    public void testDuplicateStudentId() {
        GradeableStudent student1 = new GradeableStudent("S001", "田中", 20, "工学部");
        GradeableStudent student2 = new GradeableStudent("S001", "山田", 21, "理学部");
        
        repository.addStudent(student1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            repository.addStudent(student2);
        });
    }
    
    @Test
    @DisplayName("学部による検索が正しく動作する")
    public void testFindByDepartment() {
        repository.addStudent(new GradeableStudent("S001", "田中", 20, "工学部"));
        repository.addStudent(new GradeableStudent("S002", "山田", 21, "工学部"));
        repository.addStudent(new GradeableStudent("S003", "佐藤", 22, "理学部"));
        
        List<GradeableStudent> engineeringStudents = repository.findByDepartment("工学部");
        assertEquals(2, engineeringStudents.size());
        
        List<GradeableStudent> scienceStudents = repository.findByDepartment("理学部");
        assertEquals(1, scienceStudents.size());
    }
    
    @Test
    @DisplayName("成績上位の学生取得が正しく動作する")
    public void testGetTopStudents() {
        GradeableStudent student1 = new GradeableStudent("S001", "田中", 20, "工学部");
        student1.setGpa(3.8);
        GradeableStudent student2 = new GradeableStudent("S002", "山田", 21, "理学部");
        student2.setGpa(3.2);
        GradeableStudent student3 = new GradeableStudent("S003", "佐藤", 22, "文学部");
        student3.setGpa(3.9);
        
        repository.addStudent(student1);
        repository.addStudent(student2);
        repository.addStudent(student3);
        
        List<GradeableStudent> topStudents = repository.getTopStudents(2);
        assertEquals(2, topStudents.size());
        assertEquals("S003", topStudents.get(0).getId()); // 最高GPA
        assertEquals("S001", topStudents.get(1).getId()); // 2番目
    }
}

// StudentServiceTest.java
import org.junit.jupiter.api.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {
    @Mock
    private DatabaseManager mockDbManager;
    
    private StudentService service;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new StudentService();
    }
    
    @Test
    @DisplayName("学生登録時に重複チェックが機能する")
    public void testRegisterDuplicateStudent() {
        GradeableStudent student1 = new GradeableStudent("S001", "田中", 20, "工学部");
        GradeableStudent student2 = new GradeableStudent("S001", "山田", 21, "理学部");
        
        assertDoesNotThrow(() -> service.registerStudent(student1));
        assertThrows(DuplicateStudentException.class, 
            () -> service.registerStudent(student2));
    }
    
    @Test
    @DisplayName("成績更新時の例外処理が正しく動作する")
    public void testUpdateGradeExceptions() {
        // 存在しない学生
        assertThrows(StudentNotFoundException.class, 
            () -> service.updateGrade("S999", "数学", 3.5));
        
        // 無効な成績
        GradeableStudent student = new GradeableStudent("S001", "田中", 20, "工学部");
        assertDoesNotThrow(() -> service.registerStudent(student));
        
        assertThrows(InvalidGradeException.class, 
            () -> service.updateGrade("S001", "数学", 5.0));
        assertThrows(InvalidGradeException.class, 
            () -> service.updateGrade("S001", "数学", -1.0));
    }
    
    @Test
    @DisplayName("成績レポート生成が正しく動作する")
    public void testGenerateGradeReport() throws Exception {
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        service.registerStudent(student);
        
        service.updateGrade("S001", "数学", 3.5);
        service.updateGrade("S001", "物理", 4.0);
        
        String report = service.generateGradeReport("S001");
        
        assertTrue(report.contains("S001"));
        assertTrue(report.contains("田中太郎"));
        assertTrue(report.contains("工学部"));
        assertTrue(report.contains("数学"));
        assertTrue(report.contains("3.5"));
        assertTrue(report.contains("物理"));
        assertTrue(report.contains("4.0"));
        assertTrue(report.contains("3.75")); // GPA
    }
}

// 統合テスト
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIntegrationTest {
    private DatabaseManager dbManager;
    private static final String TEST_DB = "test_student.db";
    
    @BeforeEach
    public void setUp() throws SQLException {
        // テスト用データベース作成
        System.setProperty("db.url", "jdbc:sqlite:" + TEST_DB);
        dbManager = new DatabaseManager();
    }
    
    @AfterEach
    public void tearDown() throws SQLException {
        dbManager.close();
        new File(TEST_DB).delete();
    }
    
    @Test
    @DisplayName("学生情報の保存と読み込みが正しく動作する")
    public void testSaveAndLoadStudent() throws SQLException {
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        student.addGrade("数学", 3.5);
        student.addGrade("物理", 4.0);
        
        dbManager.saveStudent(student);
        
        List<GradeableStudent> loaded = dbManager.loadAllStudents();
        assertEquals(1, loaded.size());
        
        GradeableStudent loadedStudent = loaded.get(0);
        assertEquals("S001", loadedStudent.getId());
        assertEquals("田中太郎", loadedStudent.getName());
        assertEquals(2, loadedStudent.getGrades().size());
        assertEquals(3.75, loadedStudent.getGpa(), 0.01);
    }
    
    @Test
    @DisplayName("トランザクション処理が正しく動作する")
    public void testTransactionRollback() throws SQLException {
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        dbManager.saveStudent(student);
        
        // 削除処理中に例外を発生させる（モック化やテスト用フックを使用）
        // ここでは概念的なテストケースとして記載
        
        // トランザクションがロールバックされ、データが残っていることを確認
        List<GradeableStudent> students = dbManager.loadAllStudents();
        assertEquals(1, students.size());
    }
}
```

## まとめ

この統合プロジェクトでは、Javaプログラミングの基礎から応用まで、段階的に技術を積み重ねて実践的なアプリケーションを構築しました。

### 学習したポイント

1. **オブジェクト指向設計**
   - クラスの設計と実装
   - 継承とポリモーフィズムの活用
   - インターフェイスと抽象クラスの使い分け

2. **データ構造とアルゴリズム**
   - コレクションフレームワークの活用
   - ジェネリクスによる型安全性の確保
   - Stream APIを使った関数型プログラミング

3. **例外処理とエラーハンドリング**
   - カスタム例外の設計
   - 適切な例外処理の実装
   - ロギングの活用

4. **GUI開発**
   - Swingを使ったデスクトップアプリケーション
   - イベント駆動プログラミング
   - MVCパターンの実装

5. **データ永続化**
   - JDBCを使ったデータベース操作
   - トランザクション管理
   - SQLとJavaの統合

6. **テスト駆動開発**
   - JUnitによる単体テスト
   - モックを使った依存性の分離
   - 統合テストの実装

### 発展課題

このプロジェクトをさらに発展させるためのアイデア：

1. **Web化**: Spring Bootを使ってWebアプリケーションに変換
2. **セキュリティ**: 認証・認可機能の追加
3. **レポート機能**: PDFやExcel形式でのレポート生成
4. **国際化**: 多言語対応の実装
5. **パフォーマンス最適化**: キャッシュ機能やインデックスの追加
6. **マイクロサービス化**: 機能ごとにサービスを分割

このプロジェクトを通じて、実践的なJavaアプリケーション開発の全体像を理解し、さまざまな技術を組み合わせて問題を解決する能力を身につけることができました。

## 統合学習ガイド: 付録の効果的活用

統合プロジェクトで使用した技術をさらに深く理解するため、以下の付録を体系的に学習することを推奨します：

### 開発環境の最適化
**付録A: 開発環境の構築**
- 本プロジェクトで使用したJDKの詳細理解
- より高度な開発環境の設定
- パフォーマンス重視の環境構築

### 技術詳細の深堀り
**付録B: 技術的詳細解説（Deep Dive）**

プロジェクトの各フェーズに対応する詳細技術：

**フェーズ1-2関連**:
- B.04「ソフトウェア設計原則」: より堅牢なクラス設計
- B.05「仮想メソッドテーブル」: 継承の内部実装理解

**フェーズ3-4関連**:
- B.08「コレクションの内部実装」: 効率的なデータ構造選択
- B.09「型消去とジェネリクス」: タイプセーフなコレクション設計

**フェーズ5-6関連**:
- B.16「Javaメモリモデル」: マルチスレッド環境での安全な実装
- B.20「データベース高度機能」: 高性能なデータアクセス層

**フェーズ7関連**:
- B.21「テスト戦略」: テストピラミッドと品質保証

### 理論的基盤の理解
**付録C: ソフトウェア工学の理論的基盤**
- なぜこのような設計を選択したのかの理論的背景
- 品質メトリクスによる客観的な評価方法
- アーキテクチャ理論に基づく長期保守性の考慮

### 学習スケジュール提案

**段階1（基礎固め）**: 付録A + B.01-B.04
**段階2（実装強化）**: プロジェクト構築 + 対応するB.05-B.16
**段階3（理論統合）**: 付録C全体の学習
**段階4（発展課題）**: B.20-B.21 + 実際のプロダクト開発

この体系的なアプローチにより、単なるプロジェクト完成を超えた、プロフェッショナルレベルのJava開発スキルを獲得できます。