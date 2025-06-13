# 第12章 GUIアプリケーション

この章では、Swingを使用したGUIアプリケーションの開発方法を学習します。C言語のコンソールアプリケーションとは異なる、視覚的なユーザーインターフェースを持つアプリケーションの作成方法を習得しましょう。

## 12.1 GUIプログラミングの基礎

### コンソール vs GUI

```c
// C言語のコンソールアプリケーション
#include <stdio.h>

int main() {
    int a, b;
    printf("数値を2つ入力してください: ");
    scanf("%d %d", &a, &b);
    printf("合計: %d\n", a + b);
    return 0;
}
```

```java
// JavaのGUIアプリケーション
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleCalculator extends JFrame {
    private JTextField field1, field2, result;
    
    public SimpleCalculator() {
        setTitle("簡単な電卓");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));
        
        add(new JLabel("数値1:"));
        field1 = new JTextField();
        add(field1);
        
        add(new JLabel("数値2:"));
        field2 = new JTextField();
        add(field2);
        
        JButton button = new JButton("計算");
        button.addActionListener(e -> calculate());
        add(button);
        
        result = new JTextField();
        result.setEditable(false);
        add(result);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void calculate() {
        try {
            int a = Integer.parseInt(field1.getText());
            int b = Integer.parseInt(field2.getText());
            result.setText(String.valueOf(a + b));
        } catch (NumberFormatException e) {
            result.setText("エラー");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleCalculator().setVisible(true);
        });
    }
}
```

## 12.2 Swingの基本コンポーネント

### JFrame - メインウィンドウ

```java
import javax.swing.*;

public class BasicFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("基本ウィンドウ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);  // 画面中央に配置
            frame.setVisible(true);
        });
    }
}
```

### 基本コンポーネント

```java
import javax.swing.*;
import java.awt.*;

public class BasicComponents extends JFrame {
    public BasicComponents() {
        setTitle("基本コンポーネント");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        // ラベル
        add(new JLabel("ラベル"));
        
        // テキストフィールド
        JTextField textField = new JTextField("テキストフィールド", 15);
        add(textField);
        
        // ボタン
        JButton button = new JButton("ボタン");
        add(button);
        
        // チェックボックス
        JCheckBox checkBox = new JCheckBox("チェックボックス");
        add(checkBox);
        
        // ラジオボタン
        JRadioButton radio1 = new JRadioButton("選択肢1");
        JRadioButton radio2 = new JRadioButton("選択肢2");
        ButtonGroup group = new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        add(radio1);
        add(radio2);
        
        // コンボボックス
        String[] items = {"項目1", "項目2", "項目3"};
        JComboBox<String> comboBox = new JComboBox<>(items);
        add(comboBox);
        
        // テキストエリア
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setBorder(BorderFactory.createTitledBorder("テキストエリア"));
        add(new JScrollPane(textArea));
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicComponents().setVisible(true);
        });
    }
}
```

## 12.3 レイアウトマネージャー

### BorderLayout

```java
import javax.swing.*;
import java.awt.*;

public class BorderLayoutExample extends JFrame {
    public BorderLayoutExample() {
        setTitle("BorderLayout例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        add(new JButton("北"), BorderLayout.NORTH);
        add(new JButton("南"), BorderLayout.SOUTH);
        add(new JButton("東"), BorderLayout.EAST);
        add(new JButton("西"), BorderLayout.WEST);
        add(new JButton("中央"), BorderLayout.CENTER);
        
        setSize(400, 300);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BorderLayoutExample().setVisible(true);
        });
    }
}
```

### GridLayout

```java
import javax.swing.*;
import java.awt.*;

public class GridLayoutExample extends JFrame {
    public GridLayoutExample() {
        setTitle("GridLayout例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));
        
        for (int i = 1; i <= 9; i++) {
            add(new JButton("ボタン" + i));
        }
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GridLayoutExample().setVisible(true);
        });
    }
}
```

### FlowLayout

```java
import javax.swing.*;
import java.awt.*;

public class FlowLayoutExample extends JFrame {
    public FlowLayoutExample() {
        setTitle("FlowLayout例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        add(new JButton("ボタン1"));
        add(new JButton("長いボタン2"));
        add(new JButton("ボタン3"));
        add(new JButton("とても長いボタン4"));
        add(new JButton("ボタン5"));
        
        setSize(300, 150);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FlowLayoutExample().setVisible(true);
        });
    }
}
```

