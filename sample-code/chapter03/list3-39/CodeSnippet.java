/**
 * リスト3-39
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (1987行目)
 */

// 良い例
if (userInput.equals("yes")) {
    // 処理
}

// 悪い例（バグの原因）
if (userInput == "yes") {
    // userInputが動的に生成された場合、falseになる可能性大
}

// null安全な比較
if ("yes".equals(userInput)) {
    // userInputがnullでもNullPointerExceptionを回避
}