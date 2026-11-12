import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntConsumer;
import java.util.zip.CRC32;

/**
 * ファイルをサーバーへ送るクライアント
 */
public class FileTransferClient {
    private static final int BUFFER_SIZE = 8192;

    private final String host;
    private final int port;

    public FileTransferClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * ファイルを送る。progressには、25%進むごとに進み具合（25、50、75、100）を渡す
     */
    public TransferResult send(Path file, IntConsumer progress) throws IOException {
        long size = Files.size(file);
        CRC32 crc = new CRC32();
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream in = new DataInputStream(socket.getInputStream());
             InputStream fileIn = new BufferedInputStream(Files.newInputStream(file))) {
            out.writeUTF(file.getFileName().toString());
            out.writeLong(size);

            byte[] buffer = new byte[BUFFER_SIZE];
            long sent = 0;
            int nextReport = 25;
            int n;
            while ((n = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                crc.update(buffer, 0, n);
                sent += n;
                while (nextReport <= 100 && sent * 100 >= size * nextReport) {
                    progress.accept(nextReport);
                    nextReport += 25;
                }
            }
            out.flush();

            long received = in.readLong();
            long serverCrc = in.readLong();
            return new TransferResult(file.getFileName().toString(), size,
                    received == size, serverCrc == crc.getValue());
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("使い方: java FileTransferClient ホスト ポート ファイル...");
            return;
        }
        FileTransferClient client = new FileTransferClient(args[0], Integer.parseInt(args[1]));
        for (int i = 2; i < args.length; i++) {
            Path file = Path.of(args[i]);
            IO.println(file.getFileName() + " を送ります");
            TransferResult result = client.send(file, percent -> IO.println("  " + percent + "%"));
            IO.println(result.succeeded() ? "  送信に成功しました" : "  送信に失敗しました");
        }
    }
}
