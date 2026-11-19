// 第19章：SwingWorker（Java 6から）
import javax.swing.SwingWorker;

public class SwingWorkerProbe extends SwingWorker<String, Void> {
    @Override
    protected String doInBackground() {
        return "done";
    }
}