## 12.4 イベント処理

### ActionListener

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EventExample extends JFrame implements ActionListener {
    private JTextField textField;
    private JLabel label;
    private int counter = 0;
    
    public EventExample() {
        setTitle("イベント処理例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        textField = new JTextField(20);
        add(textField);
        
        JButton button1 = new JButton("表示");
        button1.addActionListener(this);
        add(button1);
        
        // ラムダ式を使用
        JButton button2 = new JButton("カウント");
        button2.addActionListener(e -> {
            counter++;
            label.setText("カウント: " + counter);
        });
        add(button2);
        
        // 匿名クラス
        JButton button3 = new JButton("クリア");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setText("");
                label.setText("クリアしました");
            }
        });
        add(button3);
        
        label = new JLabel("ここに結果が表示されます");
        add(label);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String text = textField.getText();
        label.setText("入力: " + text);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EventExample().setVisible(true);
        });
    }
}
```

### その他のイベント

```java
import javax.swing.*;
import java.awt.event.*;

public class VariousEvents extends JFrame {
    public VariousEvents() {
        setTitle("様々なイベント");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTextField textField = new JTextField(20);
        
        // キーイベント
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Enterが押されました");
                }
            }
        });
        
        // マウスイベント
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    textField.selectAll();
                }
            }
        });
        
        // フォーカスイベント
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBackground(java.awt.Color.YELLOW);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                textField.setBackground(java.awt.Color.WHITE);
            }
        });
        
        add(textField);
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VariousEvents().setVisible(true);
        });
    }
}
```

## 12.5 実践的なアプリケーション

### シンプルなテキストエディタ

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SimpleTextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    
    public SimpleTextEditor() {
        setTitle("シンプルテキストエディタ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createMenuBar();
        createTextArea();
        createToolBar();
        
        fileChooser = new JFileChooser();
        
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem newItem = new JMenuItem("新規");
        JMenuItem openItem = new JMenuItem("開く");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("終了");
        
        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("編集");
        JMenuItem cutItem = new JMenuItem("切り取り");
        JMenuItem copyItem = new JMenuItem("コピー");
        JMenuItem pasteItem = new JMenuItem("貼り付け");
        
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createTextArea() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        
        JButton newButton = new JButton("新規");
        JButton openButton = new JButton("開く");
        JButton saveButton = new JButton("保存");
        
        newButton.addActionListener(e -> newFile());
        openButton.addActionListener(e -> openFile());
        saveButton.addActionListener(e -> saveFile());
        
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void newFile() {
        textArea.setText("");
        setTitle("シンプルテキストエディタ - 新規文書");
    }
    
    private void openFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                textArea.read(reader, null);
                reader.close();
                setTitle("シンプルテキストエディタ - " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルを開けませんでした: " + e.getMessage());
            }
        }
    }
    
    private void saveFile() {
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                textArea.write(writer);
                writer.close();
                setTitle("シンプルテキストエディタ - " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "ファイルを保存できませんでした: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleTextEditor().setVisible(true);
        });
    }
}
```

