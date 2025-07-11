/**
 * リスト11-2
 * CollectionProblemクラス
 * 
 * 元ファイル: chapter11-generics.md (101行目)
 */

import java.util.ArrayList;
import java.util.List;

public class CollectionProblem {
    public static void main(String[] args) {
        // ジェネリクスを使わないリスト（raw type）
        List list = new ArrayList();
        
        // 文字列を追加
        list.add("Java");
        list.add("Python");
        
        // うっかり数値も追加できてしまう！
        list.add(42);
        
        // 取り出すとき...
        for (int i = 0; i < list.size(); i++) {
            // Object型として取り出される
            Object item = list.get(i);
            
            // 文字列として使いたい場合は型変換が必要
            String language = (String) item;  // 3番目の要素で実行時エラー！
            System.out.println(language.toUpperCase());
        }
    }
}