// TodoInputPanel.java
import javax.swing.*;
import java.awt.*;

public class TodoInputPanel extends JPanel {
    private final JTextField inputField = new JTextField();
    private final JButton addButton = new JButton("追加");

    public TodoInputPanel(DefaultListModel<String> listModel) {
        super(new BorderLayout());
        addButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                inputField.setText("");
            }
        });
        add(inputField, BorderLayout.CENTER);
        add(addButton, BorderLayout.EAST);
    }
}
