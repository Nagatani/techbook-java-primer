import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.ArrayList;
import java.util.List;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * JUnit PlatformのLauncherでテストクラスを実行し、テストごとの結果を表示する補助クラス。
 * 観察用のプログラムから呼び出す（単独では実行しない）。
 *
 * IDEやMaven（Surefire）、Gradleも、内部ではこのLauncherを使ってテストを見つけ、実行している。
 */
public class ProbeRunner {

    /** 1つのテストの結果（表示名、結果の種類、例外の型、例外のメッセージ） */
    public record Outcome(String name, String status, String exception, String message) {
    }

    /** テストクラスを実行し、テストごとの結果を表示して、結果の一覧を返す */
    public static List<Outcome> run(Class<?> testClass) {
        return run(testClass, true);
    }

    public static List<Outcome> run(Class<?> testClass, boolean print) {
        List<Outcome> outcomes = new ArrayList<>();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        TestExecutionListener listener = new TestExecutionListener() {
            @Override
            public void executionSkipped(TestIdentifier id, String reason) {
                if (id.isTest()) {
                    outcomes.add(new Outcome(id.getDisplayName(), "SKIPPED", "", reason));
                }
            }

            @Override
            public void executionFinished(TestIdentifier id, TestExecutionResult result) {
                if (!id.isTest()) {
                    return;
                }
                Throwable t = result.getThrowable().orElse(null);
                String type = t == null ? "" : t.getClass().getName();
                String message = t == null || t.getMessage() == null
                        ? "" : t.getMessage().lines().findFirst().orElse("");
                outcomes.add(new Outcome(id.getDisplayName(),
                        result.getStatus().toString(), type, message));
            }
        };
        SummaryGeneratingListener summary = new SummaryGeneratingListener();
        Launcher launcher = LauncherFactory.create();
        launcher.execute(request, listener, summary);
        if (print) {
            for (Outcome o : outcomes) {
                String detail = o.exception().isEmpty() ? o.message()
                        : o.exception() + ": " + o.message();
                IO.println("%-10s %s%s".formatted(o.status(), o.name(),
                        detail.isEmpty() ? "" : "  <- " + detail));
            }
            TestExecutionSummary s = summary.getSummary();
            IO.println("summary: found=%d succeeded=%d failed=%d aborted=%d skipped=%d"
                    .formatted(s.getTestsFoundCount(), s.getTestsSucceededCount(),
                            s.getTestsFailedCount(), s.getTestsAbortedCount(),
                            s.getTestsSkippedCount()));
        }
        return outcomes;
    }
}
