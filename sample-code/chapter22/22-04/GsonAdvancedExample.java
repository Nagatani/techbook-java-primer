import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

public class GsonAdvancedExample {
    public static void main(String[] args) {
        // 1. カスタマイズされたGsonインスタンスの作成
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()  // 整形された出力
            .create();
        
        // 2. オブジェクトのシリアライズ
        Task task = new Task("買い物", false);
        String json = gson.toJson(task);
        IO.println("Pretty JSON:\n" + json);
        
        // 3. コレクションの処理
        List<Task> tasks = Arrays.asList(
            new Task("買い物", false),
            new Task("勉強", true),
            new Task("運動", false)
        );
        
        String tasksJson = gson.toJson(tasks);
        IO.println("\nTasks JSON:\n" + tasksJson);
        
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
        IO.println("\nLoaded Task: " + loadedTask.getTitle());
    }
}
