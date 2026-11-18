/** インターフェイスのフィールドは暗黙にfinalなので、代入できない */
public class AssignConstant {
    interface Config {
        int LIMIT = 10;
    }

    static void change() {
        Config.LIMIT = 20;
    }
}
