/**
 * リスト5-8
 * Birdクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (461行目)
 */

// 悪い例：すべての鳥が飛べるという誤った前提
public class Bird {
    protected String name;
    
    public Bird(String name) {
        this.name = name;
    }
    
    public void fly() {
        System.out.println(name + " が飛んでいる");
    }
}

public class Eagle extends Bird {
    public Eagle(String name) {
        super(name);
    }
    // flyメソッドを適切に継承
}

public class Penguin extends Bird {
    public Penguin(String name) {
        super(name);
    }
    
    @Override
    public void fly() {
        // ペンギンは飛べない！
        throw new UnsupportedOperationException("ペンギンは飛べません");
    }
}

// 使用時の問題
public class BirdPark {
    public static void makeBirdsFly(List<Bird> birds) {
        for (Bird bird : birds) {
            bird.fly(); // ペンギンで例外が発生！
        }
    }
}