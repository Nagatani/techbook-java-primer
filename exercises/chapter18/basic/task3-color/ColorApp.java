import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * 赤・緑・青のスライダーで色を作る（基礎課題3の解答例）
 * スライダーとスピナーは連動し、どちらを動かしても見本の色が変わる
 */
public class ColorApp extends JFrame {
    private static final String[] NAMES = {"赤", "緑", "青"};

    private final JSlider[] sliders = new JSlider[3];
    private final JSpinner[] spinners = new JSpinner[3];
    private final JPanel preview = new JPanel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");

    public ColorApp() {
        super("色の調合");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controls = new JPanel(new GridLayout(3, 3, 8, 4));
        int[] initial = {255, 128, 0};
        for (int i = 0; i < 3; i++) {
            JSlider slider = new JSlider(0, 255, initial[i]);
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(initial[i], 0, 255, 1));
            sliders[i] = slider;
            spinners[i] = spinner;
            // スライダーを動かしたら、スピナーと見本を合わせる
            slider.addChangeListener(e -> {
                spinner.setValue(slider.getValue());
                // ドラッグ中かどうかで表示を変える
                statusLabel.setText(slider.getValueIsAdjusting() ? "調整中..." : " ");
                updatePreview();
            });
            // スピナーを変えたら、スライダーを合わせる（スライダーの側で見本が変わる）
            spinner.addChangeListener(e -> slider.setValue((Integer) spinner.getValue()));
            controls.add(new JLabel(NAMES[i]));
            controls.add(slider);
            controls.add(spinner);
        }

        preview.setPreferredSize(new Dimension(300, 120));
        JPanel bottom = new JPanel(new GridLayout(0, 1));
        bottom.add(codeLabel);
        bottom.add(statusLabel);

        add(controls, BorderLayout.NORTH);
        add(preview, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        updatePreview();
        pack();
        setLocationRelativeTo(null);
    }

    private void updatePreview() {
        int r = sliders[0].getValue();
        int g = sliders[1].getValue();
        int b = sliders[2].getValue();
        preview.setBackground(new Color(r, g, b));
        codeLabel.setText("RGB(%d, %d, %d)  #%02X%02X%02X".formatted(r, g, b, r, g, b));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ColorApp().setVisible(true));
    }
}
