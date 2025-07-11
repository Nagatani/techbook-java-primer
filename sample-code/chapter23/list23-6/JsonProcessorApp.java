/**
 * リスト23-6
 * JsonProcessorAppクラス
 * 
 * 元ファイル: chapter23-build-and-deploy.md (506行目)
 */

// JsonProcessorApp.java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class JsonProcessorApp {
    
    static class Person {
        private String name;
        private int age;
        private LocalDateTime created;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
            this.created = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public int getAge() { return age; }
        public Date getCreated() { return created; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JSON Processor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // 入力パネル
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            inputPanel.add(new JLabel("Name:"));
            JTextField nameField = new JTextField();
            inputPanel.add(nameField);
            
            inputPanel.add(new JLabel("Age:"));
            JTextField ageField = new JTextField();
            inputPanel.add(ageField);
            
            JButton convertButton = new JButton("Convert to JSON");
            inputPanel.add(convertButton);
            inputPanel.add(new JLabel()); // 空のセル
            
            // 出力エリア
            JTextArea outputArea = new JTextArea(10, 40);
            outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);
            
            // ボタンのアクション
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
                
            convertButton.addActionListener(e -> {
                try {
                    String name = nameField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    
                    Person person = new Person(name, age);
                    String json = gson.toJson(person);
                    
                    outputArea.setText(json);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "年齢は数値で入力してください", 
                        "入力エラー", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}