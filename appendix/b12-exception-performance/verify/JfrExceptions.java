import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

/**
 * JFR（JDK Flight Recorder）で例外のイベントを記録する。
 *
 *   javac JfrExceptions.java && java JfrExceptions
 */
public class JfrExceptions {

    static class Quiet extends RuntimeException {
        Quiet() {
            super("quiet", null, false, false);
        }
    }

    public static void main(String[] args) throws Exception {
        for (String name : List.of("default", "profile")) {
            Map<String, String> settings = Configuration.getConfiguration(name).getSettings();
            IO.println("設定%sのjdk.JavaExceptionThrow: enabled=%s throttle=%s".formatted(
                    name, settings.get("jdk.JavaExceptionThrow#enabled"),
                    settings.get("jdk.JavaExceptionThrow#throttle")));
        }

        Path file = Files.createTempFile("exceptions", ".jfr");
        try (Recording recording = new Recording()) {
            recording.enable("jdk.JavaExceptionThrow");
            recording.enable("jdk.ExceptionStatistics");
            recording.start();

            for (int i = 0; i < 3; i++) {
                try {
                    throw new IllegalStateException("thrown " + i);
                } catch (IllegalStateException e) {
                    // 受け取るだけ
                }
            }
            new IllegalArgumentException("created but not thrown");
            new Quiet();

            recording.stop();
            recording.dump(file);
        }

        Map<String, Integer> thrown = new TreeMap<>();
        List<Long> statistics = new ArrayList<>();
        for (RecordedEvent e : RecordingFile.readAllEvents(file)) {
            if (e.getEventType().getName().equals("jdk.JavaExceptionThrow")) {
                String type = e.getClass("thrownClass").getName();
                if (!type.startsWith("JfrExceptions") && !type.startsWith("java.lang.Illegal")) {
                    continue;
                }
                thrown.merge(type, 1, Integer::sum);
            } else if (e.getEventType().getName().equals("jdk.ExceptionStatistics")) {
                statistics.add(e.getLong("throwables"));
            }
        }
        thrown.forEach((type, n) -> IO.println("jdk.JavaExceptionThrow " + type + ": " + n));
        // jdk.ExceptionStatisticsは、作られたThrowableの累計を定期的に記録する
        if (statistics.size() >= 2) {
            long grown = statistics.get(statistics.size() - 1) - statistics.get(0);
            IO.println("jdk.ExceptionStatisticsのthrowablesの増加が5以上: " + (grown >= 5));
        }
        Files.delete(file);
    }
}
