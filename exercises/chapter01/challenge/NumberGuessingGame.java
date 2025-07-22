/**
 * 第1章 チャレンジ課題1: 数値推測ゲーム
 * 
 * コンピューターが考えた数値をユーザーが推測するインタラクティブゲーム。
 * 難易度選択、スコアシステム、統計機能を含む本格的なゲーム実装。
 * 
 * 学習ポイント:
 * - Math.random()を使った乱数生成
 * - 複雑な条件分岐とループ処理
 * - 静的変数を使った状態管理
 * - ユーザーインターフェイスの設計
 * - ゲームロジックの実装
 */

public class NumberGuessingGame {
    // ゲーム統計用の静的変数
    private static int totalGames = 0;
    private static int totalTries = 0;
    private static int bestScore = 0;
    private static double bestAverage = Double.MAX_VALUE;
    
    // 難易度設定
    private static final int[][] DIFFICULTY_SETTINGS = {
        {50, 10},   // 簡単: 1-50, 10回
        {100, 7},   // 普通: 1-100, 7回
        {200, 5}    // 難しい: 1-200, 5回
    };
    
    private static final String[] DIFFICULTY_NAMES = {"簡単", "普通", "難しい"};
    
    public static void main(String[] args) {
        displayWelcomeMessage();
        
        boolean playAgain = true;
        while (playAgain) {
            playGame();
            playAgain = askPlayAgain();
        }
        
        displayFinalStatistics();
        System.out.println("ゲームを終了します。ありがとうございました！");
    }
    
    /**
     * ウェルカムメッセージを表示します
     */
    private static void displayWelcomeMessage() {
        System.out.println("=== 数値推測ゲーム ===");
        System.out.println();
        System.out.println("コンピューターが考えた数値を当ててください！");
        System.out.println("ヒントを参考に、少ない回数で正解を目指しましょう。");
        System.out.println();
    }
    
    /**
     * メインゲームループ
     */
    private static void playGame() {
        int difficulty = selectDifficulty();
        int maxNumber = DIFFICULTY_SETTINGS[difficulty][0];
        int maxTries = DIFFICULTY_SETTINGS[difficulty][1];
        
        int targetNumber = generateRandomNumber(maxNumber);
        
        System.out.println();
        System.out.println("コンピューターが1から" + maxNumber + "の数値を考えました！");
        System.out.println(maxTries + "回以内に当ててください。");
        System.out.println();
        
        int tries = 0;
        boolean hasWon = false;
        
        while (tries < maxTries && !hasWon) {
            tries++;
            int guess = getPlayerGuess(tries, maxTries - tries + 1);
            
            if (guess == targetNumber) {
                hasWon = true;
                displayWinMessage(tries, difficulty, maxTries);
            } else {
                displayHint(guess, targetNumber, maxTries - tries);
            }
        }
        
        if (!hasWon) {
            displayLoseMessage(targetNumber);
        }
        
        updateStatistics(tries, hasWon, difficulty, maxTries);
        displayCurrentStatistics();
    }
    
    /**
     * 難易度選択
     */
    private static int selectDifficulty() {
        System.out.println("難易度を選択してください:");
        
        for (int i = 0; i < DIFFICULTY_NAMES.length; i++) {
            int maxNum = DIFFICULTY_SETTINGS[i][0];
            int maxTries = DIFFICULTY_SETTINGS[i][1];
            System.out.println((i + 1) + ". " + DIFFICULTY_NAMES[i] + 
                              " (1-" + maxNum + ", " + maxTries + "回)");
        }
        
        System.out.print("選択 (1-3): ");
        
        // 簡単な入力処理（実際のプログラムでは Scanner を使用）
        // ここでは難易度1（普通）を自動選択
        int choice = 2; // デフォルトで普通を選択
        System.out.println(choice);
        
        if (choice >= 1 && choice <= 3) {
            return choice - 1; // 0ベースのインデックスに変換
        } else {
            System.out.println("無効な選択です。普通を選択します。");
            return 1; // 普通をデフォルト
        }
    }
    
    /**
     * ランダムな数値を生成します
     */
    private static int generateRandomNumber(int max) {
        return (int)(Math.random() * max) + 1;
    }
    
    /**
     * プレイヤーの推測を取得します
     */
    private static int getPlayerGuess(int tryNumber, int remainingTries) {
        System.out.print(tryNumber + "回目の推測 (残り" + (remainingTries - 1) + "回): ");
        
        // 実際のプログラムでは Scanner を使用して入力を受け取る
        // ここではデモ用に自動的に値を生成
        int guess = generateDemoGuess(tryNumber);
        System.out.println(guess);
        
        return guess;
    }
    
