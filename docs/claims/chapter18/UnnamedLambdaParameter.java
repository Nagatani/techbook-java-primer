// 第18章：使わないラムダ式の引数を_と書く（Java 22で正式）
import java.awt.event.ActionListener;

public class UnnamedLambdaParameter {
    static ActionListener make() {
        return _ -> { };
    }
}
