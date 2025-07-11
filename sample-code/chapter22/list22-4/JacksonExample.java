/**
 * リスト22-4
 * JacksonExampleクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (403行目)
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;

public class JacksonExample {
    
    // データモデルクラス
    static class Product {
        @JsonProperty("product_id")
        private int id;
        
        private String name;
        private double price;
        
        @JsonIgnore  // JSONに含めない
        private String internalCode;
        
        private Map<String, String> attributes;
        
        // コンストラクタ、ゲッター、セッター
        public Product() {}
        
        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.attributes = new HashMap<>();
        }
        
        // ゲッター省略
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // 1. オブジェクトからJSONへ
        Product product = new Product(1, "ノートPC", 98000.0);
        String json = mapper.writeValueAsString(product);
        System.out.println("JSON: " + json);
        
        // 2. 整形されたJSON出力
        String prettyJson = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(product);
        System.out.println("\nPretty JSON:\n" + prettyJson);
        
        // 3. JSONからオブジェクトへ
        String jsonInput = """
            {
                "product_id": 2,
                "name": "マウス",
                "price": 2980.0,
                "attributes": {
                    "color": "black",
                    "wireless": "true"
                }
            }
            """;
        
        Product loaded = mapper.readValue(jsonInput, Product.class);
        System.out.println("\nLoaded: " + loaded.getName());
        
        // 4. コレクションの処理
        List<Product> products = Arrays.asList(
            new Product(1, "キーボード", 5980.0),
            new Product(2, "モニター", 25800.0)
        );
        
        String productsJson = mapper.writeValueAsString(products);
        
        // TypeReferenceを使った複雑な型のデシリアライズ
        List<Product> loadedProducts = mapper.readValue(productsJson, 
            new TypeReference<List<Product>>() {});
        
        System.out.println("\nLoaded products count: " + loadedProducts.size());
    }
}