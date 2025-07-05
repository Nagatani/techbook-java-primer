package chapter12.solutions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) パターンをRecordで実装
 * 
 * 学習内容：
 * - DTOパターンの実装
 * - データ転送の最適化
 * - ネストしたRecord構造
 * - シリアライゼーション対応
 */
public class DTOExample {
    
    /**
     * ユーザーの基本情報を表すDTO
     */
    public record UserDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String profileImageUrl
    ) {
        
        public UserDTO {
            if (id != null && id <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
            if (username != null && username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (email != null && !isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
        
        /**
         * フルネームを取得
         */
        public String fullName() {
            if (firstName == null && lastName == null) {
                return null;
            }
            if (firstName == null) {
                return lastName;
            }
            if (lastName == null) {
                return firstName;
            }
            return firstName + " " + lastName;
        }
        
        /**
         * 年齢を計算
         */
        public Integer age() {
            if (birthDate == null) {
                return null;
            }
            return LocalDate.now().getYear() - birthDate.getYear();
        }
        
        /**
         * 簡略版のDTOを作成
         */
        public UserSummaryDTO toSummary() {
            return new UserSummaryDTO(id, username, fullName());
        }
        
        private static boolean isValidEmail(String email) {
            return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        }
    }
    
    /**
     * ユーザーの要約情報を表すDTO
     */
    public record UserSummaryDTO(
        Long id,
        String username,
        String displayName
    ) {
        public UserSummaryDTO {
            Objects.requireNonNull(id, "User ID cannot be null");
            Objects.requireNonNull(username, "Username cannot be null");
        }
    }
    
    /**
     * 住所情報を表すDTO
     */
    public record AddressDTO(
        String street,
        String city,
        String state,
        String zipCode,
        String country
    ) {
        
        public AddressDTO {
            if (street != null) street = street.trim();
            if (city != null) city = city.trim();
            if (state != null) state = state.trim();
            if (zipCode != null) zipCode = zipCode.trim();
            if (country != null) country = country.trim();
        }
        
        /**
         * 完全な住所文字列を作成
         */
        public String toFullAddress() {
            StringBuilder sb = new StringBuilder();
            
            if (street != null && !street.isEmpty()) {
                sb.append(street);
            }
            if (city != null && !city.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(city);
            }
            if (state != null && !state.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(state);
            }
            if (zipCode != null && !zipCode.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(zipCode);
            }
            if (country != null && !country.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(country);
            }
            
            return sb.toString();
        }
        
        /**
         * 住所が完全かどうかをチェック
         */
        public boolean isComplete() {
            return street != null && !street.isEmpty() &&
                   city != null && !city.isEmpty() &&
                   state != null && !state.isEmpty() &&
                   zipCode != null && !zipCode.isEmpty() &&
                   country != null && !country.isEmpty();
        }
    }
    
    /**
     * 詳細なユーザー情報を表すDTO（住所情報を含む）
     */
    public record UserDetailDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String profileImageUrl,
        AddressDTO address,
        List<String> phoneNumbers,
        Map<String, String> preferences
    ) {
        
        public UserDetailDTO {
            if (phoneNumbers != null) {
                phoneNumbers = List.copyOf(phoneNumbers);
            }
            if (preferences != null) {
                preferences = Map.copyOf(preferences);
            }
        }
        
        /**
         * 基本情報のDTOに変換
         */
        public UserDTO toBasicUser() {
            return new UserDTO(id, username, email, firstName, lastName, birthDate, profileImageUrl);
        }
        
        /**
         * 住所の完全性をチェック
         */
        public boolean hasCompleteAddress() {
            return address != null && address.isComplete();
        }
        
        /**
         * 電話番号を持っているかチェック
         */
        public boolean hasPhoneNumbers() {
            return phoneNumbers != null && !phoneNumbers.isEmpty();
        }
        
        /**
         * 特定の設定値を取得
         */
        public String getPreference(String key) {
            return preferences != null ? preferences.get(key) : null;
        }
    }
    
    /**
     * 商品情報を表すDTO
     */
    public record ProductDTO(
        Long id,
        String name,
        String description,
        Double price,
        String category,
        String imageUrl,
        Boolean available,
        Map<String, Object> attributes
    ) {
        
        public ProductDTO {
            if (id != null && id <= 0) {
                throw new IllegalArgumentException("Product ID must be positive");
            }
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product price cannot be negative");
            }
            if (attributes != null) {
                attributes = Map.copyOf(attributes);
            }
        }
        
        /**
         * 商品が利用可能かチェック
         */
        public boolean isAvailable() {
            return available != null && available;
        }
        
        /**
         * 価格が設定されているかチェック
         */
        public boolean hasPrice() {
            return price != null && price > 0;
        }
        
        /**
         * 属性値を取得
         */
        public Object getAttribute(String key) {
            return attributes != null ? attributes.get(key) : null;
        }
        
        /**
         * 文字列属性を取得
         */
        public String getStringAttribute(String key) {
            Object value = getAttribute(key);
            return value instanceof String ? (String) value : null;
        }
        
        /**
         * 数値属性を取得
         */
        public Double getNumericAttribute(String key) {
            Object value = getAttribute(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return null;
        }
        
        /**
         * 簡略版のDTOを作成
         */
        public ProductSummaryDTO toSummary() {
            return new ProductSummaryDTO(id, name, price, imageUrl);
        }
    }
    
    /**
     * 商品の要約情報を表すDTO
     */
    public record ProductSummaryDTO(
        Long id,
        String name,
        Double price,
        String imageUrl
    ) {
        public ProductSummaryDTO {
            Objects.requireNonNull(id, "Product ID cannot be null");
            Objects.requireNonNull(name, "Product name cannot be null");
        }
    }
    
    /**
     * 注文項目を表すDTO
     */
    public record OrderItemDTO(
        Long productId,
        String productName,
        Integer quantity,
        Double unitPrice,
        Double totalPrice
    ) {
        
        public OrderItemDTO {
            if (productId != null && productId <= 0) {
                throw new IllegalArgumentException("Product ID must be positive");
            }
            if (quantity != null && quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice != null && unitPrice < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative");
            }
            if (totalPrice != null && totalPrice < 0) {
                throw new IllegalArgumentException("Total price cannot be negative");
            }
        }
        
        /**
         * 商品IDと名前から簡単なコンストラクタ
         */
        public OrderItemDTO(Long productId, String productName, Integer quantity, Double unitPrice) {
            this(productId, productName, quantity, unitPrice, 
                 quantity != null && unitPrice != null ? quantity * unitPrice : null);
        }
        
        /**
         * 総額を計算（nullチェック付き）
         */
        public Double calculateTotalPrice() {
            if (quantity != null && unitPrice != null) {
                return quantity * unitPrice;
            }
            return totalPrice;
        }
    }
    
    /**
     * 注文情報を表すDTO
     */
    public record OrderDTO(
        Long id,
        Long customerId,
        String customerName,
        LocalDateTime orderDate,
        String status,
        List<OrderItemDTO> items,
        Double totalAmount,
        AddressDTO shippingAddress,
        String paymentMethod
    ) {
        
        public OrderDTO {
            if (id != null && id <= 0) {
                throw new IllegalArgumentException("Order ID must be positive");
            }
            if (customerId != null && customerId <= 0) {
                throw new IllegalArgumentException("Customer ID must be positive");
            }
            if (items != null) {
                items = List.copyOf(items);
            }
        }
        
        /**
         * 注文項目数を取得
         */
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }
        
        /**
         * 総数量を計算
         */
        public Integer getTotalQuantity() {
            if (items == null) return null;
            
            return items.stream()
                .filter(item -> item.quantity() != null)
                .mapToInt(OrderItemDTO::quantity)
                .sum();
        }
        
        /**
         * 総額を計算
         */
        public Double calculateTotalAmount() {
            if (items == null) return totalAmount;
            
            return items.stream()
                .filter(item -> item.calculateTotalPrice() != null)
                .mapToDouble(OrderItemDTO::calculateTotalPrice)
                .sum();
        }
        
        /**
         * 特定の商品を含むかチェック
         */
        public boolean containsProduct(Long productId) {
            if (items == null || productId == null) return false;
            
            return items.stream()
                .anyMatch(item -> productId.equals(item.productId()));
        }
        
        /**
         * 注文のステータスをチェック
         */
        public boolean isPending() {
            return "PENDING".equals(status);
        }
        
        public boolean isConfirmed() {
            return "CONFIRMED".equals(status);
        }
        
        public boolean isShipped() {
            return "SHIPPED".equals(status);
        }
        
        public boolean isDelivered() {
            return "DELIVERED".equals(status);
        }
        
        public boolean isCancelled() {
            return "CANCELLED".equals(status);
        }
        
        /**
         * 配送先住所が設定されているかチェック
         */
        public boolean hasShippingAddress() {
            return shippingAddress != null && shippingAddress.isComplete();
        }
        
        /**
         * 簡略版のDTOを作成
         */
        public OrderSummaryDTO toSummary() {
            return new OrderSummaryDTO(id, customerId, customerName, orderDate, status, totalAmount);
        }
    }
    
    /**
     * 注文の要約情報を表すDTO
     */
    public record OrderSummaryDTO(
        Long id,
        Long customerId,
        String customerName,
        LocalDateTime orderDate,
        String status,
        Double totalAmount
    ) {
        public OrderSummaryDTO {
            Objects.requireNonNull(id, "Order ID cannot be null");
            Objects.requireNonNull(customerId, "Customer ID cannot be null");
        }
    }
    
    /**
     * APIレスポンス用の汎用DTO
     */
    public record ApiResponseDTO<T>(
        boolean success,
        String message,
        T data,
        List<String> errors,
        Map<String, Object> metadata
    ) {
        
        public ApiResponseDTO {
            if (errors != null) {
                errors = List.copyOf(errors);
            }
            if (metadata != null) {
                metadata = Map.copyOf(metadata);
            }
        }
        
        /**
         * 成功レスポンスを作成
         */
        public static <T> ApiResponseDTO<T> success(T data) {
            return new ApiResponseDTO<>(true, "Success", data, null, null);
        }
        
        public static <T> ApiResponseDTO<T> success(T data, String message) {
            return new ApiResponseDTO<>(true, message, data, null, null);
        }
        
        /**
         * エラーレスポンスを作成
         */
        public static <T> ApiResponseDTO<T> error(String message) {
            return new ApiResponseDTO<>(false, message, null, null, null);
        }
        
        public static <T> ApiResponseDTO<T> error(String message, List<String> errors) {
            return new ApiResponseDTO<>(false, message, null, errors, null);
        }
        
        /**
         * バリデーションエラーレスポンスを作成
         */
        public static <T> ApiResponseDTO<T> validationError(List<String> errors) {
            return new ApiResponseDTO<>(false, "Validation failed", null, errors, null);
        }
        
        /**
         * エラーがあるかチェック
         */
        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }
        
        /**
         * メタデータを持っているかチェック
         */
        public boolean hasMetadata() {
            return metadata != null && !metadata.isEmpty();
        }
        
        /**
         * メタデータ値を取得
         */
        public Object getMetadata(String key) {
            return metadata != null ? metadata.get(key) : null;
        }
    }
    
    /**
     * ページネーション情報を表すDTO
     */
    public record PageInfoDTO(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
    ) {
        
        public PageInfoDTO {
            if (page < 0) {
                throw new IllegalArgumentException("Page number cannot be negative");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Page size must be positive");
            }
            if (totalElements < 0) {
                throw new IllegalArgumentException("Total elements cannot be negative");
            }
        }
        
        /**
         * ページ情報から作成
         */
        public static PageInfoDTO of(int page, int size, long totalElements) {
            int totalPages = (int) Math.ceil((double) totalElements / size);
            boolean hasNext = page < totalPages - 1;
            boolean hasPrevious = page > 0;
            
            return new PageInfoDTO(page, size, totalElements, totalPages, hasNext, hasPrevious);
        }
        
        /**
         * 次のページ番号を取得
         */
        public Integer nextPage() {
            return hasNext ? page + 1 : null;
        }
        
        /**
         * 前のページ番号を取得
         */
        public Integer previousPage() {
            return hasPrevious ? page - 1 : null;
        }
        
        /**
         * 最初のページかチェック
         */
        public boolean isFirst() {
            return page == 0;
        }
        
        /**
         * 最後のページかチェック
         */
        public boolean isLast() {
            return page == totalPages - 1;
        }
    }
    
    /**
     * ページネーション対応のレスポンスDTO
     */
    public record PagedResponseDTO<T>(
        List<T> content,
        PageInfoDTO pageInfo,
        boolean success,
        String message
    ) {
        
        public PagedResponseDTO {
            if (content != null) {
                content = List.copyOf(content);
            }
        }
        
        /**
         * 成功レスポンスを作成
         */
        public static <T> PagedResponseDTO<T> of(List<T> content, PageInfoDTO pageInfo) {
            return new PagedResponseDTO<>(content, pageInfo, true, "Success");
        }
        
        /**
         * 空のページを作成
         */
        public static <T> PagedResponseDTO<T> empty(int page, int size) {
            PageInfoDTO pageInfo = PageInfoDTO.of(page, size, 0);
            return new PagedResponseDTO<>(List.of(), pageInfo, true, "No content found");
        }
        
        /**
         * コンテンツを持っているかチェック
         */
        public boolean hasContent() {
            return content != null && !content.isEmpty();
        }
        
        /**
         * コンテンツの数を取得
         */
        public int getContentSize() {
            return content != null ? content.size() : 0;
        }
    }
}