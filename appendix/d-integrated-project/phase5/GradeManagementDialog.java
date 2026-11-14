// GradeManagementDialog.java - 成績管理ダイアログ
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Map;

public class GradeManagementDialog extends JDialog {
    private StudentService service;
    private String studentId;
    private DefaultTableModel gradeTableModel;
    private JLabel gpaLabel;
    private JTable gradeTable;
    
    // 呼び出し側で、studentIdの学生がいることを確かめてから作る
    public GradeManagementDialog(Frame parent, StudentService service, String studentId) {
        super(parent, "成績管理", true);
        this.service = service;
        this.studentId = studentId;
        initializeUI();
        loadGrades();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 学生情報パネル（学生が見つからなくても、ラベルは必ず作る）
        JPanel infoPanel = new JPanel();
        JLabel nameLabel = new JLabel("学生: (" + studentId + ")");
        gpaLabel = new JLabel();
        try {
            GradeableStudent student = service.getStudent(studentId);
            nameLabel.setText("学生: " + student.getName() + " (" + studentId + ")");
        } catch (StudentNotFoundException e) {
            nameLabel.setText(e.getMessage());
        }
        infoPanel.add(nameLabel);
        infoPanel.add(gpaLabel);
        add(infoPanel, BorderLayout.NORTH);
        
        // 成績テーブル
        String[] columns = {"科目名", "成績"};
        gradeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradeTable = new JTable(gradeTableModel);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                if (course.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "科目名を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double grade = Double.parseDouble(gradeField.getText().trim());
                
                service.updateGrade(studentId, course, grade);
                loadGrades();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "成績は数値で入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            } catch (StudentNotFoundException | InvalidGradeException e) {
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedGrade() {
        int row = gradeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "削除する成績を選択してください", "エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseName = (String) gradeTableModel.getValueAt(row, 0);
        
        int result = JOptionPane.showConfirmDialog(this, 
            courseName + " の成績を削除しますか？", 
            "確認", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                service.removeGrade(studentId, courseName);
                loadGrades();
            } catch (StudentNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 成績の一覧とGPAを表示し直す
    private void loadGrades() {
        gradeTableModel.setRowCount(0);
        try {
            GradeableStudent student = service.getStudent(studentId);
            Map<String, Double> grades = student.getGrades();
            
            for (Map.Entry<String, Double> entry : grades.entrySet()) {
                Object[] row = {entry.getKey(), String.format("%.1f", entry.getValue())};
                gradeTableModel.addRow(row);
            }
            gpaLabel.setText("GPA: " + String.format("%.2f", student.getGpa()));
        } catch (StudentNotFoundException e) {
            gpaLabel.setText("GPA: -");
        }
    }
}
