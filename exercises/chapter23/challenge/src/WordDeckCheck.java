import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 画面を開かずにWordDeckの動きを確かめるプログラム
 */
public class WordDeckCheck {
    public static void main(String[] args) throws IOException {
        WordDeck deck = WordDeck.parse(List.of(
            "# 単語,意味", "apple,りんご", "borrow,借りる", "deadline,締め切り"));
        IO.println("単語の数: " + deck.size());

        WordCard first = deck.next();
        IO.println("1枚目: " + first.word() + " = " + first.meaning());
        deck.markKnown(first);
        IO.println("2枚目: " + deck.next().word());
        IO.println("3枚目: " + deck.next().word());
        IO.println("4枚目（覚えた単語は飛ばす）: " + deck.next().word());

        Path dir = Files.createTempDirectory("flashcard");
        Path file = dir.resolve("save").resolve("known.txt");
        deck.saveKnown(file);
        WordDeck reloaded = WordDeck.parse(List.of("apple,りんご", "borrow,借りる", "deadline,締め切り"));
        reloaded.loadKnown(file);
        IO.println("保存して読み直した覚えた単語の数: " + reloaded.knownCount());

        Files.delete(file);
        Files.delete(file.getParent());
        Files.delete(dir);
    }
}
