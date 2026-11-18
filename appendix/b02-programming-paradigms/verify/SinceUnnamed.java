import java.util.function.Function;

/** 名前のない変数 _（--release 22 からコンパイルできる） */
public class SinceUnnamed {
    static final Function<Object, String> CONSTANT = _ -> "ignored";
}
