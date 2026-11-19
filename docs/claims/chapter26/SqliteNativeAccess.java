// 第26章：sqlite-jdbcをJava 25で使うと、ネイティブアクセスの警告が出る
import java.sql.Connection;
import java.sql.DriverManager;

public class SqliteNativeAccess {
    public static void main(String[] args) throws Exception {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            System.out.println("connected: " + con.getMetaData().getDatabaseProductName());
        }
    }
}
