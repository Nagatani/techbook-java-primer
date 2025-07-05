/**
 * 第17章 課題1: 基本的な電卓アプリ - 解答例
 * 
 * SwingとAWTを使った基本的な電卓アプリケーション
 * 
 * 学習ポイント:
 * - JFrameとJPanelの基本的な使い方
 * - GridLayoutによるボタン配置
 * - ActionListenerによるイベント処理
 * - 電卓の計算ロジック実装
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class Calculator extends JFrame implements ActionListener {
    
    // 解答例 1: 基本的な実装
    
    private JTextField display;
    private JButton[] numberButtons = new JButton[10];
    private JButton[] operationButtons = new JButton[8];
    private JButton addButton, subtractButton, multiplyButton, divideButton;
    private JButton equalsButton, clearButton, deleteButton, decimalButton;
    
    private double firstNumber = 0;
    private double secondNumber = 0;
    private double result = 0;
    private char operator = ' ';
    private boolean operatorPressed = false;
    private boolean equalsPressed = false;
    
    private DecimalFormat formatter = new DecimalFormat("#.##########");
    
    public Calculator() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }
    
    /**
     * コンポーネントの初期化
     */
    private void initializeComponents() {
        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 30));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // 数字ボタンの作成
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
            numberButtons[i].setFocusable(false);
            numberButtons[i].setBackground(new Color(240, 240, 240));
        }
        
        // 演算子ボタンの作成
        addButton = new JButton("+");
        subtractButton = new JButton("-");
        multiplyButton = new JButton("*");
        divideButton = new JButton("/");
        equalsButton = new JButton("=");
        clearButton = new JButton("C");
        deleteButton = new JButton("Del");
        decimalButton = new JButton(".");
        
        operationButtons[0] = addButton;
        operationButtons[1] = subtractButton;
        operationButtons[2] = multiplyButton;
        operationButtons[3] = divideButton;
        operationButtons[4] = equalsButton;
        operationButtons[5] = clearButton;
        operationButtons[6] = deleteButton;
        operationButtons[7] = decimalButton;
        
        // 演算子ボタンのスタイル設定
        for (JButton button : operationButtons) {
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setFocusable(false);
        }
        
        // 演算子ボタンの色設定
        addButton.setBackground(new Color(255, 165, 0));
        subtractButton.setBackground(new Color(255, 165, 0));
        multiplyButton.setBackground(new Color(255, 165, 0));
        divideButton.setBackground(new Color(255, 165, 0));
        equalsButton.setBackground(new Color(255, 69, 0));
        clearButton.setBackground(new Color(255, 99, 99));
        deleteButton.setBackground(new Color(255, 99, 99));
        decimalButton.setBackground(new Color(240, 240, 240));
    }
    
    /**
     * レイアウトの設定
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 表示パネル
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        displayPanel.add(display);
        add(displayPanel, BorderLayout.NORTH);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // ボタンの配置
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JLabel()); // 空白
        buttonPanel.add(divideButton);
        
        buttonPanel.add(numberButtons[7]);
        buttonPanel.add(numberButtons[8]);
        buttonPanel.add(numberButtons[9]);
        buttonPanel.add(multiplyButton);
        
        buttonPanel.add(numberButtons[4]);
        buttonPanel.add(numberButtons[5]);
        buttonPanel.add(numberButtons[6]);
        buttonPanel.add(subtractButton);
        
        buttonPanel.add(numberButtons[1]);
        buttonPanel.add(numberButtons[2]);
        buttonPanel.add(numberButtons[3]);
        buttonPanel.add(addButton);
        
        buttonPanel.add(numberButtons[0]);
        buttonPanel.add(decimalButton);
        buttonPanel.add(new JLabel()); // 空白
        buttonPanel.add(equalsButton);
        
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    /**
     * イベントリスナーの設定
     */
    private void setupEventListeners() {
        for (JButton button : numberButtons) {
            button.addActionListener(this);
        }
        for (JButton button : operationButtons) {
            button.addActionListener(this);
        }
        
        // キーボードショートカット
        setupKeyboardShortcuts();
    }
    
    /**
     * フレームの設定
     */
    private void setupFrame() {
        setTitle("計算機");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // アイコンの設定（オプション）
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("calculator.png"));
        } catch (Exception e) {
            // アイコンファイルがない場合は無視
        }
    }
    
    /**
     * キーボードショートカットの設定
     */
    private void setupKeyboardShortcuts() {
        // 数字キー
        for (int i = 0; i < 10; i++) {
            final int digit = i;
            KeyStroke keyStroke = KeyStroke.getKeyStroke(String.valueOf(i));
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(keyStroke, "number" + i);
            getRootPane().getActionMap().put("number" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    numberPressed(String.valueOf(digit));
                }
            });
        }
        
        // 演算子キー
        setupOperatorShortcuts();
        
        // その他のキー
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "clear");
        getRootPane().getActionMap().put("clear", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("BACK_SPACE"), "delete");
        getRootPane().getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
    }
    
    /**
     * 演算子のショートカット設定
     */
    private void setupOperatorShortcuts() {
        // +
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("PLUS"), "add");
        getRootPane().getActionMap().put("add", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operatorPressed('+');
            }
        });
        
        // -
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("MINUS"), "subtract");
        getRootPane().getActionMap().put("subtract", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operatorPressed('-');
            }
        });
        
        // *
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ASTERISK"), "multiply");
        getRootPane().getActionMap().put("multiply", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operatorPressed('*');
            }
        });
        
        // /
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("SLASH"), "divide");
        getRootPane().getActionMap().put("divide", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operatorPressed('/');
            }
        });
        
        // =
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "equals");
        getRootPane().getActionMap().put("equals", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
        
        // .
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("PERIOD"), "decimal");
        getRootPane().getActionMap().put("decimal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decimalPressed();
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // 数字ボタンの処理
        for (int i = 0; i < 10; i++) {
            if (e.getSource() == numberButtons[i]) {
                numberPressed(String.valueOf(i));
                return;
            }
        }
        
        // 演算子ボタンの処理
        if (e.getSource() == addButton) {
            operatorPressed('+');
        } else if (e.getSource() == subtractButton) {
            operatorPressed('-');
        } else if (e.getSource() == multiplyButton) {
            operatorPressed('*');
        } else if (e.getSource() == divideButton) {
            operatorPressed('/');
        } else if (e.getSource() == equalsButton) {
            calculate();
        } else if (e.getSource() == clearButton) {
            clear();
        } else if (e.getSource() == deleteButton) {
            delete();
        } else if (e.getSource() == decimalButton) {
            decimalPressed();
        }
    }
    
    /**
     * 数字ボタンが押された時の処理
     */
    private void numberPressed(String number) {
        if (equalsPressed) {
            clear();
        }
        
        String currentText = display.getText();
        if (operatorPressed || currentText.equals("0")) {
            display.setText(number);
            operatorPressed = false;
        } else {
            // 最大桁数のチェック
            if (currentText.length() < 15) {
                display.setText(currentText + number);
            }
        }
        equalsPressed = false;
    }
    
    /**
     * 演算子ボタンが押された時の処理
     */
    private void operatorPressed(char op) {
        if (!operatorPressed && !equalsPressed) {
            if (operator != ' ') {
                calculate();
            } else {
                firstNumber = Double.parseDouble(display.getText());
            }
        }
        
        operator = op;
        operatorPressed = true;
        equalsPressed = false;
    }
    
    /**
     * 小数点ボタンが押された時の処理
     */
    private void decimalPressed() {
        if (equalsPressed) {
            clear();
        }
        
        String currentText = display.getText();
        if (operatorPressed) {
            display.setText("0.");
            operatorPressed = false;
        } else if (!currentText.contains(".")) {
            display.setText(currentText + ".");
        }
        equalsPressed = false;
    }
    
    /**
     * 計算処理
     */
    private void calculate() {
        if (operator == ' ' || operatorPressed) {
            return;
        }
        
        try {
            secondNumber = Double.parseDouble(display.getText());
            
            switch (operator) {
                case '+':
                    result = firstNumber + secondNumber;
                    break;
                case '-':
                    result = firstNumber - secondNumber;
                    break;
                case '*':
                    result = firstNumber * secondNumber;
                    break;
                case '/':
                    if (secondNumber == 0) {
                        showError("ゼロで割ることはできません");
                        return;
                    }
                    result = firstNumber / secondNumber;
                    break;
                default:
                    return;
            }
            
            // 結果の表示
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                showError("計算結果が不正です");
                return;
            }
            
            display.setText(formatter.format(result));
            firstNumber = result;
            operator = ' ';
            operatorPressed = false;
            equalsPressed = true;
            
        } catch (NumberFormatException e) {
            showError("無効な数値です");
        }
    }
    
    /**
     * クリア処理
     */
    private void clear() {
        display.setText("0");
        firstNumber = 0;
        secondNumber = 0;
        result = 0;
        operator = ' ';
        operatorPressed = false;
        equalsPressed = false;
    }
    
    /**
     * 削除処理（バックスペース）
     */
    private void delete() {
        String currentText = display.getText();
        if (currentText.length() > 1 && !operatorPressed && !equalsPressed) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            display.setText("0");
        }
    }
    
    /**
     * エラー表示
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
        clear();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                // デフォルトのLook&Feelを使用
            }
            new Calculator().setVisible(true);
        });
    }
}

/*
 * 解答例の特徴:
 * 
 * 1. 堅牢なエラーハンドリング
 *    - ゼロ除算のチェック
 *    - 無効な数値の処理
 *    - 計算結果の妥当性チェック
 * 
 * 2. ユーザビリティの向上
 *    - キーボードショートカット
 *    - 適切な色分け
 *    - 直感的な操作感
 * 
 * 3. 状態管理の実装
 *    - 演算子押下状態の管理
 *    - 等号押下後の処理
 *    - 連続計算への対応
 * 
 * 4. UI/UXの配慮
 *    - システムLook&Feelの適用
 *    - 適切なフォントサイズ
 *    - ボタンの視覚的区別
 * 
 * 拡張可能な設計:
 * - 履歴機能の追加
 * - 高度な数学関数の追加
 * - 設定の永続化
 * - カスタマイズ可能な外観
 */