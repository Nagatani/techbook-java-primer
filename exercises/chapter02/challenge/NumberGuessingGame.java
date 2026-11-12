/**
 * 第2章 チャレンジ課題: 数当てゲーム（解答例）
 *
 * コンピューターが選んだ数を、ヒントを頼りに当てるゲームです。
 * 難易度を選ぶと、数の範囲と挑戦できる回数が変わります。
 * 1回のゲームが終わったら、続けて遊ぶかどうかを尋ねます。
 *
 * 学習ポイント:
 * - IO.readlnとInteger.parseIntによる入力
 * - while文とfor文による繰り返し
 * - if文による判定と、boolean型の変数によるループの終わらせ方
 * - 配列による設定値の管理
 * - staticメソッドによる処理の分割
 * - Math.random()による乱数
 */
public class NumberGuessingGame {
    public static void main(String[] args) {
        String[] levelNames = {"簡単", "普通", "難しい"};
        int[] maxNumbers = {50, 100, 200};
        int[] maxTries = {10, 7, 5};

        IO.println("=== 数当てゲーム ===");

        int games = 0;
        int wins = 0;
        int totalTries = 0;
        int bestScore = 0;
        boolean playAgain = true;

        while (playAgain) {
            int level = selectLevel(levelNames, maxNumbers, maxTries);
            int tries = playGame(maxNumbers[level], maxTries[level]);

            games++;
            if (tries > 0) {
                wins++;
                totalTries += tries;
                int score = calculateScore(tries, maxTries[level]);
                IO.println("スコア: " + score + "点");
                if (score > bestScore) {
                    bestScore = score;
                    IO.println("ベストスコアを更新しました");
                }
            }

            String answer = IO.readln("もう一度遊びますか？ (y/n): ");
            playAgain = answer != null && answer.trim().equalsIgnoreCase("y");
            IO.println();
        }

        IO.println("=== ゲームの記録 ===");
        IO.println("遊んだ回数: " + games + "回");
        IO.println("当てた回数: " + wins + "回");
        if (wins > 0) {
            IO.println("当てるまでの平均回数: " + "%.1f".formatted((double) totalTries / wins) + "回");
        }
        IO.println("ベストスコア: " + bestScore + "点");
    }

    /**
     * 難易度を表示して選ばせ、選んだ難易度の番号（0から始まる）を返す
     */
    static int selectLevel(String[] levelNames, int[] maxNumbers, int[] maxTries) {
        IO.println("難易度を選んでください");
        for (int i = 0; i < levelNames.length; i++) {
            IO.println("%d. %s（1〜%d、%d回まで）".formatted(i + 1, levelNames[i], maxNumbers[i], maxTries[i]));
        }
        int choice = readNumber("番号: ", 1, levelNames.length);
        return choice - 1;
    }

    /**
     * 1回分のゲームを行う。当てたときは使った回数を、当てられなかったときは0を返す
     */
    static int playGame(int maxNumber, int maxTries) {
        int target = (int) (Math.random() * maxNumber) + 1;
        IO.println("1から" + maxNumber + "までの数を選びました。" + maxTries + "回以内に当ててください");

        int tries = 0;
        boolean hit = false;
        while (tries < maxTries && !hit) {
            tries++;
            int guess = readNumber(tries + "回目: ", 1, maxNumber);
            if (guess == target) {
                hit = true;
            } else if (guess < target) {
                IO.println("もっと大きい数です（残り" + (maxTries - tries) + "回）");
            } else {
                IO.println("もっと小さい数です（残り" + (maxTries - tries) + "回）");
            }
        }

        if (hit) {
            IO.println("正解です。" + tries + "回で当てました");
            return tries;
        }
        IO.println("残念でした。正解は" + target + "でした");
        return 0;
    }

    /**
     * 残った回数が多いほど高くなるスコアを計算する
     */
    static int calculateScore(int tries, int maxTries) {
        int base = 100;
        int bonus = (maxTries - tries) * 50;
        return base + bonus;
    }

    /**
     * minからmaxまでの整数が入力されるまで、入力を繰り返す
     * 数字以外が入力されたときの処理（例外処理）は第13章で学ぶため、ここでは数字だけかどうかを自分で調べる
     */
    static int readNumber(String message, int min, int max) {
        while (true) {
            String input = IO.readln(message);
            if (input == null) {
                IO.println("入力が終わったため、" + min + "を選んだことにします");
                return min;
            }
            input = input.trim();
            if (isDigits(input)) {
                int number = Integer.parseInt(input);
                if (number >= min && number <= max) {
                    return number;
                }
            }
            IO.println(min + "から" + max + "までの整数を入力してください");
        }
    }

    /**
     * 文字列が1文字以上の数字だけでできているかを調べる
     */
    static boolean isDigits(String text) {
        if (text.isEmpty() || text.length() > 9) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
