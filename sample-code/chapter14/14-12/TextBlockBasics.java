public class TextBlockBasics {
    public static void main(String[] args) {
        // Text Blocksの基本構文
        String textBlock = """
                これは Text Block の例です。
                複数行にわたる文字列を
                簡潔に記述できます。
                """;
        
        System.out.println("=== Text Block の出力 ===");
        System.out.println(textBlock);
        
        // 従来の文字列との比較
        String traditional = "これは Text Block の例です。\n" +
                "複数行にわたる文字列を\n" +
                "簡潔に記述できます。\n";
        
        System.out.println("\n=== 従来の文字列との同等性 ===");
        System.out.println("同じ内容？: " + textBlock.equals(traditional));
        
        // 空のText Block
        String empty = """
                """;
        System.out.println("空のText Block: '" + empty + "'");
        
        // 1行のText Block
        String singleLine = """
                単一行のText Block
                """;
        System.out.println("1行のText Block: " + singleLine);
    }
}
