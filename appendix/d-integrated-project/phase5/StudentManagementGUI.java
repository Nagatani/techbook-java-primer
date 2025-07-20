// StudentManagementGUI.java - メインウィンドウ
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudentManagementGUI extends JFrame {
    private StudentService service;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    
    public StudentManagementGUI() {
        service = new StudentService();
        initializeUI();
        
        // サンプルデータの追加
        addSampleData();
        refreshTable();
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
    
    private void showStudentDetails(String studentId) {
        try {
            GradeableStudent student = service.getStudent(studentId);
            GradeManagementDialog dialog = new GradeManagementDialog(this, service, studentId);
            dialog.setVisible(true);
            refreshTable(); // ダイアログが閉じられた後、テーブルを更新
        } catch (StudentNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "学生を選択してください", "エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(row, 0);
        showStudentDetails(studentId);
    }
    
    private void deleteSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "学生を選択してください", "エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(row, 0);
        String studentName = (String) tableModel.getValueAt(row, 1);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "学生 " + studentName + " を削除しますか？", 
            "確認", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Note: 実際の削除機能はStudentServiceに実装が必要
            JOptionPane.showMessageDialog(this, "削除機能は未実装です", "情報", JOptionPane.INFORMATION_MESSAGE);
            // refreshTable();
        }
    }
    
    private void searchStudents() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }
        
        tableModel.setRowCount(0);
        for (GradeableStudent student : service.getAllStudents()) {
            if (student.getName().toLowerCase().contains(searchText) ||
                student.getId().toLowerCase().contains(searchText)) {
                addStudentToTable(student);
            }
        }
    }
    
    private void filterByDepartment() {
        String selected = (String) departmentFilter.getSelectedItem();
        if ("すべて".equals(selected)) {
            refreshTable();
            return;
        }
        
        tableModel.setRowCount(0);
        for (GradeableStudent student : service.getAllStudents()) {
            if (student.getDepartment().equals(selected)) {
                addStudentToTable(student);
            }
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<GradeableStudent> students = service.getAllStudents();
        
        for (GradeableStudent student : students) {
            addStudentToTable(student);
        }
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
                return "CSVファイル (*.csv)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            service.importStudentsFromCSV(fileChooser.getSelectedFile().getAbsolutePath());
            refreshTable();
            JOptionPane.showMessageDialog(this, "インポートが完了しました", "情報", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void exportStudents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("students_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                service.exportToCSV(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "エクスポートが完了しました", "情報", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
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
    
    private void addSampleData() {
        try {
            GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
            student1.addGrade("数学", 3.5);
            student1.addGrade("物理", 4.0);
            service.registerStudent(student1);
            
            GradeableStudent student2 = new GradeableStudent("S002", "山田花子", 21, "理学部");
            student2.addGrade("化学", 3.8);
            student2.addGrade("生物", 3.5);
            service.registerStudent(student2);
            
            GradeableStudent student3 = new GradeableStudent("S003", "佐藤次郎", 22, "文学部");
            student3.addGrade("英文学", 3.2);
            service.registerStudent(student3);
            
        } catch (DuplicateStudentException e) {
            // サンプルデータなので無視
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementGUI gui = new StudentManagementGUI();
            gui.setVisible(true);
        });
    }
}