    /**
     * デモ用の推測値を生成（実際のプログラムでは不要）
     */
    private static int generateDemoGuess(int tryNumber) {
        // デモンストレーション用の推測パターン
        switch (tryNumber) {
            case 1: return 50;
            case 2: return 75;
            case 3: return 62;
            case 4: return 68;
            case 5: return 71;
            case 6: return 69;
            case 7: return 70;
            default: return 50;
        }
    }
    
    /**
     * ヒントを表示します
     */
    private static void displayHint(int guess, int target, int remainingTries) {
        String hint;
        String encouragement = "";
        
        int difference = Math.abs(guess - target);
        
        if (guess < target) {
            hint = "もっと大きい数です";
        } else {
            hint = "もっと小さい数です";
        }
        
        // 詳細なヒント
        if (difference <= 2) {
            encouragement = " (とても近いです！)";
        } else if (difference <= 5) {
            encouragement = " (近いです)";
        } else if (difference <= 10) {
            encouragement = " (少し離れています)";
        } else if (difference <= 20) {
            encouragement = " (離れています)";
        } else {
            encouragement = " (かなり離れています)";
        }
        
        System.out.println("ヒント: " + hint + encouragement + 
                          " (残り" + remainingTries + "回)");
        
        // 特別なヒント
        if (remainingTries == 1) {
            System.out.println("最後のチャンスです！慎重に考えてください。");
        } else if (remainingTries == 2) {
            provideSpecialHint(target);
        }
        
        System.out.println();
    }
    
    /**
     * 特別なヒントを提供します
     */
    private static void provideSpecialHint(int target) {
        System.out.print("特別ヒント: ");
        
        if (target % 2 == 0) {
            System.out.println("答えは偶数です");
        } else {
            System.out.println("答えは奇数です");
        }
        
        // 範囲ヒント
        if (target <= 25) {
            System.out.println("追加ヒント: 答えは25以下です");
        } else if (target <= 50) {
            System.out.println("追加ヒント: 答えは26-50の範囲です");
        } else if (target <= 75) {
            System.out.println("追加ヒント: 答えは51-75の範囲です");
        } else {
            System.out.println("追加ヒント: 答えは76以上です");
        }
    }
    
    /**
     * 勝利メッセージを表示します
     */
    private static void displayWinMessage(int tries, int difficulty, int maxTries) {
        System.out.println("🎉 正解です！ " + tries + "回で当てました！");
        
        // スコア計算
        int baseScore = (difficulty + 1) * 100; // 難易度による基本スコア
        int bonusScore = (maxTries - tries) * 50; // 早く当てたボーナス
        int totalScore = baseScore + bonusScore;
        
        System.out.println();
        System.out.println("=== スコア詳細 ===");
        System.out.println("基本スコア: " + baseScore + "点 (難易度: " + DIFFICULTY_NAMES[difficulty] + ")");
        System.out.println("ボーナス: " + bonusScore + "点 (残り回数: " + (maxTries - tries) + "回)");
        System.out.println("総合スコア: " + totalScore + "点");
        
        // ベストスコア更新チェック
        if (totalScore > bestScore) {
            bestScore = totalScore;
            System.out.println("🏆 ベストスコア更新！");
        }
        
        // パフォーマンス評価
        displayPerformanceEvaluation(tries, maxTries);
        
        System.out.println();
    }
    
    /**
     * パフォーマンス評価を表示します
     */
    private static void displayPerformanceEvaluation(int tries, int maxTries) {
        double performance = (double) tries / maxTries;
        
        System.out.print("評価: ");
        if (performance <= 0.3) {
            System.out.println("🌟 素晴らしい！天才的な推測力です");
        } else if (performance <= 0.5) {
            System.out.println("⭐ とても良い！優秀な成績です");
        } else if (performance <= 0.7) {
            System.out.println("👍 良い！平均以上の成績です");
        } else if (performance <= 0.85) {
            System.out.println("📚 普通！もう少し効率的な推測を心がけましょう");
        } else {
            System.out.println("💪 惜しい！次回はもっと戦略的に攻めましょう");
        }
    }
    
    /**
     * 敗北メッセージを表示します
     */
    private static void displayLoseMessage(int target) {
        System.out.println("😢 残念！正解は " + target + " でした。");
        System.out.println("次回は頑張ってください！");
        System.out.println();
    }
    
