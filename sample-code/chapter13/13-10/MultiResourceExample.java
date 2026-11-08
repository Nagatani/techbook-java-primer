import java.io.*;
import java.sql.*;

public class MultiResourceExample {
    public static void copyFile(String sourcePath, String destPath) 
            throws IOException {
        // 複数のリソースをセミコロンで区切って宣言
        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            
        } // すべてのリソースが逆順（宣言と逆の順序）で自動的にクローズされる
    }
    
    // データベース接続の例
    public static void executeDatabaseTransaction() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mydb";
        
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO users (name, email) VALUES (?, ?)");
             Statement stmt = conn.createStatement()) {
            
            conn.setAutoCommit(false);
            
            try {
                pstmt.setString(1, "山田太郎");
                pstmt.setString(2, "yamada@example.com");
                pstmt.executeUpdate();
                
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) {
                    System.out.println("ユーザー数: " + rs.getInt(1));
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
