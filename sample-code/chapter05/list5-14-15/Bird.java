/**
 * リスト5-14
 * Birdクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (752行目)
 */

// 悪い例：「飛べる鳥」と「泳げる鳥」を無理に継承で表現
public class Bird {
    public void eat() { /* ... */ }
    public void sleep() { /* ... */ }
}

public class FlyingBird extends Bird {
    public void fly() { /* ... */ }
}

public class SwimmingBird extends Bird {
    public void swim() { /* ... */ }
}

// 問題：ペンギンは飛べないが泳げる、鴨は飛べて泳げる
// どちらを継承すればよい？