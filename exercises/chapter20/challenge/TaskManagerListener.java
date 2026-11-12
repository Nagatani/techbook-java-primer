/**
 * TaskManagerの内容が変わったことを受け取るリスナー。
 */
@FunctionalInterface
public interface TaskManagerListener {
    void tasksChanged();
}
