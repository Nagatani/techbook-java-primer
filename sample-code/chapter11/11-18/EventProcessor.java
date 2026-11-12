import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventProcessor {
    // イベントの種類ごとに、登録されたハンドラーの一覧を持つ
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new HashMap<>();

    // イベントハンドラーの登録
    public <T> void on(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(event -> handler.accept(eventType.cast(event)));
    }

    // 条件を満たすイベントだけを処理するハンドラーの登録
    public <T> void onFiltered(Class<T> eventType,
                               Predicate<T> filter,
                               Consumer<T> handler) {
        on(eventType, event -> {
            if (filter.test(event)) {
                handler.accept(event);
            }
        });
    }

    // イベントの発行：登録されたハンドラーを順に呼び出す
    public void publish(Object event) {
        for (Consumer<Object> handler : handlers.getOrDefault(event.getClass(), List.of())) {
            handler.accept(event);
        }
    }

    record OrderCreated(String id, int amount) {}

    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();

        // すべての注文で呼ばれるハンドラー
        processor.on(OrderCreated.class,
            order -> IO.println("注文を受け付けました: " + order.id()));

        // 金額が10000円を超える注文だけで呼ばれるハンドラー
        processor.onFiltered(OrderCreated.class,
            order -> order.amount() > 10000,
            order -> IO.println("高額の注文です: " + order.id()));

        processor.publish(new OrderCreated("A001", 3000));
        processor.publish(new OrderCreated("A002", 25000));
    }
}
