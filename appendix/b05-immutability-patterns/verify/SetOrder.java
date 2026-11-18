import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Set.ofで作ったセットの要素の順序が、JVMを起動するたびに変わるかを観察する。
 * 自分自身を別のJVMで5回起動し、それぞれの順序を集める。
 *   javac SetOrder.java && java SetOrder
 */
public class SetOrder {
    public static void main(String[] args) throws IOException, InterruptedException {
        Set<String> set = Set.of("a", "b", "c", "d", "e", "f", "g", "h");
        if (args.length > 0 && args[0].equals("child")) {
            IO.println(String.join("", set));
            return;
        }
        String java = ProcessHandle.current().info().command().orElse("java");
        String classPath = System.getProperty("java.class.path");
        Set<String> orders = new LinkedHashSet<>();
        for (int i = 0; i < 5; i++) {
            Process p = new ProcessBuilder(List.of(java, "-cp", classPath, "SetOrder", "child"))
                    .redirectErrorStream(true).start();
            String order = new String(p.getInputStream().readAllBytes()).strip();
            p.waitFor();
            orders.add(order);
        }
        List<String> sorted = new ArrayList<>(set);
        sorted.sort(null);
        IO.println("同じ要素: " + String.join("", sorted));
        IO.println("5回の起動で現れた順序の種類: " + orders.size());
    }
}
