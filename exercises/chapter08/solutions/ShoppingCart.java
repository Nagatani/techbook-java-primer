package chapter08.solutions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ショッピングカートシステムのクラス
 * 
 * 商品の管理、カートへの追加・削除、合計金額の計算、
 * 割引適用などの機能を提供します。
 */
public class ShoppingCart {
    
    /**
     * 商品を表すクラス
     */
    public static class Product {
        private String id;
        private String name;
        private BigDecimal price;
        private String category;
        private int stockQuantity;
        private boolean isAvailable;
        
        public Product(String id, String name, BigDecimal price, String category, int stockQuantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
            this.stockQuantity = Math.max(0, stockQuantity);
            this.isAvailable = true;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
        public String getCategory() { return category; }
        public int getStockQuantity() { return stockQuantity; }
        public boolean isAvailable() { return isAvailable; }
        
        public void setPrice(BigDecimal price) { this.price = price; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = Math.max(0, stockQuantity); }
        public void setAvailable(boolean available) { this.isAvailable = available; }
        
        public boolean isInStock(int quantity) {
            return isAvailable && stockQuantity >= quantity;
        }
        
        public void reduceStock(int quantity) {
            if (quantity <= stockQuantity) {
                stockQuantity -= quantity;
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Product product = (Product) obj;
            return Objects.equals(id, product.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return String.format("Product{id='%s', name='%s', price=%s, category='%s', stock=%d, available=%s}",
                               id, name, price, category, stockQuantity, isAvailable);
        }
    }
    
    /**
     * カートアイテムを表すクラス
     */
    public static class CartItem {
        private Product product;
        private int quantity;
        private BigDecimal unitPrice;
        
        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = Math.max(1, quantity);
            this.unitPrice = product.getPrice();
        }
        
        // Getters and Setters
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        
        public void setQuantity(int quantity) { this.quantity = Math.max(1, quantity); }
        
        public BigDecimal getTotalPrice() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CartItem cartItem = (CartItem) obj;
            return Objects.equals(product.getId(), cartItem.product.getId());
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(product.getId());
        }
        
        @Override
        public String toString() {
            return String.format("CartItem{product=%s, quantity=%d, unitPrice=%s, totalPrice=%s}",
                               product.getName(), quantity, unitPrice, getTotalPrice());
        }
    }
    
    /**
     * 割引情報を表すクラス
     */
    public static class Discount {
        private String code;
        private String description;
        private DiscountType type;
        private BigDecimal value;
        private BigDecimal minimumAmount;
        private String applicableCategory;
        
        public enum DiscountType {
            PERCENTAGE,    // パーセンテージ割引
            FIXED_AMOUNT   // 固定金額割引
        }
        
        public Discount(String code, String description, DiscountType type, BigDecimal value, BigDecimal minimumAmount) {
            this.code = code;
            this.description = description;
            this.type = type;
            this.value = value;
            this.minimumAmount = minimumAmount != null ? minimumAmount : BigDecimal.ZERO;
        }
        
        public Discount(String code, String description, DiscountType type, BigDecimal value, BigDecimal minimumAmount, String applicableCategory) {
            this(code, description, type, value, minimumAmount);
            this.applicableCategory = applicableCategory;
        }
        
        // Getters
        public String getCode() { return code; }
        public String getDescription() { return description; }
        public DiscountType getType() { return type; }
        public BigDecimal getValue() { return value; }
        public BigDecimal getMinimumAmount() { return minimumAmount; }
        public String getApplicableCategory() { return applicableCategory; }
        
        public BigDecimal calculateDiscount(BigDecimal amount) {
            if (amount.compareTo(minimumAmount) < 0) {
                return BigDecimal.ZERO;
            }
            
            if (type == DiscountType.PERCENTAGE) {
                return amount.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                return value.min(amount);
            }
        }
        
        @Override
        public String toString() {
            return String.format("Discount{code='%s', description='%s', type=%s, value=%s}",
                               code, description, type, value);
        }
    }
    