    /**
     * 統計を更新します
     */
    private static void updateStatistics(int tries, boolean hasWon, int difficulty, int maxTries) {
        totalGames++;
        
        if (hasWon) {
            totalTries += tries;
            
            // 平均の更新
            double currentAverage = (double) totalTries / totalGames;
            if (currentAverage < bestAverage) {
                bestAverage = currentAverage;
            }
        } else {
            // 負けた場合は最大試行回数を加算
            totalTries += maxTries;
        }
    }
    
    /**
     * 現在の統計を表示します
     */
    private static void displayCurrentStatistics() {
        if (totalGames > 0) {
            double averageTries = (double) totalTries / totalGames;
            
            System.out.println("=== 現在の統計 ===");
            System.out.println("総プレイ回数: " + totalGames + "回");
            System.out.println("平均試行回数: " + String.format("%.1f", averageTries) + "回");
            System.out.println("ベストスコア: " + bestScore + "点");
            
            if (bestAverage != Double.MAX_VALUE) {
                System.out.println("ベスト平均: " + String.format("%.1f", bestAverage) + "回");
            }
            
            System.out.println();
        }
    }
    
    /**
     * 最終統計を表示します
     */
    private static void displayFinalStatistics() {
        System.out.println("=== 最終統計 ===");
        
        if (totalGames == 0) {
            System.out.println("プレイ記録がありません。");
            return;
        }
        
        double averageTries = (double) totalTries / totalGames;
        
        System.out.println("総プレイ回数: " + totalGames + "回");
        System.out.println("総試行回数: " + totalTries + "回");
        System.out.println("平均試行回数: " + String.format("%.2f", averageTries) + "回");
        System.out.println("ベストスコア: " + bestScore + "点");
        
        if (bestAverage != Double.MAX_VALUE) {
            System.out.println("ベスト平均試行回数: " + String.format("%.2f", bestAverage) + "回");
        }
        
        // 総合評価
        System.out.println();
        System.out.print("総合評価: ");
        if (averageTries <= 3.0) {
            System.out.println("🏆 マスター級！数値推測の達人です");
        } else if (averageTries <= 4.0) {
            System.out.println("🥇 エキスパート！とても優秀です");
        } else if (averageTries <= 5.0) {
            System.out.println("🥈 上級者！良い成績です");
        } else if (averageTries <= 6.0) {
            System.out.println("🥉 中級者！平均的な成績です");
        } else {
            System.out.println("📚 初心者！練習すればもっと上達できます");
        }
        
        System.out.println();
    }
    
    /**
     * 再プレイするかを確認します
     */
    private static boolean askPlayAgain() {
        System.out.print("もう一度プレイしますか？ (y/n): ");
        
        // 実際のプログラムでは Scanner を使用
        // ここではデモ用に自動で判定
        boolean playAgain = totalGames < 3; // 3回まで自動プレイ
        
        System.out.println(playAgain ? "y" : "n");
        System.out.println();
        
        return playAgain;
    }
}

/*
 * このゲームの特徴:
 * 
 * 1. 難易度システム:
 *    - 簡単: 1-50, 10回チャンス
 *    - 普通: 1-100, 7回チャンス  
 *    - 難しい: 1-200, 5回チャンス
 * 
 * 2. ヒントシステム:
 *    - 大小の方向指示
 *    - 距離による詳細ヒント
 *    - 特別ヒント（偶数/奇数、範囲）
 * 
 * 3. スコアシステム:
 *    - 基本スコア（難易度による）
 *    - ボーナススコア（早く当てた場合）
 *    - ベストスコア記録
 * 
 * 4. 統計機能:
 *    - 総プレイ回数
 *    - 平均試行回数
 *    - ベストスコア・ベスト平均
 *    - 総合評価
 * 
 * 5. ユーザビリティ:
 *    - 分かりやすいメッセージ
 *    - 励ましとヒント
 *    - パフォーマンス評価
 * 
 * 学習効果:
 * - Math.random() の使い方
 * - 複雑な条件分岐
 * - 静的変数による状態管理
 * - ユーザーインターフェイス設計
 * - ゲームアルゴリズムの理解
 * 
 * 実装上の注意:
 * このサンプルコードは教育目的のため、ユーザー入力を
 * 自動生成していますが、実際のプログラムでは Scanner
 * クラスを使用して実際のユーザー入力を処理します。
 */