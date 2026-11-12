import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * ToDoリスト
 *
 * - タスク: 並び順に意味があり、順番を入れ替えるのでList（ArrayList）
 * - タグの一覧: 重複を除き、文字コードの順（辞書順）に並べるのでTreeSet
 * - タグごとのタスク: タグから引くのでMap。タグの辞書順に表示するのでTreeMap
 */
public class TodoList {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    // タスクを末尾に加える
    public Task add(String title, String... tags) {
        Task task = new Task(nextId, title, tags);
        nextId++;
        tasks.add(task);
        return task;
    }

    // 番号でタスクを探す。見つからなければnull
    public Task find(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public boolean complete(int id) {
        Task task = find(id);
        if (task == null) {
            return false;
        }
        task.markDone();
        return true;
    }

    // タスクを先頭に移す（いちばん先にやる）
    public boolean moveToTop(int id) {
        Task task = find(id);
        if (task == null) {
            return false;
        }
        tasks.remove(task);
        tasks.add(0, task);
        return true;
    }

    // 完了したタスクを取り除き、取り除いた数を返す
    // 走査しながら削除するので、拡張for文ではなくIteratorのremoveを使う
    public int removeCompleted() {
        int count = 0;
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            if (task.isDone()) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    // 使われているタグの一覧（重複なし、辞書順）
    public Set<String> allTags() {
        Set<String> result = new TreeSet<>();
        for (Task task : tasks) {
            result.addAll(task.getTags());
        }
        return result;
    }

    // タグごとのタスクの内容（タグの辞書順、タスクはリストの順）
    public Map<String, List<String>> tasksByTag() {
        Map<String, List<String>> result = new TreeMap<>();
        for (Task task : tasks) {
            for (String tag : task.getTags()) {
                List<String> titles = result.get(tag);
                if (titles == null) {
                    titles = new ArrayList<>();
                    result.put(tag, titles);
                }
                titles.add(task.getTitle());
            }
        }
        return result;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
