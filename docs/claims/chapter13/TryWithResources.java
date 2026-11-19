// 第13章：try-with-resources文（Java 7で導入）とjava.util.logging（Java 1.4）。--release 8で通ることだけを確かめる
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TryWithResources {
    static final Logger LOG = Logger.getLogger("x");

    static int first() throws Exception {
        try (StringReader r = new StringReader("abc")) {
            LOG.log(Level.FINE, "read");
            return r.read();
        }
    }
}
