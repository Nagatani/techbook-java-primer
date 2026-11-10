public class TextBlockBasics {
    public static void main(String[] args) {
        // Text Blocksの基本構文
        String textBlock = """
                これは Text Block の例です。
                複数行にわたる文字列を
                簡潔に記述できます。
                """;
        
        IO.println("=== Text Block の出力 ===");
        IO.println(textBlock);
        
        // 従来の文字列との比較
        String traditional = "これは Text Block の例です。\n" +
                "複数行にわたる文字列を\n" +
                "簡潔に記述できます。\n";
        
        IO.println("\n=== 従来の文字列との同等性 ===");
        IO.println("同じ内容？: " + textBlock.equals(traditional));
        
        // 空のText Block
        String empty = """
                """;
        IO.println("空のText Block: '" + empty + "'");
        
        // 1行のText Block
        String singleLine = """
                単一行のText Block
                """;
        IO.println("1行のText Block: " + singleLine);
    }
}
