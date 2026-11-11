// 最初の版から、多くのクラスが実装しているインターフェイス
interface Greeting {
    String message();

    // あとの版で追加したdefaultメソッド
    // 実装クラスを書き換えなくても、すべての実装クラスで使える
    default String messageTo(String name) {
        return name + "さん、" + message();
    }
}

// defaultメソッドを追加する前から存在する実装クラス（変更していない）
class MorningGreeting implements Greeting {
    @Override
    public String message() {
        return "おはようございます";
    }
}

public class DefaultMethodDemo {
    public static void main(String[] args) {
        Greeting greeting = new MorningGreeting();
        IO.println(greeting.message());
        IO.println(greeting.messageTo("田中"));
    }
}
