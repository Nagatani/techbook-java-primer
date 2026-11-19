// 第18章：匿名クラスとリスナー（JDK 1.1から）
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnonymousListener {
    static ActionListener make() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        };
    }
}
