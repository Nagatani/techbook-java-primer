// 第15章：スレッドのスタックの既定の大きさ（-XX:ThreadStackSize、単位はKB）。OSとCPUで変わる
import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.ManagementFactory;

public class StackSize {
    public static void main(String[] args) {
        HotSpotDiagnosticMXBean bean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        String kb = bean.getVMOption("ThreadStackSize").getValue();
        IO.println(System.getProperty("os.name") + " " + System.getProperty("os.arch") + ": ThreadStackSize=" + kb);
    }
}
