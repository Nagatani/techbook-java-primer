// StaticMemberExample.java
class Tool {
    // インスタンスメンバー（各Toolインスタンスが個別に持つ）
    String name;

    // クラスメンバー（Toolクラス全体で共有される）
    static int toolCount = 0;

    // コンストラクター
    public Tool(String name) {
        this.name = name;
        toolCount++; // インスタンスが作られるたびに、共有カウンターを増やす
        IO.println(this.name + " が作成されました。現在のツール総数: " + toolCount);
    }

    // インスタンスメソッド
    public void showName() {
        IO.println("このツールの名前は " + this.name + " です。");
    }

    // クラスメソッド
    public static void showToolCount() {
        // IO.println(this.name); // エラー！ staticメソッド内ではインスタンスメンバー(name)は使えない
        IO.println("作成されたツールの総数は " + toolCount + " です。");
    }
}

public class StaticMemberExample {
    public static void main(String[] args) {
        IO.println("--- ツール作成前 ---");
        // インスタンスがなくてもクラスメソッドは呼び出せる
        Tool.showToolCount();

        IO.println("\n--- ツール作成 ---");
        Tool hammer = new Tool("ハンマー");
        Tool wrench = new Tool("レンチ");

        IO.println("\n--- 各ツールの情報表示 ---");
        hammer.showName(); // インスタンスメソッドの呼び出し
        wrench.showName();

        IO.println("\n--- ツール総数の再確認 ---");
        // クラス名経由でのアクセスが推奨
        Tool.showToolCount();
    }
}