    // カートアイテムの管理（ProductID -> CartItem）
    private Map<String, CartItem> cartItems;
    
    // 利用可能な商品の管理
    private Map<String, Product> availableProducts;
    
    // 利用可能な割引の管理
    private Map<String, Discount> availableDiscounts;
    
    // 適用された割引
    private Set<String> appliedDiscounts;
    
    /**
     * ShoppingCartのコンストラクタ
     */
    public ShoppingCart() {
        cartItems = new LinkedHashMap<>(); // 追加順序を保持
        availableProducts = new HashMap<>();
        availableDiscounts = new HashMap<>();
        appliedDiscounts = new HashSet<>();
    }
    
    /**
     * 商品を追加する
     * 
     * @param product 追加する商品
     * @return 追加に成功した場合true
     */
    public boolean addProduct(Product product) {
        if (product == null) {
            return false;
        }
        
        availableProducts.put(product.getId(), product);
        return true;
    }
    
    /**
     * 割引を追加する
     * 
     * @param discount 追加する割引
     * @return 追加に成功した場合true
     */
    public boolean addDiscount(Discount discount) {
        if (discount == null) {
            return false;
        }
        
        availableDiscounts.put(discount.getCode(), discount);
        return true;
    }
    
    /**
     * カートに商品を追加する
     * 
     * @param productId 商品ID
     * @param quantity 数量
     * @return 追加に成功した場合true
     */
    public boolean addToCart(String productId, int quantity) {
        Product product = availableProducts.get(productId);
        if (product == null || !product.isInStock(quantity)) {
            return false;
        }
        
        CartItem existingItem = cartItems.get(productId);
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            if (!product.isInStock(newQuantity)) {
                return false;
            }
            existingItem.setQuantity(newQuantity);
        } else {
            cartItems.put(productId, new CartItem(product, quantity));
        }
        
        return true;
    }
    
    /**
     * カートから商品を削除する
     * 
     * @param productId 商品ID
     * @return 削除に成功した場合true
     */
    public boolean removeFromCart(String productId) {
        return cartItems.remove(productId) != null;
    }
    
