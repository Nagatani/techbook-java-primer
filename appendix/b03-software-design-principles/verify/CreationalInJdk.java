import java.util.List;
import java.util.Locale;

/**
 * 標準ライブラリの中の生成に関するパターン。
 * 実行: javac CreationalInJdk.java && java CreationalInJdk
 */
public class CreationalInJdk {
    public static void main(String[] args) {
        // 静的ファクトリメソッドとキャッシュ：-128〜127は同じオブジェクトが返る
        IO.println("Integer.valueOf(127) == Integer.valueOf(127): "
                + (Integer.valueOf(127) == Integer.valueOf(127)));
        IO.println("Integer.valueOf(128) == Integer.valueOf(128): "
                + (Integer.valueOf(128) == Integer.valueOf(128)));

        // ファクトリは、返す実装クラスを隠せる（クラスの名前は実装の詳細）
        IO.println("List.of(1, 2)のクラス: " + List.of(1, 2).getClass().getName());
        IO.println("List.of(1, 2, 3)のクラス: " + List.of(1, 2, 3).getClass().getName());

        // シングルトン：publicなコンストラクターがなく、いつも同じオブジェクトを返す
        IO.println("Runtimeのpublicなコンストラクター: " + Runtime.class.getConstructors().length);
        IO.println("Runtime.getRuntime() == Runtime.getRuntime(): "
                + (Runtime.getRuntime() == Runtime.getRuntime()));

        // ビルダー：値を1つずつ設定し、最後に不変のオブジェクトを作る
        Locale locale = new Locale.Builder().setLanguage("ja").setRegion("JP").build();
        IO.println("Locale.Builderで作ったLocale: " + locale);

        // StringBuilderのappendは自分自身を返す（メソッドをつなげて書ける）
        StringBuilder sb = new StringBuilder();
        IO.println("sb.append(\"a\") == sb: " + (sb.append("a") == sb));
    }
}
