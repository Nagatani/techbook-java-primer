import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class VarRefactoringExample {
    
    // リファクタリング前
    public static void beforeRefactoring() {
        HashMap<String, List<Map<String, Object>>> complexData = 
            new HashMap<String, List<Map<String, Object>>>();
        
        ArrayList<String> filteredNames = new ArrayList<String>();
        
        List<Integer> processedNumbers = Arrays.asList(1, 2, 3, 4, 5)
            .stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .collect(Collectors.toList());
    }
    
    // リファクタリング後
    public static void afterRefactoring() {
        var complexData = new HashMap<String, List<Map<String, Object>>>();
        var filteredNames = new ArrayList<String>();
        
        var processedNumbers = Arrays.asList(1, 2, 3, 4, 5)
            .stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .collect(Collectors.toList());
    }
    
    // 段階的リファクタリングの例
    public static void gradualRefactoring() {
        // ステップ1: もっとも冗長な部分から開始
        var userCache = new ConcurrentHashMap<String, UserProfile>();
        
        // ステップ2: 型が明確なファクトリーメソッド
        var emptyList = Collections.<String>emptyList();
        
        // ステップ3: Stream処理結果
        var groupedUsers = getAllUsers()
            .stream()
            .collect(Collectors.groupingBy(UserProfile::getDepartment));
    }
    
    private static List<UserProfile> getAllUsers() {
        return List.of();
    }
    
    static class UserProfile {
        public String getDepartment() { return "Engineering"; }
    }
}
