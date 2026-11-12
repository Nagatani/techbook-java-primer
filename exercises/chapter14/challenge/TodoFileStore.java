import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** ToDoリストをファイルに保存し、ファイルから読み込む */
public class TodoFileStore {
    /** テキスト形式の1行目に置く見出し */
    public static final String HEADER = "#todo v1";

    /** 読み込んだ一覧と、読み飛ばした行の警告 */
    public record LoadResult(TodoList list, List<String> warnings) {}

    /**
     * タブ区切りのテキスト（UTF-8）で保存する
     * 前のファイルがあれば、上書きする前に「.bak」を付けた名前でコピーしておく。保存した件数を返す
     */
    public static int saveText(Path path, TodoList list) throws IOException {
        if (Files.exists(path)) {
            Path backup = path.resolveSibling(path.getFileName() + ".bak");
            Files.copy(path, backup, StandardCopyOption.REPLACE_EXISTING);
            IO.println("  前のファイルを " + backup.getFileName() + " にコピーしました");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(HEADER + "\n");
            for (Task task : list.getTasks()) {
                // 題名の中のタブは区切りと区別できないので、空白に置き換える
                String title = task.title().replace('\t', ' ');
                writer.write(task.id() + "\t" + title + "\t" + task.due() + "\t" + task.done() + "\n");
            }
        }
        list.markSaved();
        return list.getTasks().size();
    }

    /** テキスト形式を読み込む。壊れた行は読み飛ばし、行番号付きの警告にする */
    public static LoadResult loadText(Path path) throws IOException {
        TodoList list = new TodoList();
        List<String> warnings = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (!HEADER.equals(header)) {
                throw new IOException(path.getFileName() + " はToDoリストのファイルではありません");
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split("\t");
                if (fields.length != 4) {
                    warnings.add(lineNumber + "行目: 項目が4つではありません（" + line.replace('\t', ' ') + "）");
                    continue;
                }
                try {
                    int id = Integer.parseInt(fields[0]);
                    boolean done = Boolean.parseBoolean(fields[3]);
                    list.restore(new Task(id, fields[1], fields[2], done));
                } catch (NumberFormatException e) {
                    warnings.add(lineNumber + "行目: 番号が整数ではありません（" + fields[0] + "）");
                }
            }
        }
        return new LoadResult(list, warnings);
    }

    /** オブジェクトの直列化で保存する */
    public static void saveObject(Path path, TodoList list) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.writeObject(list);
        }
    }

    /** 直列化したファイルから読み込む */
    public static TodoList loadObject(Path path) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(path)))) {
            return (TodoList) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(path.getFileName() + " のクラスが見つかりません", e);
        }
    }
}
