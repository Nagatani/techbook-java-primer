/** 無関係な2つのインターフェイスから同じdefaultメソッドを受け継ぐと、コンパイルエラーになる */
public class DiamondConflict {
    interface Left {
        default String who() {
            return "Left";
        }
    }

    interface Right {
        default String who() {
            return "Right";
        }
    }

    // who()をオーバーライドしていない
    static class Both implements Left, Right {
    }
}
