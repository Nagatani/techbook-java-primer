import java.io.Serializable;

/** ToDoリストの1件のタスク。直列化できるようにSerializableを実装する */
public record Task(int id, String title, String due, boolean done) implements Serializable {

    /** 完了にしたタスクを返す（レコードは変更できないので、新しく作る） */
    public Task complete() {
        return new Task(id, title, due, true);
    }
}
