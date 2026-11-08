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
        this.createdAt = LocalDateTime.now();
        this.tags = Arrays.asList("work", "important");
    }
    
    // ゲッター・セッター省略
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
}
