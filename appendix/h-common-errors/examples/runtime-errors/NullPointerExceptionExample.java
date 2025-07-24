package examples.runtime;

/**
 * NullPointerExceptionの実例と解決方法を示すサンプルコード
 */
public class NullPointerExceptionExample {
    
    // エラーを発生させる例
    static class ErrorExample {
        private String message;
        
        public void printMessage() {
            // messageが初期化されていないためNullPointerException
            System.out.println(message.length());
        }
        
        public static void demonstrateError() {
            System.out.println("=== NullPointerException発生例 ===");
            try {
                ErrorExample example = new ErrorExample();
                example.printMessage();
            } catch (NullPointerException e) {
                System.err.println("エラー発生: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // 解決策1: デフォルト値で初期化
    static class Solution1 {
        private String message = "";  // 空文字列で初期化
        
        public void printMessage() {
            System.out.println("メッセージの長さ: " + message.length());
        }
        
        public static void demonstrate() {
            System.out.println("\n=== 解決策1: デフォルト値で初期化 ===");
            Solution1 example = new Solution1();
            example.printMessage();
        }
    }
    
    // 解決策2: nullチェック
    static class Solution2 {
        private String message;
        
        public void printMessage() {
            if (message != null) {
                System.out.println("メッセージの長さ: " + message.length());
            } else {
                System.out.println("メッセージが設定されていません");
            }
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public static void demonstrate() {
            System.out.println("\n=== 解決策2: nullチェック ===");
            Solution2 example = new Solution2();
            example.printMessage();  // nullの場合
            
            example.setMessage("Hello, World!");
            example.printMessage();  // 値が設定された場合
        }
    }
    
    // 解決策3: Optionalの使用
    static class Solution3 {
        private java.util.Optional<String> message = java.util.Optional.empty();
        
        public void printMessage() {
            message.ifPresentOrElse(
                msg -> System.out.println("メッセージの長さ: " + msg.length()),
                () -> System.out.println("メッセージが設定されていません")
            );
        }
        
        public void setMessage(String message) {
            this.message = java.util.Optional.ofNullable(message);
        }
        
        public static void demonstrate() {
            System.out.println("\n=== 解決策3: Optionalの使用 ===");
            Solution3 example = new Solution3();
            example.printMessage();  // 空のOptional
            
            example.setMessage("Optional Example");
            example.printMessage();  // 値が存在する場合
            
            example.setMessage(null);
            example.printMessage();  // nullをセットした場合
        }
    }
    
    // メソッドチェーンでのNullPointerException
    static class ChainExample {
        static class Address {
            private String city;
            
            public String getCity() {
                return city;
            }
            
            public void setCity(String city) {
                this.city = city;
            }
        }
        
        static class Person {
            private Address address;
            
            public Address getAddress() {
                return address;
            }
            
            public void setAddress(Address address) {
                this.address = address;
            }
        }
        
        public static void demonstrateChainError() {
            System.out.println("\n=== メソッドチェーンでのエラー ===");
            try {
                Person person = new Person();
                // addressがnullなのでNullPointerException
                String city = person.getAddress().getCity();
                System.out.println("都市: " + city);
            } catch (NullPointerException e) {
                System.err.println("チェーン中でエラー発生");
                e.printStackTrace();
            }
        }
        
        public static void demonstrateChainSolution() {
            System.out.println("\n=== メソッドチェーンの安全な処理 ===");
            Person person = new Person();
            
            // 段階的なnullチェック
            if (person.getAddress() != null && person.getAddress().getCity() != null) {
                System.out.println("都市: " + person.getAddress().getCity());
            } else {
                System.out.println("住所情報が不完全です");
            }
            
            // Optionalを使った方法
            java.util.Optional.ofNullable(person)
                .map(Person::getAddress)
                .map(Address::getCity)
                .ifPresentOrElse(
                    city -> System.out.println("都市（Optional）: " + city),
                    () -> System.out.println("住所情報が不完全です（Optional）")
                );
        }
    }
    
    public static void main(String[] args) {
        System.out.println("NullPointerException サンプルプログラム");
        System.out.println("=====================================");
        
        // エラー例の実行
        ErrorExample.demonstrateError();
        
        // 各解決策の実行
        Solution1.demonstrate();
        Solution2.demonstrate();
        Solution3.demonstrate();
        
        // メソッドチェーンの例
        ChainExample.demonstrateChainError();
        ChainExample.demonstrateChainSolution();
    }
}