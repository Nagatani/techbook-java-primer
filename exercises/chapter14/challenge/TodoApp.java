import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** ToDoリストの保存と復元を実演するmainメソッド */
public class TodoApp {
    public static void main(String[] args) throws IOException {
        Path dir = Path.of("todo-data");
        Path textFile = dir.resolve("todo.tsv");
        Path objectFile = dir.resolve("todo.ser");
        Path reportFile = dir.resolve("report.txt");

        IO.println("=== 準備 ===");
        prepareDirectory(dir);

        IO.println();
        IO.println("=== テキスト形式で保存 ===");
        TodoList list = new TodoList();
        list.add("レポートの下書き", "11/20");
        list.add("図書館に本を返す", "11/15");
        list.add("サークルの会費を払う", "11/18");
        list.add("履修登録の確認", "11/30");
        IO.println("保存していない変更: " + list.isModified());
        int count = TodoFileStore.saveText(textFile, list);
        IO.println(textFile.getFileName() + " に" + count + "件保存しました（" + Files.size(textFile) + "バイト）");
        IO.println("保存していない変更: " + list.isModified());

        list.complete(2);
        list.add("買い物\t牛乳と卵", "11/16");
        count = TodoFileStore.saveText(textFile, list);
        IO.println(textFile.getFileName() + " に" + count + "件保存しました（" + Files.size(textFile) + "バイト）");

        IO.println();
        IO.println("=== " + textFile.getFileName() + " の中身 ===");
        for (String line : Files.readAllLines(textFile, StandardCharsets.UTF_8)) {
            IO.println(line.replace("\t", " | "));
        }

        IO.println();
        IO.println("=== 手で書き換えて壊れた行がある場合 ===");
        Files.writeString(textFile, "6\tゼミの発表準備\n" + "七\t部屋の掃除\t11/23\tfalse\n" + "8\t自転車の修理\t11/25\tfalse\n",
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        TodoFileStore.LoadResult result = TodoFileStore.loadText(textFile);
        for (String warning : result.warnings()) {
            IO.println("警告: " + warning);
        }
        TodoList loaded = result.list();
        IO.println(loaded.getTasks().size() + "件読み込みました");
        Task added = loaded.add("年賀状の準備", "12/20");
        IO.println("新しいタスクの番号: " + added.id());

        IO.println();
        IO.println("=== 直列化で保存して復元 ===");
        IO.println("保存前の変更あり: " + loaded.isModified());
        TodoFileStore.saveObject(objectFile, loaded);
        TodoList restored = TodoFileStore.loadObject(objectFile);
        IO.println("復元した件数: " + restored.getTasks().size());
        IO.println("同じ内容: " + restored.getTasks().equals(loaded.getTasks()));
        IO.println("復元後の変更あり: " + restored.isModified() + "（transientのフィールドは保存されない）");

        IO.println();
        IO.println("=== まとめを書き出す ===");
        writeReport(reportFile, restored);
        IO.print(Files.readString(reportFile, StandardCharsets.UTF_8));

        IO.println();
        IO.println("=== " + dir.getFileName() + " のファイル（名前順） ===");
        try (Stream<Path> files = Files.list(dir)) {
            files.map(path -> path.getFileName().toString()).sorted().forEach(IO::println);
        }
    }

    /** 作業用のディレクトリを用意する。前回の実行で作ったファイルは消す */
    private static void prepareDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            List<Path> oldFiles;
            try (Stream<Path> files = Files.list(dir)) {
                oldFiles = files.toList();
            }
            for (Path file : oldFiles) {
                Files.delete(file);
            }
        }
        Files.createDirectories(dir);
        IO.println(dir.getFileName() + " を空の状態で用意しました");
    }

    /** テキストブロックのひな形に値を埋め込んで、まとめのファイルを書く */
    private static void writeReport(Path path, TodoList list) throws IOException {
        // 未完了のタスクを期限の早い順に並べる（期限は「月/日」の2桁ずつなので文字列の順で比べられる）
        List<Task> undone = list.getTasks().stream()
                .filter(task -> !task.done())
                .sorted(Comparator.comparing(Task::due))
                .toList();
        StringBuilder lines = new StringBuilder();
        for (Task task : undone) {
            lines.append("  - ").append(task.due()).append(" ").append(task.title()).append("\n");
        }
        int total = list.getTasks().size();
        String report = """
                ToDoリストのまとめ
                ------------------
                全体: %d件（完了 %d件、未完了 %d件）

                未完了のタスク:
                %s""".formatted(total, total - undone.size(), undone.size(), lines);
        Files.writeString(path, report, StandardCharsets.UTF_8);
        IO.println(path.getFileName() + " を書き出しました");
    }
}
