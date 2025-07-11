/**
 * リスト6-9
 * SolvedAmbiguityクラス
 * 
 * 元ファイル: chapter06-immutability-and-final.md (342行目)
 */

import java.util.List; // java.util.Listを主に使うと決める

public class SolvedAmbiguity {
    public static void main(String[] args) {
        List<String> utilList = new java.util.ArrayList<>(); // importした方
        java.awt.List awtList = new java.awt.List();      // 完全限定名で指定
    }
}