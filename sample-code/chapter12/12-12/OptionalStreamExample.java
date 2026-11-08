import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptionalStreamExample {
    static class Product {
        private String name;
        private double price;
        
        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("Laptop", 1200.0),
            new Product("Mouse", 25.0),
            new Product("Keyboard", 75.0),
            new Product("Monitor", 300.0)
        );
        
        // findFirst() - 最初の要素をOptionalで返す
        Optional<Product> firstExpensive = products.stream()
            .filter(p -> p.getPrice() > 100)
            .findFirst();
        
        firstExpensive.ifPresent(p -> 
            System.out.println("最初の高額商品: " + p.getName()));
        
        // findAny() - 任意の要素をOptionalで返す（並列処理に最適）
        Optional<Product> anyProduct = products.parallelStream()
            .filter(p -> p.getPrice() < 50)
            .findAny();
        
        System.out.println("安価な商品: " + 
            anyProduct.map(Product::getName).orElse("なし"));
        
        // max/min - 最大値・最小値をOptionalで返す
        Optional<Product> mostExpensive = products.stream()
            .max((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        
        mostExpensive.ifPresent(p -> 
            System.out.println("最高額商品: " + p.getName() + " ($" + p.getPrice() + ")"));
        
        // OptionalをStreamに変換（Java 9以降）
        List<String> expensiveProductNames = products.stream()
            .map(p -> p.getPrice() > 500 ? Optional.of(p.getName()) : Optional.<String>empty())
            .flatMap(Optional::stream)  // Optional -> Stream変換
            .collect(Collectors.toList());
        
        System.out.println("高額商品リスト: " + expensiveProductNames);
    }
}
