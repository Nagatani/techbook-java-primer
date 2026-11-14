// StudentManagementGUI.java - メインウィンドウ（データベース対応版）
// データベースへの保存に失敗すると、StudentServiceがIllegalStateExceptionを投げるので、
// 操作ごとに受け止めてメッセージを出す
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StudentManagementGUI extends JFrame {
    private static final String ALL_DEPARTMENTS = "すべて";
    
    private StudentService service;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private boolean updatingDepartmentFilter = false; // 選択肢を作り直している間はイベントを無視する
    
    public StudentManagementGUI(StudentService service) {
        this.service = service;
        initializeUI();
        
        // サンプルデータの追加
        addSampleData();
        refreshTable();
    }
    
    private void initializeUI() {
        setTitle("学生管理システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ウィンドウを閉じるとき（終了する前）にデータベースの接続を閉じる
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                service.shutdown();
            }
        });
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
        // ウィンドウを閉じたときと同じ処理にする
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        
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
        
        panel.add(new JLabel("検索（氏名・学生ID）:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> applyFilters());
        panel.add(searchField);
        
        panel.add(new JLabel("学部:"));
        departmentFilter = new JComboBox<>(new String[]{ALL_DEPARTMENTS});
        departmentFilter.addActionListener(e -> {
            if (!updatingDepartmentFilter) {
                applyFilters();
            }
        });
        panel.add(departmentFilter);
        
        JButton searchButton = new JButton("検索");
        searchButton.addActionListener(e -> applyFilters());
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
                        showGradeDialog(studentId);
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
        JButton gradeButton = new JButton("成績管理");
        JButton deleteButton = new JButton("削除");
        JButton refreshButton = new JButton("表示を更新");
        
        addButton.addActionListener(e -> showAddStudentDialog());
        gradeButton.addActionListener(e -> manageGradesOfSelectedStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        refreshButton.addActionListener(e -> refreshTable());
        
        panel.add(addButton);
        panel.add(gradeButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "学生追加", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 入力フィールド（学部は一覧から選ぶか、新しい名前を入力する）
        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(15);
        JComboBox<String> deptCombo = new JComboBox<>(departmentChoices().toArray(new String[0]));
        deptCombo.setEditable(true);
        
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
                Object selectedDept = deptCombo.getSelectedItem();
                String dept = selectedDept == null ? "" : selectedDept.toString().trim();
                
                if (id.isEmpty() || name.isEmpty() || dept.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "学生ID・氏名・学部は必須です", "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int age = Integer.parseInt(ageField.getText().trim());
                if (age <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "年齢は1以上の数値で入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GradeableStudent student = new GradeableStudent(id, name, age, dept);
                service.registerStudent(student);
                refreshTable();
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "年齢は数値で入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            } catch (DuplicateStudentException | IllegalStateException ex) {
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
    
    // 成績管理ダイアログを開く（モーダルなので、閉じるまでここで待つ）
    private void showGradeDialog(String studentId) {
        try {
            service.getStudent(studentId); // 存在を確かめる
            GradeManagementDialog dialog = new GradeManagementDialog(this, service, studentId);
            dialog.setVisible(true);
            refreshTable(); // ダイアログが閉じられた後、テーブルを更新（GPAが変わるため）
        } catch (StudentNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void manageGradesOfSelectedStudent() {
        String studentId = selectedStudentId();
        if (studentId != null) {
            showGradeDialog(studentId);
        }
    }
    
    private void deleteSelectedStudent() {
        String studentId = selectedStudentId();
        if (studentId == null) {
            return;
        }
        String studentName = (String) tableModel.getValueAt(studentTable.getSelectedRow(), 1);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "学生 " + studentName + " を削除しますか？", 
            "確認", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            deleteStudent(studentId);
        }
    }
    
    // 学生を削除して表示を更新する
    private void deleteStudent(String studentId) {
        try {
            service.deleteStudent(studentId);
            refreshTable();
        } catch (StudentNotFoundException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 選択している行の学生IDを返す。選んでいなければメッセージを出してnullを返す
    private String selectedStudentId() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "学生を選択してください", "エラー", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }
    
    // 検索文字列と学部の両方の条件で、表に出す学生を絞り込む
    private void applyFilters() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedDept = (String) departmentFilter.getSelectedItem();
        boolean allDepartments = selectedDept == null || ALL_DEPARTMENTS.equals(selectedDept);
        
        tableModel.setRowCount(0);
        for (GradeableStudent student : service.getAllStudents()) {
            boolean textMatches = searchText.isEmpty()
                || student.getName().toLowerCase().contains(searchText)
                || student.getId().toLowerCase().contains(searchText);
            boolean deptMatches = allDepartments || student.getDepartment().equals(selectedDept);
            if (textMatches && deptMatches) {
                addStudentToTable(student);
            }
        }
    }
    
    // 学部の選択肢を、登録されている学生の学部から作り直してから、表を表示し直す
    private void refreshTable() {
        String selected = (String) departmentFilter.getSelectedItem();
        updatingDepartmentFilter = true;
        departmentFilter.removeAllItems();
        departmentFilter.addItem(ALL_DEPARTMENTS);
        for (String dept : departmentChoices()) {
            departmentFilter.addItem(dept);
        }
        departmentFilter.setSelectedItem(selected); // 選択肢にないときは変わらない
        if (departmentFilter.getSelectedIndex() < 0) {
            departmentFilter.setSelectedIndex(0);
        }
        updatingDepartmentFilter = false;
        
        applyFilters();
    }
    
    // 学部の一覧（既定の3学部と、登録されている学生の学部）
    private Set<String> departmentChoices() {
        Set<String> departments = new TreeSet<>(List.of("工学部", "理学部", "文学部"));
        for (GradeableStudent student : service.getAllStudents()) {
            departments.add(student.getDepartment());
        }
        return departments;
    }
    
    private void addStudentToTable(GradeableStudent student) {
        Object[] row = {
            student.getId(),
            student.getName(),
            student.getAge(),
            student.getDepartment(),
            String.format("%.2f", student.getGpa())
        };
        tableModel.addRow(row);
    }
    
    private void importStudents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            
            @Override
            public String getDescription() {
                return "CSVファイル (*.csv、UTF-8)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                StudentService.ImportResult result =
                    service.importStudentsFromCSV(fileChooser.getSelectedFile().getAbsolutePath());
                refreshTable();
                // （データベースへの保存に失敗するとIllegalStateExceptionで中断する）
                String message = "インポートが完了しました\n成功: " + result.successCount() + "件";
                if (result.errorCount() > 0) {
                    message += "\nエラー: " + result.errorCount() + "件（詳細はコンソールのログ）";
                }
                JOptionPane.showMessageDialog(this, message, "情報", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | IllegalStateException e) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "インポートエラー: " + e.getMessage(), 
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportStudents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("students_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                service.exportToCSV(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "エクスポートが完了しました", "情報", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "エクスポートエラー: " + e.getMessage(), 
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, 
            "学生管理システム v1.0\n\nJavaプログラミング統合プロジェクト", 
            "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // 学生が1人もいないときだけ、サンプルデータを登録する
    private void addSampleData() {
        if (!service.getAllStudents().isEmpty()) {
            return;
        }
        GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        student1.addGrade("数学", 3.5);
        student1.addGrade("物理", 4.0);
        
        GradeableStudent student2 = new GradeableStudent("S002", "山田花子", 21, "理学部");
        student2.addGrade("化学", 3.8);
        student2.addGrade("生物", 3.5);
        
        GradeableStudent student3 = new GradeableStudent("S003", "佐藤次郎", 22, "文学部");
        student3.addGrade("英文学", 3.2);
        
        for (GradeableStudent student : List.of(student1, student2, student3)) {
            try {
                service.registerStudent(student);
            } catch (DuplicateStudentException | IllegalStateException e) {
                // 空の状態から登録するので重複はない。保存の失敗はログに記録済み
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentService service;
            try {
                service = new StudentService(); // student_management.dbに接続する
            } catch (SQLException e) {
                // SQLite JDBCドライバーがクラスパスにないときなど
                JOptionPane.showMessageDialog(null, 
                    "データベースに接続できません: " + e.getMessage(), 
                    "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            StudentManagementGUI gui = new StudentManagementGUI(service);
            gui.setVisible(true);
        });
    }
}
