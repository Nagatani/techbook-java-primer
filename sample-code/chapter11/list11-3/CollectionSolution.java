/**
 * リスト11-3
 * CollectionSolutionクラス
 * 
 * 元ファイル: chapter11-generics.md (137行目)
 */

import java.util.ArrayList;
import java.util.List;

public class CollectionSolution {
    public static void main(String[] args) {
        // String型だけを格納できるリスト
        List<String> list = new ArrayList<String>();
        
        // 文字列は問題なく追加できる
        list.add("Java");
        list.add("Python");
        
        // 数値を追加しようとすると...
        // list.add(42);  // コンパイルエラー！実行前に問題を発見
        
        // 取り出すときも型変換不要
        for (String language : list) {
            System.out.println(language.toUpperCase());
        }
    }
}