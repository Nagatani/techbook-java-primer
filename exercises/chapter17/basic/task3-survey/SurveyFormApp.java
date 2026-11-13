import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * 授業アンケートの入力画面（基礎課題3の解答例）
 *
 * 入力系・選択系のコンポーネントを、JPanelの入れ子で並べます。
 * 送信したときの処理は、第18章で学ぶイベント処理で付けます。
 */
public class SurveyFormApp extends JFrame {

    public SurveyFormApp() {
        super("授業アンケート");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 名前: 1行の文字の入力
        JTextField nameField = new JTextField(15);

        // 年齢: 数値だけを選ばせる（初期値20、0〜120、1ずつ増減）
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 120, 1));

        // 学年: 決まった候補から1つ選ぶ
        JComboBox<String> gradeBox = new JComboBox<>(new String[] {"1年", "2年", "3年", "4年"});
        gradeBox.setSelectedIndex(1);

        // 好きな言語: ButtonGroupにまとめたラジオボタンは1つだけ選べる
        JRadioButton javaButton = new JRadioButton("Java", true);
        JRadioButton cButton = new JRadioButton("C");
        JRadioButton pythonButton = new JRadioButton("Python");
        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(javaButton);
        languageGroup.add(cButton);
        languageGroup.add(pythonButton);
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        languagePanel.add(javaButton);
        languagePanel.add(cButton);
        languagePanel.add(pythonButton);

        // 使ったことのあるエディター: チェックボックスはいくつでも選べる
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolPanel.add(new JCheckBox("VS Code", true));
        toolPanel.add(new JCheckBox("IntelliJ IDEA"));
        toolPanel.add(new JCheckBox("Eclipse"));

        // 満足度: 1〜5のスライダー。目盛りと数字を表示する
        JSlider satisfactionSlider = new JSlider(1, 5, 4);
        satisfactionSlider.setMajorTickSpacing(1);
        satisfactionSlider.setPaintTicks(true);
        satisfactionSlider.setPaintLabels(true);
        satisfactionSlider.setSnapToTicks(true);

        // 項目名と入力部品を2列の格子に並べる。行の数は0（必要なだけ）にしておく
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 6));
        formPanel.add(new JLabel("名前"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("年齢"));
        formPanel.add(ageSpinner);
        formPanel.add(new JLabel("学年"));
        formPanel.add(gradeBox);
        formPanel.add(new JLabel("好きな言語"));
        formPanel.add(languagePanel);
        formPanel.add(new JLabel("使ったことのあるエディター"));
        formPanel.add(toolPanel);
        formPanel.add(new JLabel("満足度"));
        formPanel.add(satisfactionSlider);

        // 感想: 複数行の入力。JScrollPaneに入れると、長くなったときにスクロールできる
        JTextArea commentArea = new JTextArea(4, 30);
        commentArea.setLineWrap(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBorder(BorderFactory.createTitledBorder("感想"));

        // ボタンは右寄せのFlowLayoutに置く
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("送信"));

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(formPanel, BorderLayout.NORTH);
        content.add(commentScroll, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(content);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SurveyFormApp().setVisible(true));
    }
}
