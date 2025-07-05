/**
 * 第19章 課題1: 学生管理GUIアプリケーション - 解答例
 * 
 * JTableとMVCパターンを使った学生管理システム
 * 
 * 学習ポイント:
 * - JTableとTableModelの使い方
 * - MVCパターンの実装
 * - イベント駆動プログラミング
 * - データバインディング
 * - オブザーバーパターンの活用
 */

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StudentManagementGUI extends JFrame {
    
    // GUI コンポーネント
    private JTable studentTable;
    private StudentTableModel tableModel;
    private TableRowSorter<StudentTableModel> rowSorter;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;
    private JButton saveButton, loadButton;
    private JLabel statusLabel;
    
    // データ
    private List<Student> students;
    
    // 学生データモデル
    public static class Student {
        private String id;
        private String name;
        private int age;
        private String grade;
        private String email;
        private LocalDate enrollmentDate;
        
        public Student(String id, String name, int age, String grade, String email, LocalDate enrollmentDate) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.grade = grade;
            this.email = email;
            this.enrollmentDate = enrollmentDate;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public LocalDate getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
        
        @Override
        public String toString() {
            return String.format("Student{id='%s', name='%s', age=%d, grade='%s', email='%s', enrollmentDate=%s}",
                    id, name, age, grade, email, enrollmentDate);
        }
        
        // CSV用のメソッド
        public String toCSV() {
            return String.join(",", id, name, String.valueOf(age), grade, email, 
                    enrollmentDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        public static Student fromCSV(String csvLine) {
            String[] parts = csvLine.split(",");
            if (parts.length != 6) {
                throw new IllegalArgumentException("Invalid CSV format");
            }
            return new Student(
                    parts[0].trim(),
                    parts[1].trim(),
                    Integer.parseInt(parts[2].trim()),
                    parts[3].trim(),
                    parts[4].trim(),
                    LocalDate.parse(parts[5].trim())
            );
        }
    }
    
    // テーブルモデル（MVCパターンのModel）
    private class StudentTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "学籍番号", "氏名", "年齢", "学年", "メールアドレス", "入学日"
        };
        
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
            Student student = students.get(rowIndex);
            switch (columnIndex) {
                case 0: return student.getId();
                case 1: return student.getName();
                case 2: return student.getAge();
                case 3: return student.getGrade();
                case 4: return student.getEmail();
                case 5: return student.getEnrollmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                default: return null;
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: case 1: case 3: case 4: case 5: return String.class;
                case 2: return Integer.class;
                default: return Object.class;
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // 学籍番号は編集不可
            return columnIndex != 0;
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            Student student = students.get(rowIndex);
            try {
                switch (columnIndex) {
                    case 1: student.setName(value.toString()); break;
                    case 2: student.setAge(Integer.parseInt(value.toString())); break;
                    case 3: student.setGrade(value.toString()); break;
                    case 4: student.setEmail(value.toString()); break;
                    case 5: student.setEnrollmentDate(LocalDate.parse(value.toString())); break;
                }
                fireTableCellUpdated(rowIndex, columnIndex);
                updateStatus("学生情報を更新しました: " + student.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(StudentManagementGUI.this, 
                        "無効な値です: " + e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public Student getStudentAt(int rowIndex) {
            return students.get(rowIndex);
        }
        
        public void addStudent(Student student) {
            students.add(student);
            int newRow = students.size() - 1;
            fireTableRowsInserted(newRow, newRow);
            updateStatus("学生を追加しました: " + student.getName());
        }
        
        public void removeStudent(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < students.size()) {
                Student student = students.remove(rowIndex);
                fireTableRowsDeleted(rowIndex, rowIndex);
                updateStatus("学生を削除しました: " + student.getName());
            }
        }
        
        public void updateStudent(int rowIndex, Student student) {
            if (rowIndex >= 0 && rowIndex < students.size()) {
                students.set(rowIndex, student);
                fireTableRowsUpdated(rowIndex, rowIndex);
                updateStatus("学生情報を更新しました: " + student.getName());
            }
        }
    }
    
    public StudentManagementGUI() {
        students = new ArrayList<>();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
        loadSampleData();
    }
    
    // GUIコンポーネントの初期化
    private void initializeComponents() {
        // テーブルとモデル
        tableModel = new StudentTableModel();
        studentTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);
        
        // テーブルの設定
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        studentTable.setGridColor(Color.LIGHT_GRAY);
        studentTable.setShowGrid(true);
        
        // 列幅の設定
        TableColumnModel columnModel = studentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // ID
        columnModel.getColumn(1).setPreferredWidth(120); // 名前
        columnModel.getColumn(2).setPreferredWidth(60);  // 年齢
        columnModel.getColumn(3).setPreferredWidth(80);  // 学年
        columnModel.getColumn(4).setPreferredWidth(180); // メール
        columnModel.getColumn(5).setPreferredWidth(100); // 入学日
        
        // 検索フィールド
        searchField = new JTextField(20);
        searchField.setToolTipText("学生情報を検索（名前、学年、メールアドレス）");
        
        // ボタン
        addButton = new JButton("追加");
        editButton = new JButton("編集");
        deleteButton = new JButton("削除");
        saveButton = new JButton("保存");
        loadButton = new JButton("読込");
        
        // ボタンのアイコン設定（利用可能な場合）
        addButton.setIcon(new ImageIcon("icons/add.png"));
        editButton.setIcon(new ImageIcon("icons/edit.png"));
        deleteButton.setIcon(new ImageIcon("icons/delete.png"));
        saveButton.setIcon(new ImageIcon("icons/save.png"));
        loadButton.setIcon(new ImageIcon("icons/load.png"));
        
        // ステータスラベル
        statusLabel = new JLabel("学生管理システム開始");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    }
    
    // レイアウトの設定
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 上部パネル（検索）
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("検索:"));
        topPanel.add(searchField);
        add(topPanel, BorderLayout.NORTH);
        
        // 中央パネル（テーブル）
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("学生一覧"));
        add(scrollPane, BorderLayout.CENTER);
        
        // 右パネル（ボタン）
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        add(buttonPanel, BorderLayout.EAST);
        
        // 下部パネル（ステータス）
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    // イベントリスナーの設定
    private void setupEventListeners() {
        // ボタンイベント
        addButton.addActionListener(e -> showAddStudentDialog());
        editButton.addActionListener(e -> showEditStudentDialog());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        saveButton.addActionListener(e -> saveToCSV());
        loadButton.addActionListener(e -> loadFromCSV());
        
        // 検索フィールドのリアルタイム検索
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterStudents(); }
            
            @Override
            public void removeUpdate(DocumentEvent e) { filterStudents(); }
            
            @Override
            public void changedUpdate(DocumentEvent e) { filterStudents(); }
        });
        
        // テーブル選択イベント
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = studentTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // 初期状態では編集・削除ボタンを無効化
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    // フレームの設定
    private void setupFrame() {
        setTitle("学生管理システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // メニューバーの追加
        setupMenuBar();
    }
    
    // メニューバーの設定
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem newItem = new JMenuItem("新規");
        JMenuItem openItem = new JMenuItem("開く");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("終了");
        
        newItem.addActionListener(e -> clearAllStudents());
        openItem.addActionListener(e -> loadFromCSV());
        saveItem.addActionListener(e -> saveToCSV());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 編集メニュー
        JMenu editMenu = new JMenu("編集");
        JMenuItem addItem = new JMenuItem("学生追加");
        JMenuItem editItem = new JMenuItem("学生編集");
        JMenuItem deleteItem = new JMenuItem("学生削除");
        
        addItem.addActionListener(e -> showAddStudentDialog());
        editItem.addActionListener(e -> showEditStudentDialog());
        deleteItem.addActionListener(e -> deleteSelectedStudent());
        
        editMenu.add(addItem);
        editMenu.add(editItem);
        editMenu.add(deleteItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }
    
    // 学生追加ダイアログ
    private void showAddStudentDialog() {
        StudentDialog dialog = new StudentDialog(this, "学生追加", null);
        dialog.setVisible(true);
        
        if (dialog.isOK()) {
            Student newStudent = dialog.getStudent();
            // ID重複チェック
            if (students.stream().anyMatch(s -> s.getId().equals(newStudent.getId()))) {
                JOptionPane.showMessageDialog(this, "学籍番号が重複しています", "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            tableModel.addStudent(newStudent);
        }
    }
    
    // 学生編集ダイアログ
    private void showEditStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "編集する学生を選択してください", "情報", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // ビューの行をモデルの行に変換
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        Student student = tableModel.getStudentAt(modelRow);
        
        StudentDialog dialog = new StudentDialog(this, "学生編集", student);
        dialog.setVisible(true);
        
        if (dialog.isOK()) {
            Student updatedStudent = dialog.getStudent();
            tableModel.updateStudent(modelRow, updatedStudent);
        }
    }
    
    // 学生削除処理
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "削除する学生を選択してください", "情報", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        Student student = tableModel.getStudentAt(modelRow);
        
        int result = JOptionPane.showConfirmDialog(this,
                "学生「" + student.getName() + "」を削除しますか？",
                "削除確認", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeStudent(modelRow);
        }
    }
    
    // 検索フィルタリング
    private void filterStudents() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            // 名前、学年、メールアドレスで検索
            RowFilter<StudentTableModel, Object> filter = RowFilter.regexFilter(
                    "(?i)" + searchText, 1, 3, 4); // 大文字小文字を区別しない
            rowSorter.setRowFilter(filter);
        }
        updateStatus("検索結果: " + studentTable.getRowCount() + "件");
    }
    
    // データ保存
    private void saveToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // ヘッダー
                writer.println("学籍番号,氏名,年齢,学年,メールアドレス,入学日");
                
                // データ
                for (Student student : students) {
                    writer.println(student.toCSV());
                }
                
                updateStatus("データを保存しました: " + file.getName());
                JOptionPane.showMessageDialog(this, "データを保存しました", "保存完了", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存に失敗しました: " + e.getMessage(), 
                        "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // データ読込
    private void loadFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                students.clear();
                
                String line = reader.readLine(); // ヘッダーをスキップ
                if (line == null || !line.startsWith("学籍番号")) {
                    throw new IOException("無効なCSVファイル形式です");
                }
                
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        try {
                            Student student = Student.fromCSV(line);
                            students.add(student);
                        } catch (Exception e) {
                            System.err.println("無効な行をスキップしました: " + line);
                        }
                    }
                }
                
                tableModel.fireTableDataChanged();
                updateStatus("データを読み込みました: " + students.size() + "件");
                JOptionPane.showMessageDialog(this, students.size() + "件のデータを読み込みました", 
                        "読込完了", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "読込に失敗しました: " + e.getMessage(), 
                        "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // サンプルデータの読込
    private void loadSampleData() {
        students.add(new Student("S2024001", "田中太郎", 20, "2年", "tanaka@example.com", LocalDate.of(2023, 4, 1)));
        students.add(new Student("S2024002", "佐藤花子", 19, "1年", "sato@example.com", LocalDate.of(2024, 4, 1)));
        students.add(new Student("S2023001", "鈴木一郎", 21, "3年", "suzuki@example.com", LocalDate.of(2022, 4, 1)));
        students.add(new Student("S2023002", "山田美咲", 20, "2年", "yamada@example.com", LocalDate.of(2023, 4, 1)));
        tableModel.fireTableDataChanged();
        updateStatus("サンプルデータを読み込みました");
    }
    
    // 全データクリア
    private void clearAllStudents() {
        int result = JOptionPane.showConfirmDialog(this,
                "すべてのデータを削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            students.clear();
            tableModel.fireTableDataChanged();
            updateStatus("データをクリアしました");
        }
    }
    
    // ステータス更新
    private void updateStatus(String message) {
        statusLabel.setText(message + " [" + new java.util.Date() + "]");
    }
    
    // 学生入力ダイアログクラス
    private static class StudentDialog extends JDialog {
        private JTextField idField, nameField, ageField, gradeField, emailField, dateField;
        private boolean okPressed = false;
        private Student student;
        
        public StudentDialog(JFrame parent, String title, Student student) {
            super(parent, title, true);
            this.student = student;
            initializeDialog();
            if (student != null) {
                populateFields(student);
            }
        }
        
        private void initializeDialog() {
            setLayout(new BorderLayout());
            
            // フォームパネル
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // フィールドの作成
            idField = new JTextField(15);
            nameField = new JTextField(15);
            ageField = new JTextField(15);
            gradeField = new JTextField(15);
            emailField = new JTextField(15);
            dateField = new JTextField(15);
            
            // ラベルとフィールドの配置
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("学籍番号:"), gbc);
            gbc.gridx = 1;
            formPanel.add(idField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("氏名:"), gbc);
            gbc.gridx = 1;
            formPanel.add(nameField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("年齢:"), gbc);
            gbc.gridx = 1;
            formPanel.add(ageField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("学年:"), gbc);
            gbc.gridx = 1;
            formPanel.add(gradeField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("メールアドレス:"), gbc);
            gbc.gridx = 1;
            formPanel.add(emailField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 5;
            formPanel.add(new JLabel("入学日 (yyyy-mm-dd):"), gbc);
            gbc.gridx = 1;
            formPanel.add(dateField, gbc);
            
            // ボタンパネル
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("キャンセル");
            
            okButton.addActionListener(e -> {
                if (validateAndSave()) {
                    okPressed = true;
                    dispose();
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            
            setSize(350, 250);
            setLocationRelativeTo(getParent());
        }
        
        private void populateFields(Student student) {
            idField.setText(student.getId());
            nameField.setText(student.getName());
            ageField.setText(String.valueOf(student.getAge()));
            gradeField.setText(student.getGrade());
            emailField.setText(student.getEmail());
            dateField.setText(student.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        private boolean validateAndSave() {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String grade = gradeField.getText().trim();
                String email = emailField.getText().trim();
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                
                if (id.isEmpty() || name.isEmpty() || grade.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "すべてのフィールドを入力してください", 
                            "入力エラー", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                if (age < 0 || age > 150) {
                    JOptionPane.showMessageDialog(this, "年齢は0から150の間で入力してください", 
                            "入力エラー", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                student = new Student(id, name, age, grade, email, date);
                return true;
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "年齢は数値で入力してください", 
                        "入力エラー", JOptionPane.ERROR_MESSAGE);
                return false;
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "日付はyyyy-mm-dd形式で入力してください", 
                        "入力エラー", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        public boolean isOK() {
            return okPressed;
        }
        
        public Student getStudent() {
            return student;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new StudentManagementGUI().setVisible(true);
        });
    }
}

/*
 * 解答例の特徴:
 * 
 * 1. MVCパターンの実装
 *    - Model: Student クラス、StudentTableModel
 *    - View: JTable、ダイアログ、各種GUI コンポーネント
 *    - Controller: イベントハンドラメソッド
 * 
 * 2. オブザーバーパターンの活用
 *    - TableModel の変更通知
 *    - リアルタイム検索での DocumentListener
 *    - テーブル選択での ListSelectionListener
 * 
 * 3. 堅牢なエラーハンドリング
 *    - データバリデーション
 *    - ファイル I/O のエラー処理
 *    - ユーザー入力の検証
 * 
 * 4. ユーザビリティの配慮
 *    - リアルタイム検索
 *    - ソート・フィルタリング
 *    - 適切な UI フィードバック
 *    - キーボードショートカット
 * 
 * 5. データ永続化の実装
 *    - CSV ファイルでの保存・読込
 *    - ファイルダイアログの活用
 *    - データ形式の検証
 * 
 * 発展的な機能:
 * - データベース連携
 * - 印刷機能
 * - エクスポート機能
 * - 統計情報表示
 * - 設定の永続化
 */