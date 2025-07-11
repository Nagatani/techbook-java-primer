package chapter17.solutions;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * マルチクライアント対応チャットサーバー
 * 複数のクライアントが同時に接続でき、チャットルーム機能を持つ
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class ChatServer {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private static final int DEFAULT_PORT = 8888;
    private static final int THREAD_POOL_SIZE = 50;
    
    private final int port;
    private final ExecutorService executor;
    private final Map<String, ClientHandler> clients;
    private final Map<String, ChatRoom> rooms;
    private ServerSocket serverSocket;
    private volatile boolean running;
    
    /**
     * ChatServerのコンストラクタ
     * 
     * @param port サーバーがリッスンするポート番号
     */
    public ChatServer(int port) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.clients = new ConcurrentHashMap<>();
        this.rooms = new ConcurrentHashMap<>();
        this.running = false;
        
        // デフォルトのチャットルームを作成
        rooms.put("general", new ChatRoom("general"));
        rooms.put("random", new ChatRoom("random"));
    }
    
    /**
     * サーバーを起動する
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        logger.info("チャットサーバー起動: ポート " + port);
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("新規接続: " + clientSocket.getRemoteSocketAddress());
                
                ClientHandler handler = new ClientHandler(clientSocket);
                executor.submit(handler);
            } catch (IOException e) {
                if (running) {
                    logger.log(Level.WARNING, "接続受付エラー", e);
                }
            }
        }
    }
    
    /**
     * サーバーを停止する
     */
    public void stop() {
        running = false;
        
        // 全クライアントを切断
        clients.values().forEach(ClientHandler::disconnect);
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "サーバーソケットクローズエラー", e);
        }
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("チャットサーバー停止");
    }
    
    /**
     * クライアントを登録する
     */
    private synchronized boolean registerClient(String username, ClientHandler handler) {
        if (clients.containsKey(username)) {
            return false;
        }
        clients.put(username, handler);
        logger.info("ユーザー登録: " + username);
        return true;
    }
    
    /**
     * クライアントを削除する
     */
    private synchronized void unregisterClient(String username) {
        clients.remove(username);
        logger.info("ユーザー削除: " + username);
    }
    
    /**
     * 全体にメッセージをブロードキャストする
     */
    private void broadcast(String message, String excludeUser) {
        clients.forEach((username, handler) -> {
            if (!username.equals(excludeUser)) {
                handler.sendMessage(message);
            }
        });
    }
    
    /**
     * プライベートメッセージを送信する
     */
    private void sendPrivateMessage(String from, String to, String message) {
        ClientHandler recipient = clients.get(to);
        if (recipient != null) {
            recipient.sendMessage("[Private from " + from + "]: " + message);
        } else {
            ClientHandler sender = clients.get(from);
            if (sender != null) {
                sender.sendMessage("Error: User '" + to + "' not found");
            }
        }
    }
    
    /**
     * チャットルームを表すクラス
     */
    private static class ChatRoom {
        private final String name;
        private final Set<String> members;
        private final List<String> messageHistory;
        private static final int MAX_HISTORY = 100;
        
        public ChatRoom(String name) {
            this.name = name;
            this.members = ConcurrentHashMap.newKeySet();
            this.messageHistory = new CopyOnWriteArrayList<>();
        }
        
        public void addMember(String username) {
            members.add(username);
        }
        
        public void removeMember(String username) {
            members.remove(username);
        }
        
        public Set<String> getMembers() {
            return new HashSet<>(members);
        }
        
        public void addMessage(String message) {
            messageHistory.add(message);
            if (messageHistory.size() > MAX_HISTORY) {
                messageHistory.remove(0);
            }
        }
        
        public List<String> getRecentMessages(int count) {
            int start = Math.max(0, messageHistory.size() - count);
            return new ArrayList<>(messageHistory.subList(start, messageHistory.size()));
        }
    }
    
    /**
     * クライアントハンドラー
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private ChatRoom currentRoom;
        private volatile boolean connected;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.connected = true;
        }
        
        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // ログインプロセス
                if (!handleLogin()) {
                    return;
                }
                
                // デフォルトルームに参加
                joinRoom("general");
                
                // メッセージ処理ループ
                String inputLine;
                while (connected && (inputLine = in.readLine()) != null) {
                    handleMessage(inputLine);
                }
                
            } catch (IOException e) {
                logger.log(Level.WARNING, "クライアント通信エラー: " + username, e);
            } finally {
                disconnect();
            }
        }
        
        /**
         * ログイン処理
         */
        private boolean handleLogin() throws IOException {
            out.println("Welcome to Chat Server! Please enter your username:");
            
            for (int attempts = 0; attempts < 3; attempts++) {
                String name = in.readLine();
                if (name == null || name.trim().isEmpty()) {
                    out.println("Invalid username. Please try again:");
                    continue;
                }
                
                name = name.trim();
                if (registerClient(name, this)) {
                    username = name;
                    out.println("Login successful! Type /help for commands.");
                    broadcast("*** " + username + " has joined the chat ***", username);
                    return true;
                } else {
                    out.println("Username already taken. Please choose another:");
                }
            }
            
            out.println("Login failed. Disconnecting...");
            return false;
        }
        
        /**
         * メッセージを処理する
         */
        private void handleMessage(String message) {
            if (message.startsWith("/")) {
                handleCommand(message);
            } else {
                // 通常のメッセージ
                if (currentRoom != null) {
                    String formattedMessage = "[" + currentRoom.name + "] " + username + ": " + message;
                    currentRoom.addMessage(formattedMessage);
                    
                    // ルームメンバーに送信
                    for (String member : currentRoom.getMembers()) {
                        ClientHandler handler = clients.get(member);
                        if (handler != null) {
                            handler.sendMessage(formattedMessage);
                        }
                    }
                }
            }
        }
        
        /**
         * コマンドを処理する
         */
        private void handleCommand(String command) {
            String[] parts = command.split(" ", 3);
            String cmd = parts[0].toLowerCase();
            
            switch (cmd) {
                case "/help":
                    sendMessage("Available commands:");
                    sendMessage("/list - Show online users");
                    sendMessage("/rooms - Show available rooms");
                    sendMessage("/join <room> - Join a room");
                    sendMessage("/leave - Leave current room");
                    sendMessage("/pm <user> <message> - Send private message");
                    sendMessage("/history [count] - Show message history");
                    sendMessage("/quit - Disconnect");
                    break;
                    
                case "/list":
                    sendMessage("Online users: " + String.join(", ", clients.keySet()));
                    break;
                    
                case "/rooms":
                    sendMessage("Available rooms: " + String.join(", ", rooms.keySet()));
                    break;
                    
                case "/join":
                    if (parts.length > 1) {
                        joinRoom(parts[1]);
                    } else {
                        sendMessage("Usage: /join <room>");
                    }
                    break;
                    
                case "/leave":
                    leaveCurrentRoom();
                    break;
                    
                case "/pm":
                    if (parts.length > 2) {
                        sendPrivateMessage(username, parts[1], parts[2]);
                    } else {
                        sendMessage("Usage: /pm <user> <message>");
                    }
                    break;
                    
                case "/history":
                    int count = 10;
                    if (parts.length > 1) {
                        try {
                            count = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            // デフォルト値を使用
                        }
                    }
                    showHistory(count);
                    break;
                    
                case "/quit":
                    connected = false;
                    break;
                    
                default:
                    sendMessage("Unknown command: " + cmd);
            }
        }
        
        /**
         * ルームに参加する
         */
        private void joinRoom(String roomName) {
            ChatRoom room = rooms.computeIfAbsent(roomName, ChatRoom::new);
            
            if (currentRoom != null) {
                leaveCurrentRoom();
            }
            
            currentRoom = room;
            room.addMember(username);
            sendMessage("Joined room: " + roomName);
            
            // ルームメンバーに通知
            String joinMessage = "*** " + username + " joined " + roomName + " ***";
            room.addMessage(joinMessage);
            for (String member : room.getMembers()) {
                if (!member.equals(username)) {
                    ClientHandler handler = clients.get(member);
                    if (handler != null) {
                        handler.sendMessage(joinMessage);
                    }
                }
            }
        }
        
        /**
         * 現在のルームから退出する
         */
        private void leaveCurrentRoom() {
            if (currentRoom != null) {
                currentRoom.removeMember(username);
                
                // ルームメンバーに通知
                String leaveMessage = "*** " + username + " left " + currentRoom.name + " ***";
                currentRoom.addMessage(leaveMessage);
                for (String member : currentRoom.getMembers()) {
                    ClientHandler handler = clients.get(member);
                    if (handler != null) {
                        handler.sendMessage(leaveMessage);
                    }
                }
                
                sendMessage("Left room: " + currentRoom.name);
                currentRoom = null;
            }
        }
        
        /**
         * メッセージ履歴を表示する
         */
        private void showHistory(int count) {
            if (currentRoom != null) {
                List<String> history = currentRoom.getRecentMessages(count);
                sendMessage("=== Message History ===");
                history.forEach(this::sendMessage);
                sendMessage("=== End of History ===");
            } else {
                sendMessage("You must join a room to see history");
            }
        }
        
        /**
         * メッセージを送信する
         */
        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
        
        /**
         * 切断処理
         */
        public void disconnect() {
            connected = false;
            
            if (username != null) {
                leaveCurrentRoom();
                unregisterClient(username);
                broadcast("*** " + username + " has left the chat ***", username);
            }
            
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "切断処理エラー", e);
            }
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("無効なポート番号: " + args[0]);
                port = DEFAULT_PORT;
            }
        }
        
        ChatServer server = new ChatServer(port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("シャットダウン処理を開始します...");
            server.stop();
        }));
        
        try {
            server.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "サーバー起動エラー", e);
            System.exit(1);
        }
    }
}