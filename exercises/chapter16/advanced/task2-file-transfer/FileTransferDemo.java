import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * 同じプログラムの中でサーバーとクライアントを動かし、ファイルの転送を確かめる
 */
public class FileTransferDemo {
    public static void main(String[] args) throws Exception {
        Path work = Files.createTempDirectory("file-transfer");
        Path sendDir = Files.createDirectories(work.resolve("send"));
        Path receiveDir = work.resolve("received");
        try {
            List<Path> files = List.of(
                    createTextFile(sendDir.resolve("report.txt"), 3000),
                    createBinaryFile(sendDir.resolve("photo.bin"), 300_000),
                    createBinaryFile(sendDir.resolve("music.bin"), 1_000_000),
                    createTextFile(sendDir.resolve("memo.txt"), 10));

            // ポート番号0で、空いているポートを自動で選ばせる
            FileTransferServer server = new FileTransferServer(0, receiveDir, false);
            server.start();
            FileTransferClient client = new FileTransferClient("127.0.0.1", server.getPort());
            try {
                IO.println("=== 1つのファイルを送る ===");
                TransferResult first = client.send(files.get(1), p -> IO.println("  photo.bin: " + p + "%"));
                printResult(first);

                IO.println("");
                IO.println("=== 3つのファイルを同時に送る ===");
                ExecutorService senders = Executors.newFixedThreadPool(3);
                List<Future<TransferResult>> futures = new ArrayList<>();
                for (Path file : List.of(files.get(0), files.get(2), files.get(3))) {
                    futures.add(senders.submit(() -> client.send(file, p -> { })));
                }
                for (Future<TransferResult> future : futures) {
                    try {
                        printResult(future.get());
                    } catch (ExecutionException e) {
                        IO.println("  送信中にエラー: " + e.getCause().getMessage());
                    }
                }
                senders.shutdown();
            } finally {
                server.stop();
            }

            IO.println("");
            IO.println("=== 受け取ったファイル ===");
            try (Stream<Path> received = Files.list(receiveDir)) {
                for (Path file : received.sorted().toList()) {
                    Path original = sendDir.resolve(file.getFileName());
                    boolean same = Files.mismatch(file, original) == -1;
                    IO.println("  %s %,dバイト 元のファイルと%s".formatted(
                            file.getFileName(), Files.size(file), same ? "同じ" : "違う"));
                }
            }
        } finally {
            deleteAll(work);
        }
    }

    private static void printResult(TransferResult r) {
        IO.println("  %s %,dバイト: %s".formatted(r.fileName(), r.size(),
                r.succeeded() ? "成功（大きさ・チェックサムが一致）" : "失敗"));
    }

    /** 番号付きの行を並べたテキストファイルを作る */
    private static Path createTextFile(Path file, int lines) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("行目: ネットワークでファイルを送るテスト\n");
        }
        return Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
    }

    /** 決まった規則で並べたバイト列のファイルを作る（実行ごとに同じ内容になる） */
    private static Path createBinaryFile(Path file, int size) throws IOException {
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (i * 31 + i / 256);
        }
        return Files.write(file, data);
    }

    private static void deleteAll(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(p);
            }
        }
    }
}
