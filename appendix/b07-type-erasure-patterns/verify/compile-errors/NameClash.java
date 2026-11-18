import java.util.List;

// 型引数だけが違うオーバーロードは、消去後に同じシグネチャになる
class NameClash {
    void process(List<String> list) { }
    void process(List<Integer> list) { }
}
