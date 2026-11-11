import java.util.List;
import java.util.ArrayList;

public class VarLimitations {
    // var field = new ArrayList<String>(); // フィールドでは使用不可
    
    // public void method(var param) { } // パラメータでは使用不可
    
    // public var method() { return ""; } // 戻り値型では使用不可
    
    public static void demonstrateLimitations() {
        // OK: ローカル変数の初期化
        var list = new ArrayList<String>();
        
        // NG: 初期化なし
        // var uninitialized; // コンパイルエラー
        
        // NG: null初期化
        // var nullVar = null; // コンパイルエラー
        
        // NG: 配列初期化子
        // var array = {1, 2, 3}; // コンパイルエラー
        
        // OK: 明示的な配列作成
        var validArray = new int[]{1, 2, 3};
        
        // 注意: ダイヤモンド演算子単体はコンパイルできるが、ArrayList<Object>と推論される
        var diamond = new ArrayList<>(); // ArrayList<Object>
        
        // OK: 型が推論可能
        var inferredList = new ArrayList<String>();
    }
}
