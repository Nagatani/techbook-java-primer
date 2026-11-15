package com.example.theory.specifications;

import com.example.theory.algorithms.BinarySearchWithSpecs;
import com.example.theory.algorithms.StructuredProgramming;

import java.util.ArrayList;
import java.util.List;

/**
 * assert文でできること・できないことを確かめる
 * -eaを付けた場合と付けない場合で、出力が変わる
 *
 * 実行: java -ea -cp out com.example.theory.specifications.AssertionDemo
 *       java -cp out com.example.theory.specifications.AssertionDemo
 */
public class AssertionDemo {

    public static void main(String[] args) {
        boolean enabled = false;
        assert enabled = true; // -eaのときだけ代入が実行される
        IO.println("Assertions enabled: " + enabled);

        // 1. ループ不変条件と事後条件の検査（-eaのときだけ検査される）
        StructuredProgramming structured = new StructuredProgramming();
        int[] numbers = {3, 7, 2, 9, 1, 5};
        IO.println("findMaxWithInvariantCheck: " + structured.findMaxWithInvariantCheck(numbers));

        BinarySearchWithSpecs search = new BinarySearchWithSpecs();
        int[] sorted = {1, 3, 5, 7, 9, 11, 13, 15};
        IO.println("Search 7 with postcondition check: "
                + search.binarySearchWithPostConditionCheck(sorted, 7));
        IO.println("Search 8 with postcondition check: "
                + search.binarySearchWithPostConditionCheck(sorted, 8));

        // 2. 事前条件（ソート済み）を破って呼び出す
        //    -eaなら検査で止まり、-eaなしでは誤った答え（1はあるのに-1）が返る
        int[] unsorted = {5, 1, 4, 2, 3};
        try {
            IO.println("Search 1 in unsorted array: " + search.binarySearch(unsorted, 1));
        } catch (AssertionError e) {
            IO.println("AssertionError: " + e.getMessage());
        }

        // 3. 悪い例: assertの中に、プログラムに必要な処理（副作用）を書く
        //    -eaなしでは、remove自体が実行されない
        List<String> tasks = new ArrayList<>(List.of("compile", "test"));
        assert tasks.remove("compile");
        IO.println("Tasks after assert with side effect: " + tasks);
    }
}
