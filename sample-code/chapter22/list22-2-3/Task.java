/**
 * リスト22-3
 * Taskクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (314行目)
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

// Gsonの高度な使用例
public class Task {
    @SerializedName("task_title")  // JSONのフィールド名を指定
    private String title;
    
    private boolean completed;
    
    @Expose(serialize = false)  // シリアライズ時に除外
    private String internalId;
    
    private LocalDateTime createdAt;
    private List<String> tags;
    
    public Task(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
        this.createdAt = new Date();
        this.tags = Arrays.asList("work", "important");
    }
    
    // ゲッター・セッター省略
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
}

public class GsonAdvancedExample {
    public static void main(String[] args) {
        // 1. カスタマイズされたGsonインスタンスの作成
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()  // 整形された出力
            .setDateFormat("yyyy-MM-dd HH:mm:ss")  // 日付フォーマット
            .excludeFieldsWithoutExposeAnnotation()  // @Exposeのないフィールドを除外
            .create();
        
        // 2. オブジェクトのシリアライズ
        Task task = new Task("買い物", false);
        String json = gson.toJson(task);
        System.out.println("Pretty JSON:\n" + json);
        
        // 3. コレクションの処理
        List<Task> tasks = Arrays.asList(
            new Task("買い物", false),
            new Task("勉強", true),
            new Task("運動", false)
        );
        
        String tasksJson = gson.toJson(tasks);
        System.out.println("\nTasks JSON:\n" + tasksJson);
        
        // 4. 複雑なデシリアライズ
        String complexJson = """
            {
                "task_title": "プログラミング",
                "completed": false,
                "createdAt": "2024-01-20 10:30:00",
                "tags": ["study", "java", "important"]
            }
            """;
        
        Task loadedTask = gson.fromJson(complexJson, Task.class);
        System.out.println("\nLoaded Task: " + loadedTask.getTitle());
    }
}