public class TextBlockEscaping {
    public static void main(String[] args) {
        // 引用符のエスケープが不要
        String dialogue = """
                "こんにちは"と彼は言った。
                '時は金なり'という諺があります。
                "引用符をエスケープする必要がない"
                """;
        
        IO.println("=== 引用符のエスケープ不要 ===");
        IO.println(dialogue);
        
        // バックスラッシュの処理
        String windowsPath = """
                Windowsのパス: C:\\Users\\Name\\Documents
                正規表現: \\d+\\.\\d+
                """;
        
        IO.println("\n=== バックスラッシュの処理 ===");
        IO.println(windowsPath);
        
        // 三重引用符を含む場合（エスケープが必要）
        String codeWithTripleQuotes = """
                Text Blockの構文:
                String text = \"""
                    複数行文字列
                    \""";
                """;
        
        IO.println("\n=== 三重引用符のエスケープ ===");
        IO.println(codeWithTripleQuotes);
        
        // 行末の制御
        String trailingSpaces = """
                この行の末尾にスペースがある   \s
                この行は普通の改行
                この行は改行なし\
                """;
        
        IO.println("\n=== 行末の制御 ===");
        IO.println("'" + trailingSpaces + "'");
    }
}
