import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * プロフィールカード（基礎課題1の解答例）
 *
 * BorderLayoutの5つの領域に、ラベルとボタンを置きます。
 * ボタンを押したときの処理は、第18章で学ぶイベント処理で付けます。
 */
public class ProfileCardApp extends JFrame {

    public ProfileCardApp() {
        super("プロフィール");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 上（NORTH）: 見出し。左右いっぱいに広がるので、文字を中央に寄せる
        JLabel titleLabel = new JLabel("自己紹介", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // 左（WEST）: アイコンの代わりに、色を塗ったラベルに頭文字を出す
        // JLabelは背景を塗らないのが既定なので、setOpaque(true)で塗らせる
        JLabel iconLabel = new JLabel("YT", SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(70, 130, 180));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(iconLabel.getFont().deriveFont(Font.BOLD, 28f));
        iconLabel.setPreferredSize(new Dimension(90, 90));

        // 中央（CENTER）: HTMLの表で項目をそろえて並べる
        JLabel detailLabel = new JLabel("""
                <html><table>
                <tr><td><b>名前</b></td><td>山田 太郎</td></tr>
                <tr><td><b>所属</b></td><td>情報工学科 2年</td></tr>
                <tr><td><b>得意な言語</b></td><td>C、Java</td></tr>
                <tr><td><b>趣味</b></td><td>写真、自転車</td></tr>
                </table></html>
                """);
        detailLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        // 下（SOUTH）: ボタンを並べたパネル。FlowLayoutはボタンを適切な大きさで中央に並べる
        JButton messageButton = new JButton("メッセージを送る");
        JButton followButton = new JButton("フォロー中");
        followButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(messageButton);
        buttonPanel.add(followButton);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(iconLabel, BorderLayout.WEST);
        content.add(detailLabel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(content);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileCardApp().setVisible(true));
    }
}
