package chapter12.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTOExampleクラスのテストクラス
 */
public class DTOExampleTest {
    
    @Test
    void testUserDTO() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        DTOExample.UserDTO user = new DTOExample.UserDTO(
            1L, "johndoe", "john@example.com", "John", "Doe", birthDate, "profile.jpg"
        );
        
        assertEquals(1L, user.id());
        assertEquals("johndoe", user.username());
        assertEquals("john@example.com", user.email());
        assertEquals("John Doe", user.fullName());
        
        int expectedAge = LocalDate.now().getYear() - 1990;
        assertEquals(expectedAge, user.age());
    }
    
    @Test
    void testUserDTOValidation() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        
        // 無効なID
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.UserDTO(-1L, "user", "user@example.com", "First", "Last", birthDate, null));
        
        // 空のユーザー名
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.UserDTO(1L, "", "user@example.com", "First", "Last", birthDate, null));
        
        // 無効なメールアドレス
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.UserDTO(1L, "user", "invalid-email", "First", "Last", birthDate, null));
    }
    
    @Test
    void testUserDTONullHandling() {
        DTOExample.UserDTO user = new DTOExample.UserDTO(
            1L, "user", "user@example.com", null, "Last", null, null
        );
        
        assertEquals("Last", user.fullName());
        assertNull(user.age());
        
        DTOExample.UserDTO userWithFirstName = new DTOExample.UserDTO(
            1L, "user", "user@example.com", "First", null, null, null
        );
        
        assertEquals("First", userWithFirstName.fullName());
        
        DTOExample.UserDTO userWithoutNames = new DTOExample.UserDTO(
            1L, "user", "user@example.com", null, null, null, null
        );
        
        assertNull(userWithoutNames.fullName());
    }
    
    @Test
    void testUserSummaryDTO() {
        DTOExample.UserSummaryDTO summary = new DTOExample.UserSummaryDTO(1L, "johndoe", "John Doe");
        
        assertEquals(1L, summary.id());
        assertEquals("johndoe", summary.username());
        assertEquals("John Doe", summary.displayName());
        
        // null validation
        assertThrows(NullPointerException.class, () -> 
            new DTOExample.UserSummaryDTO(null, "user", "Name"));
        
        assertThrows(NullPointerException.class, () -> 
            new DTOExample.UserSummaryDTO(1L, null, "Name"));
    }
    
    @Test
    void testAddressDTO() {
        DTOExample.AddressDTO address = new DTOExample.AddressDTO(
            "123 Main St", "Springfield", "IL", "62701", "USA"
        );
        
        assertEquals("123 Main St", address.street());
        assertEquals("Springfield", address.city());
        assertEquals("IL", address.state());
        assertEquals("62701", address.zipCode());
        assertEquals("USA", address.country());
        
        assertTrue(address.isComplete());
        assertEquals("123 Main St, Springfield, IL 62701, USA", address.toFullAddress());
    }
    
    @Test
    void testAddressDTOIncomplete() {
        DTOExample.AddressDTO incompleteAddress = new DTOExample.AddressDTO(
            "123 Main St", "Springfield", null, "62701", "USA"
        );
        
        assertFalse(incompleteAddress.isComplete());
        assertEquals("123 Main St, Springfield 62701, USA", incompleteAddress.toFullAddress());
    }
    
    @Test
    void testAddressDTOTrimming() {
        DTOExample.AddressDTO address = new DTOExample.AddressDTO(
            "  123 Main St  ", "  Springfield  ", "  IL  ", "  62701  ", "  USA  "
        );
        
        assertEquals("123 Main St", address.street());
        assertEquals("Springfield", address.city());
        assertEquals("IL", address.state());
        assertEquals("62701", address.zipCode());
        assertEquals("USA", address.country());
    }
    
    @Test
    void testUserDetailDTO() {
        DTOExample.AddressDTO address = new DTOExample.AddressDTO(
            "123 Main St", "Springfield", "IL", "62701", "USA"
        );
        
        DTOExample.UserDetailDTO userDetail = new DTOExample.UserDetailDTO(
            1L, "johndoe", "john@example.com", "John", "Doe", 
            LocalDate.of(1990, 5, 15), "profile.jpg", address,
            List.of("123-456-7890", "098-765-4321"),
            Map.of("theme", "dark", "language", "en")
        );
        
        assertTrue(userDetail.hasCompleteAddress());
        assertTrue(userDetail.hasPhoneNumbers());
        assertEquals("dark", userDetail.getPreference("theme"));
        assertEquals("en", userDetail.getPreference("language"));
        assertNull(userDetail.getPreference("nonexistent"));
        
        DTOExample.UserDTO basicUser = userDetail.toBasicUser();
        assertEquals(1L, basicUser.id());
        assertEquals("johndoe", basicUser.username());
        assertEquals("john@example.com", basicUser.email());
    }
    
    @Test
    void testProductDTO() {
        DTOExample.ProductDTO product = new DTOExample.ProductDTO(
            1L, "Laptop", "High-performance laptop", 999.99, "Electronics", 
            "laptop.jpg", true, Map.of("brand", "TechCorp", "weight", 2.5)
        );
        
        assertEquals(1L, product.id());
        assertEquals("Laptop", product.name());
        assertEquals(999.99, product.price());
        assertTrue(product.isAvailable());
        assertTrue(product.hasPrice());
        assertEquals("TechCorp", product.getStringAttribute("brand"));
        assertEquals(2.5, product.getNumericAttribute("weight"));
        
        DTOExample.ProductSummaryDTO summary = product.toSummary();
        assertEquals(1L, summary.id());
        assertEquals("Laptop", summary.name());
        assertEquals(999.99, summary.price());
    }
    
    @Test
    void testProductDTOValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.ProductDTO(-1L, "Product", "Description", 100.0, "Category", 
                                     "image.jpg", true, null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.ProductDTO(1L, "Product", "Description", -10.0, "Category", 
                                     "image.jpg", true, null));
    }
    
    @Test
    void testProductDTOAttributes() {
        DTOExample.ProductDTO product = new DTOExample.ProductDTO(
            1L, "Test", "Description", 100.0, "Category", null, true,
            Map.of("stringAttr", "value", "numberAttr", 42, "boolAttr", true)
        );
        
        assertEquals("value", product.getStringAttribute("stringAttr"));
        assertEquals(42.0, product.getNumericAttribute("numberAttr"));
        assertNull(product.getStringAttribute("numberAttr")); // 型が違う
        assertNull(product.getNumericAttribute("stringAttr")); // 型が違う
        assertNull(product.getAttribute("nonexistent"));
    }
    
    @Test
    void testOrderItemDTO() {
        DTOExample.OrderItemDTO item = new DTOExample.OrderItemDTO(1L, "Laptop", 2, 999.99);
        
        assertEquals(1L, item.productId());
        assertEquals("Laptop", item.productName());
        assertEquals(2, item.quantity());
        assertEquals(999.99, item.unitPrice());
        assertEquals(1999.98, item.totalPrice(), 0.001);
        assertEquals(1999.98, item.calculateTotalPrice(), 0.001);
    }
    
    @Test
    void testOrderItemDTOValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.OrderItemDTO(-1L, "Product", 1, 100.0, 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.OrderItemDTO(1L, "Product", 0, 100.0, 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.OrderItemDTO(1L, "Product", 1, -10.0, 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.OrderItemDTO(1L, "Product", 1, 100.0, -10.0));
    }
    
    @Test
    void testOrderDTO() {
        DTOExample.AddressDTO address = new DTOExample.AddressDTO(
            "123 Main St", "Springfield", "IL", "62701", "USA"
        );
        
        List<DTOExample.OrderItemDTO> items = List.of(
            new DTOExample.OrderItemDTO(1L, "Laptop", 1, 999.99),
            new DTOExample.OrderItemDTO(2L, "Mouse", 2, 29.99)
        );
        
        DTOExample.OrderDTO order = new DTOExample.OrderDTO(
            1L, 100L, "John Doe", LocalDateTime.now(), "PENDING", 
            items, 1059.97, address, "CREDIT_CARD"
        );
        
        assertEquals(1L, order.id());
        assertEquals(100L, order.customerId());
        assertEquals("John Doe", order.customerName());
        assertEquals("PENDING", order.status());
        assertEquals(2, order.getItemCount());
        assertEquals(3, order.getTotalQuantity());
        assertEquals(1059.97, order.calculateTotalAmount(), 0.001);
        assertTrue(order.isPending());
        assertFalse(order.isConfirmed());
        assertTrue(order.hasShippingAddress());
        assertTrue(order.containsProduct(1L));
        assertFalse(order.containsProduct(999L));
        
        DTOExample.OrderSummaryDTO summary = order.toSummary();
        assertEquals(1L, summary.id());
        assertEquals(100L, summary.customerId());
        assertEquals("John Doe", summary.customerName());
    }
    
    @Test
    void testOrderDTOValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.OrderDTO(-1L, 1L, "Customer", LocalDateTime.now(), "PENDING", 
                                   List.of(), 0.0, null, "CARD"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DTOExample.OrderDTO(1L, -1L, "Customer", LocalDateTime.now(), "PENDING", 
                                   List.of(), 0.0, null, "CARD"));
    }
    
    @Test
    void testOrderDTOStatusChecks() {
        DTOExample.OrderDTO order = new DTOExample.OrderDTO(
            1L, 1L, "Customer", LocalDateTime.now(), "CONFIRMED", 
            List.of(), 0.0, null, "CARD"
        );
        
        assertFalse(order.isPending());
        assertTrue(order.isConfirmed());
        assertFalse(order.isShipped());
        assertFalse(order.isDelivered());
        assertFalse(order.isCancelled());
    }
    
    @Test
    void testApiResponseDTO() {
        // 成功レスポンス
        DTOExample.ApiResponseDTO<String> successResponse = 
            DTOExample.ApiResponseDTO.success("Hello World");
        
        assertTrue(successResponse.success());
        assertEquals("Success", successResponse.message());
        assertEquals("Hello World", successResponse.data());
        assertFalse(successResponse.hasErrors());
        
        // エラーレスポンス
        DTOExample.ApiResponseDTO<String> errorResponse = 
            DTOExample.ApiResponseDTO.error("Something went wrong");
        
        assertFalse(errorResponse.success());
        assertEquals("Something went wrong", errorResponse.message());
        assertNull(errorResponse.data());
        
        // バリデーションエラー
        List<String> errors = List.of("Field1 is required", "Field2 is invalid");
        DTOExample.ApiResponseDTO<String> validationResponse = 
            DTOExample.ApiResponseDTO.validationError(errors);
        
        assertFalse(validationResponse.success());
        assertEquals("Validation failed", validationResponse.message());
        assertTrue(validationResponse.hasErrors());
        assertEquals(2, validationResponse.errors().size());
    }
    
    @Test
    void testApiResponseDTOWithMetadata() {
        DTOExample.ApiResponseDTO<String> response = new DTOExample.ApiResponseDTO<>(
            true, "Success", "data", null, Map.of("timestamp", "2023-01-01", "version", "1.0")
        );
        
        assertTrue(response.hasMetadata());
        assertEquals("2023-01-01", response.getMetadata("timestamp"));
        assertEquals("1.0", response.getMetadata("version"));
        assertNull(response.getMetadata("nonexistent"));
    }
    
    @Test
    void testPageInfoDTO() {
        DTOExample.PageInfoDTO pageInfo = DTOExample.PageInfoDTO.of(1, 10, 25);
        
        assertEquals(1, pageInfo.page());
        assertEquals(10, pageInfo.size());
        assertEquals(25, pageInfo.totalElements());
        assertEquals(3, pageInfo.totalPages());
        assertTrue(pageInfo.hasNext());
        assertTrue(pageInfo.hasPrevious());
        assertFalse(pageInfo.isFirst());
        assertFalse(pageInfo.isLast());
        assertEquals(2, pageInfo.nextPage());
        assertEquals(0, pageInfo.previousPage());
        
        // 最初のページ
        DTOExample.PageInfoDTO firstPage = DTOExample.PageInfoDTO.of(0, 10, 25);
        assertTrue(firstPage.isFirst());
        assertFalse(firstPage.hasPrevious());
        assertNull(firstPage.previousPage());
        
        // 最後のページ
        DTOExample.PageInfoDTO lastPage = DTOExample.PageInfoDTO.of(2, 10, 25);
        assertTrue(lastPage.isLast());
        assertFalse(lastPage.hasNext());
        assertNull(lastPage.nextPage());
    }
    
    @Test
    void testPageInfoDTOValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            DTOExample.PageInfoDTO.of(-1, 10, 25));
        
        assertThrows(IllegalArgumentException.class, () -> 
            DTOExample.PageInfoDTO.of(0, 0, 25));
        
        assertThrows(IllegalArgumentException.class, () -> 
            DTOExample.PageInfoDTO.of(0, 10, -1));
    }
    
    @Test
    void testPagedResponseDTO() {
        List<String> content = List.of("item1", "item2", "item3");
        DTOExample.PageInfoDTO pageInfo = DTOExample.PageInfoDTO.of(0, 10, 3);
        DTOExample.PagedResponseDTO<String> response = DTOExample.PagedResponseDTO.of(content, pageInfo);
        
        assertTrue(response.success());
        assertEquals("Success", response.message());
        assertTrue(response.hasContent());
        assertEquals(3, response.getContentSize());
        assertEquals(content, response.content());
        assertEquals(pageInfo, response.pageInfo());
        
        // 空のページ
        DTOExample.PagedResponseDTO<String> emptyResponse = DTOExample.PagedResponseDTO.empty(0, 10);
        assertTrue(emptyResponse.success());
        assertEquals("No content found", emptyResponse.message());
        assertFalse(emptyResponse.hasContent());
        assertEquals(0, emptyResponse.getContentSize());
    }
    
    @Test
    void testDTOImmutability() {
        List<String> phoneNumbers = new java.util.ArrayList<>();
        phoneNumbers.add("123-456-7890");
        Map<String, String> preferences = new java.util.HashMap<>();
        preferences.put("theme", "dark");
        
        DTOExample.UserDetailDTO userDetail = new DTOExample.UserDetailDTO(
            1L, "user", "user@example.com", "First", "Last", 
            LocalDate.now(), null, null, phoneNumbers, preferences
        );
        
        // 元のコレクションを変更してもDTOは影響を受けない
        phoneNumbers.add("098-765-4321");
        preferences.put("language", "en");
        
        assertEquals(1, userDetail.phoneNumbers().size());
        assertEquals(1, userDetail.preferences().size());
        
        // 返されたコレクションは変更できない
        assertThrows(UnsupportedOperationException.class, () -> 
            userDetail.phoneNumbers().add("555-123-4567"));
        assertThrows(UnsupportedOperationException.class, () -> 
            userDetail.preferences().put("new", "value"));
    }
}