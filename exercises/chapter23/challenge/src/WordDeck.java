import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 単語カードの束。覚えた単語を記録し、ファイルに保存する
 * GUIに依存しないので、単独で動作を確かめられる
 */
public class WordDeck {
    private final List<WordCard> cards;
    private final Set<String> known = new LinkedHashSet<>();
    private int current = -1;

    public WordDeck(List<WordCard> cards) {
        if (cards.isEmpty()) {
            throw new IllegalArgumentException("単語が1つもありません");
        }
        this.cards = List.copyOf(cards);
    }

    /** 「単語,意味」の行から束を作る。空行と#で始まる行は読み飛ばす */
    public static WordDeck parse(List<String> lines) {
        List<WordCard> cards = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] cols = line.split(",", 2);
            if (cols.length == 2) {
                cards.add(new WordCard(cols[0].trim(), cols[1].trim()));
            }
        }
        return new WordDeck(cards);
    }

    /** まだ覚えていない次のカードを返す。すべて覚えたらnull */
    public WordCard next() {
        for (int i = 1; i <= cards.size(); i++) {
            int index = (current + i) % cards.size();
            if (!known.contains(cards.get(index).word())) {
                current = index;
                return cards.get(index);
            }
        }
        return null;
    }

    /** 単語を覚えたことにする */
    public void markKnown(WordCard card) {
        known.add(card.word());
    }

    /** 覚えた記録を消す */
    public void reset() {
        known.clear();
        current = -1;
    }

    public int size() {
        return cards.size();
    }

    /** 覚えた単語の数（束にない単語の記録は数えない） */
    public int knownCount() {
        int count = 0;
        for (WordCard card : cards) {
            if (known.contains(card.word())) {
                count++;
            }
        }
        return count;
    }

    /** 覚えた単語を1行に1つずつ保存する。保存先のフォルダーがなければ作る */
    public void saveKnown(Path file) throws IOException {
        Files.createDirectories(file.getParent());
        Files.write(file, known, StandardCharsets.UTF_8);
    }

    /** 保存した記録を読み込む。ファイルがなければ何もしない */
    public void loadKnown(Path file) throws IOException {
        if (Files.exists(file)) {
            known.addAll(Files.readAllLines(file, StandardCharsets.UTF_8));
        }
    }
}
