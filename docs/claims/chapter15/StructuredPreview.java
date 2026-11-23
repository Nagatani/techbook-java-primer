// 第15章：構造化並行性（Java 25ではまだプレビュー。--enable-previewなしではコンパイルできない）
import java.util.concurrent.StructuredTaskScope;

public class StructuredPreview {
    static void run() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> "a");
            scope.join();
        }
    }
}
