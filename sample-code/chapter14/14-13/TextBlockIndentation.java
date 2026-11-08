public class TextBlockIndentation {
    public static void main(String[] args) {
        // インデントの自動処理
        String codeExample = """
                public class Example {
                    public void method() {
                        System.out.println("Hello");
                        if (true) {
                            System.out.println("Nested");
                        }
                    }
                }
                """;
        
        System.out.println("=== インデント自動処理の例 ===");
        System.out.println(codeExample);
        
        // 手動でインデントを制御
        String customIndent = """
            レベル1
                レベル2
                    レベル3
            レベル1に戻る
            """;
        
        System.out.println("\n=== カスタムインデント ===");
        System.out.println(customIndent);
        
        // もっとも左にある行でインデントが決まる
        String mixedIndent = """
                    深いインデント
                中程度のインデント
            浅いインデント
                    再び深いインデント
            """;
        
        System.out.println("\n=== 混合インデント（もっとも浅い行が基準） ===");
        System.out.println(mixedIndent);
        
        // stripIndent()メソッドでインデント除去を明示的に実行
        String manualStrip = """
                        これは深いインデントです
                            さらに深いインデント
                        元のレベル
                        """.stripIndent();
        
        System.out.println("\n=== 手動stripIndent() ===");
        System.out.println(manualStrip);
    }
}
