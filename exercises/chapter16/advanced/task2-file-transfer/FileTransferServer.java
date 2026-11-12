import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

/**
 * ファイルを受け取って保存するサーバー。複数のクライアントから同時に受け取れる
 *
 * <p>手順: クライアントがファイル名（writeUTF）、大きさ（writeLong）、中身を送る。
 * サーバーは受け取ったバイト数とCRC32の値（どちらもwriteLong）を返す
 */
public class FileTransferServer {
    private static final int BUFFER_SIZE = 8192;
    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;  // 受け取る大きさの上限（100MB）

    private final int port;
    private final Path saveDir;
    private final boolean verbose;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public FileTransferServer(int port, Path saveDir, boolean verbose) {
        this.port = port;
        this.saveDir = saveDir;
        this.verbose = verbose;
    }

    public void start() throws IOException {
        Files.createDirectories(saveDir);
        serverSocket = new ServerSocket(port);
        acceptThread = new Thread(this::acceptLoop, "accept");
        acceptThread.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void stop() throws IOException, InterruptedException {
        serverSocket.close();
        acceptThread.join();
        threadPool.shutdown();
        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
        }
    }

    private void acceptLoop() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> receive(socket));
            } catch (SocketException e) {
                return;  // stop()で閉じられた
            } catch (IOException e) {
                System.err.println("接続を受け付けられませんでした: " + e.getMessage());
            }
        }
    }

    private void receive(Socket socket) {
        try (socket;
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            String name = in.readUTF();
            long size = in.readLong();
            // ファイル名に含まれるフォルダーの部分は捨てる（../で保存先の外に書かせない）
            Path fileName = Path.of(name).getFileName();
            if (fileName == null || size < 0 || size > MAX_FILE_SIZE) {
                out.writeLong(-1);
                out.writeLong(0);
                return;
            }
            Path target = saveDir.resolve(fileName.toString());
            CRC32 crc = new CRC32();
            long received = 0;
            try (OutputStream file = new BufferedOutputStream(Files.newOutputStream(target))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (received < size) {
                    int n = in.read(buffer, 0, (int) Math.min(buffer.length, size - received));
                    if (n == -1) {
                        throw new EOFException("ファイルの途中で接続が切れました");
                    }
                    file.write(buffer, 0, n);
                    crc.update(buffer, 0, n);
                    received += n;
                }
            }
            out.writeLong(received);
            out.writeLong(crc.getValue());
            out.flush();
            if (verbose) {
                IO.println("[server] " + fileName + " を受け取りました（" + received + "バイト）");
            }
        } catch (IOException e) {
            System.err.println("受信に失敗しました: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        int port = args.length >= 1 ? Integer.parseInt(args[0]) : 5001;
        Path saveDir = Path.of(args.length >= 2 ? args[1] : "received");
        FileTransferServer server = new FileTransferServer(port, saveDir, true);
        server.start();
        IO.println("ポート" + server.getPort() + "で待ち受けています。保存先: " + saveDir + "（Ctrl+Cで終了）");
    }
}
