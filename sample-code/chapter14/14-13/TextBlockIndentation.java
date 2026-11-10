public class TextBlockIndentation {
    public static void main(String[] args) {
        // インデントの自動処理
        String codeExample = """
                public class Example {
                    public void method() {
                        IO.println("Hello");
                        if (true) {
                            IO.println("Nested");
                        }
                    }
                }
                """;
        
        IO.println("=== インデント自動処理の例 ===");
        IO.println(codeExample);
        
        // 手動でインデントを制御
        String customIndent = """
            レベル1
                レベル2
                    レベル3
            レベル1に戻る
            """;
        
        IO.println("\n=== カスタムインデント ===");
        IO.println(customIndent);
        
        // もっとも左にある行でインデントが決まる
        String mixedIndent = """
                    深いインデント
                中程度のインデント
            浅いインデント
                    再び深いインデント
            """;
        
        IO.println("\n=== 混合インデント（もっとも浅い行が基準） ===");
        IO.println(mixedIndent);
        
        // stripIndent()メソッドでインデント除去を明示的に実行
        String manualStrip = """
                        これは深いインデントです
                            さらに深いインデント
                        元のレベル
                        """.stripIndent();
        
        IO.println("\n=== 手動stripIndent() ===");
        IO.println(manualStrip);
    }
}