### 電卓アプリケーション

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private double num1, num2, result;
    private String operator;
    private boolean operatorPressed;
    
    public Calculator() {
        setTitle("電卓");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createDisplay();
        createButtons();
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void createDisplay() {
        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 20));
        display.setPreferredSize(new Dimension(250, 50));
        add(display, BorderLayout.NORTH);
    }
    
    private void createButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        
        String[] buttons = {
            "C", "±", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "="
        };
        
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.addActionListener(this);
            
            if (text.equals("0")) {
                // 0ボタンは2列分
                buttonPanel.add(button);
                buttonPanel.add(new JLabel()); // 空のスペース
            } else {
                buttonPanel.add(button);
            }
        }
        
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        try {
            if (command.matches("[0-9]")) {
                handleNumber(command);
            } else if (command.equals(".")) {
                handleDecimal();
            } else if (command.matches("[+\\-×÷]")) {
                handleOperator(command);
            } else if (command.equals("=")) {
                handleEquals();
            } else if (command.equals("C")) {
                handleClear();
            } else if (command.equals("±")) {
                handlePlusMinus();
            } else if (command.equals("%")) {
                handlePercent();
            }
        } catch (Exception ex) {
            display.setText("エラー");
        }
    }
    
    private void handleNumber(String number) {
        if (operatorPressed) {
            display.setText(number);
            operatorPressed = false;
        } else {
            String current = display.getText();
            if (current.equals("0")) {
                display.setText(number);
            } else {
                display.setText(current + number);
            }
        }
    }
    
    private void handleDecimal() {
        String current = display.getText();
        if (!current.contains(".")) {
            display.setText(current + ".");
        }
    }
    
    private void handleOperator(String op) {
        num1 = Double.parseDouble(display.getText());
        operator = op;
        operatorPressed = true;
    }
    
    private void handleEquals() {
        num2 = Double.parseDouble(display.getText());
        
        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "×":
                result = num1 * num2;
                break;
            case "÷":
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    display.setText("エラー");
                    return;
                }
                break;
        }
        
        display.setText(String.valueOf(result));
        operatorPressed = true;
    }
    
    private void handleClear() {
        display.setText("0");
        num1 = num2 = result = 0;
        operator = "";
        operatorPressed = false;
    }
    
    private void handlePlusMinus() {
        double current = Double.parseDouble(display.getText());
        display.setText(String.valueOf(-current));
    }
    
    private void handlePercent() {
        double current = Double.parseDouble(display.getText());
        display.setText(String.valueOf(current / 100));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Calculator().setVisible(true);
        });
    }
}
```

## 12.6 MVCパターンの実装

### シンプルなTODOアプリケーション

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// Model
class TodoModel {
    private List<String> todos = new ArrayList<>();
    private List<TodoView> observers = new ArrayList<>();
    
    public void addTodo(String todo) {
        todos.add(todo);
        notifyObservers();
    }
    
    public void removeTodo(int index) {
        if (index >= 0 && index < todos.size()) {
            todos.remove(index);
            notifyObservers();
        }
    }
    
    public List<String> getTodos() {
        return new ArrayList<>(todos);
    }
    
    public void addObserver(TodoView observer) {
        observers.add(observer);
    }
    
    private void notifyObservers() {
        for (TodoView observer : observers) {
            observer.updateView();
        }
    }
}

// View
class TodoView extends JFrame {
    private TodoModel model;
    private TodoController controller;
    private JTextField inputField;
    private DefaultListModel<String> listModel;
    private JList<String> todoList;
    
    public TodoView(TodoModel model) {
        this.model = model;
        this.controller = new TodoController(model);
        model.addObserver(this);
        
        initializeComponents();
        updateView();
    }
    
    private void initializeComponents() {
        setTitle("TODOアプリ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton addButton = new JButton("追加");
        
        addButton.addActionListener(e -> {
            String todo = inputField.getText().trim();
            if (!todo.isEmpty()) {
                controller.addTodo(todo);
                inputField.setText("");
            }
        });
        
        inputField.addActionListener(e -> addButton.doClick());
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        
        // リストパネル
        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(todoList);
        
        JButton removeButton = new JButton("削除");
        removeButton.addActionListener(e -> {
            int index = todoList.getSelectedIndex();
            if (index != -1) {
                controller.removeTodo(index);
            }
        });
        
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(removeButton, BorderLayout.SOUTH);
        
        setSize(400, 300);
        setLocationRelativeTo(null);
    }
    
    public void updateView() {
        listModel.clear();
        for (String todo : model.getTodos()) {
            listModel.addElement(todo);
        }
    }
}

// Controller
class TodoController {
    private TodoModel model;
    
    public TodoController(TodoModel model) {
        this.model = model;
    }
    
    public void addTodo(String todo) {
        model.addTodo(todo);
    }
    
    public void removeTodo(int index) {
        model.removeTodo(index);
    }
}

public class TodoApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoModel model = new TodoModel();
            new TodoView(model).setVisible(true);
        });
    }
}
```

## 12.7 練習問題

1. 四則演算ができる関数電卓を作成してください。

2. 簡単な画像ビューアを作成してください。

3. ノートアプリケーション（複数のメモを管理）を作成してください。

## まとめ

この章では、Swingを使用したGUIアプリケーションの開発方法を学習しました。コンポーネントの配置、イベント処理、MVCパターンの実装など、実用的なGUIアプリケーションを作成するための基礎知識を習得することができました。