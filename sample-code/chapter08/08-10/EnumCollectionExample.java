import java.util.EnumSet;
import java.util.EnumMap;

public class EnumCollectionExample {
    public enum Permission {
        READ, WRITE, EXECUTE, DELETE
    }
    
    public static void main(String[] args) {
        // EnumSet: enum専用の高速なSet実装
        EnumSet<Permission> adminPermissions = EnumSet.allOf(Permission.class);
        EnumSet<Permission> userPermissions = EnumSet.of(Permission.READ, Permission.WRITE);
        
        System.out.println("管理者権限: " + adminPermissions);
        System.out.println("ユーザー権限: " + userPermissions);
        
        // EnumMap: enumをキーとする高速なMap実装
        EnumMap<Permission, String> permissionDescriptions = new EnumMap<>(Permission.class);
        permissionDescriptions.put(Permission.READ, "読み取り権限");
        permissionDescriptions.put(Permission.WRITE, "書き込み権限");
        permissionDescriptions.put(Permission.EXECUTE, "実行権限");
        permissionDescriptions.put(Permission.DELETE, "削除権限");
        
        for (Permission p : Permission.values()) {
            System.out.println(p + ": " + permissionDescriptions.get(p));
        }
    }
}
