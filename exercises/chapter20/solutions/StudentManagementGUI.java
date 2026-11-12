/**
 * 第20章 課題1: 学生名簿の表（解答例）
 *
 * AbstractTableModelを継承したモデルでJTableに学生を表示し、
 * 追加・編集・削除、列の見出しでの並べ替え、検索欄での絞り込み、CSVへの保存と読込を行います。
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StudentManagementGUI extends JFrame {

    private JTable studentTable;
    private StudentTableModel tableModel;
    private TableRowSorter<StudentTableModel> sorter;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;
    private JButton saveButton, loadButton;
    private JLabel statusLabel;

    // 学生のデータ（画面の部品を知らない）
    public static class Student {
        private String id;
        private String name;
        private int age;
        private int grade;
        private String email;
        private LocalDate enrollmentDate;

        public Student(String id, String name, int age, int grade, String email, LocalDate enrollmentDate) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.grade = grade;
            this.email = email;
            this.enrollmentDate = enrollmentDate;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public int getGrade() { return grade; }
        public String getEmail() { return email; }
        public LocalDate getEnrollmentDate() { return enrollmentDate; }

        // CSVの1行にする（項目にカンマを含めない前提の簡単な形式）
        public String toCsv() {
            return String.join(",", id, name, String.valueOf(age), String.valueOf(grade),
                    email, enrollmentDate.toString());
        }

        // CSVの1行から作る。形式が違えば例外を投げる
        public static Student fromCsv(String line) {
            String[] parts = line.split(",", -1);
            if (parts.length != 6) {
                throw new IllegalArgumentException("項目の数が6ではありません: " + line);
            }
            return new Student(parts[0], parts[1], Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]), parts[4], LocalDate.parse(parts[5]));
        }
    }

    // テーブルモデル。学生のリストを持ち、変更したらfireTable...で表に知らせる
    private class StudentTableModel extends AbstractTableModel {
        private final String[] columnNames = {"学籍番号", "氏名", "年齢", "学年", "メールアドレス", "入学日"};
        private final List<Student> students = new ArrayList<>();

        @Override
        public int getRowCount() {
            return students.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Student s = students.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> s.getId();
                case 1 -> s.getName();
                case 2 -> s.getAge();
                case 3 -> s.getGrade();
                case 4 -> s.getEmail();
                case 5 -> s.getEnrollmentDate();
                default -> "";
            };
        }

        // 列の型を返すと、TableRowSorterが年齢や学年を数として並べる（文字列なら"10"が"9"より前になる）
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 2, 3 -> Integer.class;
                case 5 -> LocalDate.class;
                default -> String.class;
            };
        }

        public Student getStudentAt(int rowIndex) {
            return students.get(rowIndex);
        }

        public List<Student> getStudents() {
            return new ArrayList<>(students);
        }

        public boolean containsId(String id) {
            for (Student s : students) {
                if (s.getId().equals(id)) {
                    return true;
                }
            }
            return false;
        }

        public void addStudent(Student student) {
            students.add(student);
            int row = students.size() - 1;
            fireTableRowsInserted(row, row);
        }

        public void updateStudent(int rowIndex, Student student) {
            students.set(rowIndex, student);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }

        public void removeStudent(int rowIndex) {
            students.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        public void setStudents(List<Student> newStudents) {
            students.clear();
            students.addAll(newStudents);
            fireTableDataChanged();
        }
    }

    public StudentManagementGUI() {
        setTitle("学生名簿");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 450);
        setLocationRelativeTo(null);

        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadSampleData();
    }

    private void initializeComponents() {
        tableModel = new StudentTableModel();
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 見出しをクリックすると並べ替える。絞り込みもこのソーターで行う
        sorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(sorter);

        searchField = new JTextField(20);
        addButton = new JButton("追加");
        editButton = new JButton("編集");
        deleteButton = new JButton("削除");
        saveButton = new JButton("保存");
        loadButton = new JButton("読込");
        statusLabel = new JLabel(" ");
    }

    private void setupLayout() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("検索:"));
        topPanel.add(searchField);
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> showAddStudentDialog());
        editButton.addActionListener(e -> showEditStudentDialog());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        saveButton.addActionListener(e -> saveToCSV());
        loadButton.addActionListener(e -> loadFromCSV());

        // 入力のたびに絞り込む（第19章のDocumentListener）
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterStudents(searchField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterStudents(searchField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterStudents(searchField.getText()); }
        });
    }

    private void showAddStudentDialog() {
        Student student = showStudentDialog("学生の追加", null);
        if (student == null) {
            return;
        }
        if (tableModel.containsId(student.getId())) {
            showError("その学籍番号はすでに登録されています: " + student.getId());
            return;
        }
        tableModel.addStudent(student);
        updateStatus(student.getName() + "さんを追加しました");
    }

    private void showEditStudentDialog() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow < 0) {
            showError("編集する学生を選んでください");
            return;
        }
        // 表示の行番号は並べ替えや絞り込みでずれるので、元のデータの行番号に直す
        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        Student student = showStudentDialog("学生の編集", tableModel.getStudentAt(modelRow));
        if (student != null) {
            tableModel.updateStudent(modelRow, student);
            updateStatus(student.getName() + "さんの情報を変更しました");
        }
    }

    private void deleteSelectedStudent() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow < 0) {
            showError("削除する学生を選んでください");
            return;
        }
        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        Student student = tableModel.getStudentAt(modelRow);
        int answer = JOptionPane.showConfirmDialog(this, student.getName() + "さんを削除しますか？",
                "削除の確認", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            tableModel.removeStudent(modelRow);
            updateStatus(student.getName() + "さんを削除しました");
        }
    }

    // 入力した文字を含む行だけを残す。Pattern.quoteで記号もそのまま探し、(?i)で大文字と小文字を区別しない
    private void filterStudents(String searchText) {
        if (searchText.isBlank()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(searchText.trim())));
        }
        updateStatus(studentTable.getRowCount() + "件を表示しています");
    }

    private void saveToCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Student s : tableModel.getStudents()) {
                writer.write(s.toCsv());
                writer.newLine();
            }
            updateStatus(path.getFileName() + "に保存しました");
        } catch (IOException e) {
            showError("保存できませんでした: " + e.getMessage());
        }
    }

    private void loadFromCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        List<Student> loaded = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    loaded.add(Student.fromCsv(line));
                }
            }
            tableModel.setStudents(loaded);
            updateStatus(path.getFileName() + "から" + loaded.size() + "人を読み込みました");
        } catch (IOException | IllegalArgumentException | DateTimeParseException e) {
            // NumberFormatExceptionはIllegalArgumentExceptionのサブクラスなので、ここで受け取れる
            showError("読み込めませんでした: " + e.getMessage());
        }
    }

    // 追加と編集で共通の入力ダイアログ。キャンセルや入力の誤りのときはnullを返す
    private Student showStudentDialog(String title, Student current) {
        JTextField idField = new JTextField(current == null ? "" : current.getId());
        JTextField nameField = new JTextField(current == null ? "" : current.getName());
        JTextField ageField = new JTextField(current == null ? "" : String.valueOf(current.getAge()));
        JTextField gradeField = new JTextField(current == null ? "" : String.valueOf(current.getGrade()));
        JTextField emailField = new JTextField(current == null ? "" : current.getEmail());
        JTextField dateField = new JTextField(
                current == null ? LocalDate.of(2026, 4, 1).toString() : current.getEnrollmentDate().toString());
        // 編集では学籍番号を変えさせない
        idField.setEditable(current == null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("学籍番号:"));
        panel.add(idField);
        panel.add(new JLabel("氏名:"));
        panel.add(nameField);
        panel.add(new JLabel("年齢:"));
        panel.add(ageField);
        panel.add(new JLabel("学年:"));
        panel.add(gradeField);
        panel.add(new JLabel("メールアドレス:"));
        panel.add(emailField);
        panel.add(new JLabel("入学日（2026-04-01の形）:"));
        panel.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (id.isEmpty() || name.isEmpty() || id.contains(",") || name.contains(",") || email.contains(",")) {
            showError("学籍番号と氏名を入力してください（カンマは使えません）");
            return null;
        }
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            int grade = Integer.parseInt(gradeField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            return new Student(id, name, age, grade, email, date);
        } catch (NumberFormatException e) {
            showError("年齢と学年は整数で入力してください");
        } catch (DateTimeParseException e) {
            showError("入学日は2026-04-01の形で入力してください");
        }
        return null;
    }

    private void loadSampleData() {
        tableModel.addStudent(new Student("S001", "山田太郎", 19, 2, "yamada@example.com", LocalDate.of(2025, 4, 1)));
        tableModel.addStudent(new Student("S002", "鈴木花子", 18, 1, "suzuki@example.com", LocalDate.of(2026, 4, 1)));
        tableModel.addStudent(new Student("S003", "佐藤次郎", 20, 3, "sato@example.com", LocalDate.of(2024, 4, 1)));
        tableModel.addStudent(new Student("S004", "高橋美咲", 19, 2, "takahashi@example.com", LocalDate.of(2025, 4, 1)));
        tableModel.addStudent(new Student("S005", "田中健太", 21, 4, "tanaka@example.com", LocalDate.of(2023, 4, 1)));
        updateStatus(tableModel.getRowCount() + "人の学生を表示しています");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementGUI().setVisible(true));
    }
}
