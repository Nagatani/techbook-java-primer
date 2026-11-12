/**
 * 第2章 課題7: 文字列の操作 - 解答例
 *
 * 学習ポイント:
 * - trim、length、toUpperCase、indexOf、substring、contains
 * - equalsIgnoreCaseによる比較（==は使わない）
 * - StringBuilderによる文字列の組み立て
 */
public class Exercise07_Strings {
    public static void main(String[] args) {
        String line = IO.readln("英文を入力: ");
        if (line == null) {
            IO.println("入力がありません");
            return;
        }
        line = line.trim();

        int space = line.indexOf(" ");
        String firstWord;
        if (space == -1) {
            firstWord = line;
        } else {
            firstWord = line.substring(0, space);
        }

        StringBuilder reversed = new StringBuilder();
        for (int i = line.length() - 1; i >= 0; i--) {
            reversed.append(line.charAt(i));
        }

        IO.println("入力: " + line);
        IO.println("文字数: " + line.length());
        IO.println("大文字: " + line.toUpperCase());
        IO.println("最初の単語: " + firstWord);
        IO.println("Javaを含むか: " + line.contains("Java"));
        IO.println("exitか: " + line.equalsIgnoreCase("exit"));
        IO.println("逆順: " + reversed);
    }
}

/*
 * 補足:
 * - StringBuilderにはreverseメソッドもあり、new StringBuilder(line).reverse() でも逆順にできる
 * - trimした結果は新しい文字列になる。Stringは不変なので、line.trim(); だけでは line は変わらない
 */
