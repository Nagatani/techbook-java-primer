import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * 英単語の単語帳アプリ
 * 単語はJARの中のリソース（/words.csv）から読み、覚えた単語はホームディレクトリに保存する
 */
public class FlashcardApp extends JFrame {
    /** 覚えた単語の保存先（インストール先ではなく、利用者のホームディレクトリの下） */
    static final Path SAVE_FILE = Path.of(System.getProperty("user.home"), ".flashcard-app", "known.txt");

    private final WordDeck deck;
    private final JLabel wordLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel meaningLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel();
    private final JButton showButton = new JButton("意味を見る");
    private final JButton knownButton = new JButton("覚えた");
    private final JButton nextButton = new JButton("次へ");
    private WordCard card;

    public FlashcardApp(WordDeck deck) {
        super("単語帳");
        this.deck = deck;

        wordLabel.setFont(wordLabel.getFont().deriveFont(Font.BOLD, 32f));
        meaningLabel.setFont(meaningLabel.getFont().deriveFont(20f));
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(wordLabel, BorderLayout.CENTER);
        center.add(meaningLabel, BorderLayout.SOUTH);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(showButton);
        buttons.add(knownButton);
        buttons.add(nextButton);
        JButton resetButton = new JButton("最初から");
        buttons.add(resetButton);

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttons, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));

        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        showButton.addActionListener(e -> meaningLabel.setText(card.meaning()));
        knownButton.addActionListener(e -> {
            deck.markKnown(card);
            save();
            showNext();
        });
        nextButton.addActionListener(e -> showNext());
        resetButton.addActionListener(e -> {
            deck.reset();
            save();
            showNext();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 260);
        setLocationRelativeTo(null);
        showNext();
    }

    /** まだ覚えていない次の単語を表示する */
    private void showNext() {
        card = deck.next();
        boolean hasCard = card != null;
        wordLabel.setText(hasCard ? card.word() : "すべて覚えました");
        meaningLabel.setText(" ");
        showButton.setEnabled(hasCard);
        knownButton.setEnabled(hasCard);
        nextButton.setEnabled(hasCard);
        statusLabel.setText("覚えた単語: " + deck.knownCount() + " / " + deck.size()
            + "　保存先: " + SAVE_FILE);
    }

    private void save() {
        try {
            deck.saveKnown(SAVE_FILE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存できませんでした: " + e.getMessage());
        }
    }

    /** JARの中のリソースを読み込む */
    static WordDeck loadDeck() throws IOException {
        InputStream in = FlashcardApp.class.getResourceAsStream("/words.csv");
        if (in == null) {
            throw new IOException("リソース /words.csv が見つかりません");
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        WordDeck deck = WordDeck.parse(lines);
        deck.loadKnown(SAVE_FILE);
        return deck;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FlashcardApp(loadDeck()).setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "起動できません: " + e.getMessage());
            }
        });
    }
}
