import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DynamicPanel extends JPanel {
    private final List<JComponent> components = new ArrayList<>();
    private final GridBagConstraints gbc = new GridBagConstraints();
    
    public DynamicPanel() {
        setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
    }
    
    public void addComponent(JComponent component) {
        components.add(component);
        refreshLayout();
    }
    
    public void removeComponent(int index) {
        if (index >= 0 && index < components.size()) {
            JComponent component = components.remove(index);
            remove(component);
            refreshLayout();
        }
    }
    
    private void refreshLayout() {
        // 全てのコンポーネントを一度削除
        removeAll();
        
        // 制約を再計算して追加
        for (int i = 0; i < components.size(); i++) {
            gbc.gridx = i % 3;  // 3列のグリッド
            gbc.gridy = i / 3;
            add(components.get(i), (GridBagConstraints)gbc.clone());
        }
        
        // レイアウトを更新
        revalidate();
        repaint();
    }
    
    public void clear() {
        components.clear();
        removeAll();
        revalidate();
        repaint();
    }
}
