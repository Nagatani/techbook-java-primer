/**
 * リスト6-8
 * AmbiguousClassクラス
 * 
 * 元ファイル: chapter06-immutability-and-final.md (330行目)
 */

import java.util.List;
import java.awt.List; // エラー: Listクラスが両方のパッケージに存在する

public class AmbiguousClass {
    // List list; // どちらのListか不明
}