    /**
     * カート内の商品数量を更新する
     * 
     * @param productId 商品ID
     * @param quantity 新しい数量
     * @return 更新に成功した場合true
     */
    public boolean updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(productId);
        }
        
        CartItem item = cartItems.get(productId);
        if (item == null) {
            return false;
        }
        
        Product product = item.getProduct();
        if (!product.isInStock(quantity)) {
            return false;
        }
        
        item.setQuantity(quantity);
        return true;
    }
    
    /**
     * 割引を適用する
     * 
     * @param discountCode 割引コード
     * @return 適用に成功した場合true
     */
    public boolean applyDiscount(String discountCode) {
        Discount discount = availableDiscounts.get(discountCode);
        if (discount == null) {
            return false;
        }
        
        BigDecimal subtotal = getSubtotal();
        if (subtotal.compareTo(discount.getMinimumAmount()) < 0) {
            return false;
        }
        
        appliedDiscounts.add(discountCode);
        return true;
    }
    
    /**
     * 割引を削除する
     * 
     * @param discountCode 割引コード
     * @return 削除に成功した場合true
     */
    public boolean removeDiscount(String discountCode) {
        return appliedDiscounts.remove(discountCode);
    }
    
    /**
     * 小計を計算する
     * 
     * @return 小計
     */
    public BigDecimal getSubtotal() {
        return cartItems.values().stream()
                       .map(CartItem::getTotalPrice)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 割引額を計算する
     * 
     * @return 割引額
     */
    public BigDecimal getTotalDiscount() {
        BigDecimal subtotal = getSubtotal();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (String code : appliedDiscounts) {
            Discount discount = availableDiscounts.get(code);
            if (discount != null) {
                BigDecimal discountAmount = BigDecimal.ZERO;
                
                if (discount.getApplicableCategory() != null) {
                    // カテゴリ別割引
                    BigDecimal categoryTotal = cartItems.values().stream()
                                                       .filter(item -> discount.getApplicableCategory().equals(item.getProduct().getCategory()))
                                                       .map(CartItem::getTotalPrice)
                                                       .reduce(BigDecimal.ZERO, BigDecimal::add);
                    discountAmount = discount.calculateDiscount(categoryTotal);
                } else {
                    // 全体割引
                    discountAmount = discount.calculateDiscount(subtotal);
                }
                
                totalDiscount = totalDiscount.add(discountAmount);
            }
        }
        
        return totalDiscount;
    }
    
    /**
     * 合計金額を計算する
     * 
     * @return 合計金額
     */
    public BigDecimal getTotal() {
        return getSubtotal().subtract(getTotalDiscount());
    }
    
    /**
     * カートの中身を取得する
     * 
     * @return カートアイテムのリスト
     */
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems.values());
    }
    
    /**
     * カートが空かどうかを判定する
     * 
     * @return 空の場合true
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
    
    /**
     * カートをクリアする
     */
    public void clear() {
        cartItems.clear();
        appliedDiscounts.clear();
    }
    
    /**
     * 商品の総数を取得する
     * 
     * @return 商品の総数
     */
    public int getTotalItemCount() {
        return cartItems.values().stream()
                       .mapToInt(CartItem::getQuantity)
                       .sum();
    }
    
    /**
     * 商品の種類数を取得する
     * 
     * @return 商品の種類数
     */
    public int getUniqueItemCount() {
        return cartItems.size();
    }
    
    /**
     * カテゴリ別の商品を取得する
     * 
     * @param category カテゴリ名
     * @return 該当カテゴリの商品リスト
     */
    public List<Product> getProductsByCategory(String category) {
        return availableProducts.values().stream()
                               .filter(product -> category.equals(product.getCategory()))
                               .collect(Collectors.toList());
    }
    
    /**
     * 適用された割引のリストを取得する
     * 
     * @return 適用された割引のリスト
     */
    public List<Discount> getAppliedDiscounts() {
        return appliedDiscounts.stream()
                              .map(code -> availableDiscounts.get(code))
                              .filter(Objects::nonNull)
                              .collect(Collectors.toList());
    }
    
    /**
     * 詳細な請求書を取得する
     * 
     * @return 請求書の文字列
     */
    public String getInvoice() {
        StringBuilder invoice = new StringBuilder();
        
        invoice.append("========== ショッピングカート ==========\n");
        
        if (isEmpty()) {
            invoice.append("カートは空です。\n");
        } else {
            for (CartItem item : cartItems.values()) {
                invoice.append(String.format("%-20s %2d個 x %8s = %10s\n",
                                           item.getProduct().getName(),
                                           item.getQuantity(),
                                           item.getUnitPrice(),
                                           item.getTotalPrice()));
            }
            
            invoice.append("----------------------------------------\n");
            invoice.append(String.format("小計: %32s\n", getSubtotal()));
            
            if (!appliedDiscounts.isEmpty()) {
                for (Discount discount : getAppliedDiscounts()) {
                    invoice.append(String.format("割引 (%s): %24s\n", discount.getDescription(), getTotalDiscount()));
                }
            }
            
            invoice.append("========================================\n");
            invoice.append(String.format("合計: %32s\n", getTotal()));
        }
        
        return invoice.toString();
    }
    
    // Getter methods
    public Product getProduct(String productId) { return availableProducts.get(productId); }
    public Discount getDiscount(String discountCode) { return availableDiscounts.get(discountCode); }
    public CartItem getCartItem(String productId) { return cartItems.get(productId); }
    public Collection<Product> getAllProducts() { return availableProducts.values(); }
    public Collection<Discount> getAllDiscounts() { return availableDiscounts.values(); }
}