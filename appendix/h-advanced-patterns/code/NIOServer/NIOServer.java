import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;
import java.io.IOException;

public class NIOServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = 
            ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        IO.println("NIOサーバーが起動しました");
        
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                
                if (key.isAcceptable()) {
                    register(selector, serverChannel);
                }
                
                if (key.isReadable()) {
                    answerWithEcho(key);
                }
                
                iter.remove();
            }
        }
    }
    
    private static void register(Selector selector, 
                                ServerSocketChannel serverChannel) 
            throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
    
    private static void answerWithEcho(SelectionKey key) 
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead = client.read(buffer);
        
        // -1はクライアントが接続を閉じたことを表す。閉じないと、
        // selectが同じチャネルを読み取り可能として返し続ける
        if (bytesRead == -1) {
            key.cancel();
            client.close();
            return;
        }
        
        if (buffer.position() > 0) {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }
}
