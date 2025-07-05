import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * CustomExceptionクラスのテストクラス
 */
public class CustomExceptionTest {
    
    @Test
    public void testBasicConstructor() {
        CustomException ex = new CustomException(CustomException.ErrorCode.INVALID_INPUT);
        
        assertEquals(CustomException.ErrorCode.INVALID_INPUT, ex.getErrorCode());
        assertEquals("無効な入力です", ex.getMessage());
        assertNull(ex.getDetails());
        assertNull(ex.getCause());
        assertTrue(ex.getTimestamp() > 0);
    }
    
    @Test
    public void testConstructorWithDetails() {
        String details = "ユーザー名が空です";
        CustomException ex = new CustomException(
            CustomException.ErrorCode.INVALID_INPUT, 
            details
        );
        
        assertEquals(CustomException.ErrorCode.INVALID_INPUT, ex.getErrorCode());
        assertEquals("無効な入力です: " + details, ex.getMessage());
        assertEquals(details, ex.getDetails());
    }
    
    @Test
    public void testConstructorWithCause() {
        IllegalArgumentException cause = new IllegalArgumentException("Invalid argument");
        CustomException ex = new CustomException(
            CustomException.ErrorCode.SYSTEM_ERROR,
            cause
        );
        
        assertEquals(CustomException.ErrorCode.SYSTEM_ERROR, ex.getErrorCode());
        assertEquals("システムエラーが発生しました", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    public void testConstructorWithDetailsAndCause() {
        String details = "データベース接続エラー";
        Exception cause = new Exception("Connection failed");
        CustomException ex = new CustomException(
            CustomException.ErrorCode.SYSTEM_ERROR,
            details,
            cause
        );
        
        assertEquals(CustomException.ErrorCode.SYSTEM_ERROR, ex.getErrorCode());
        assertEquals("システムエラーが発生しました: " + details, ex.getMessage());
        assertEquals(details, ex.getDetails());
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    public void testIsErrorType() {
        CustomException ex = new CustomException(CustomException.ErrorCode.DATA_NOT_FOUND);
        
        assertTrue(ex.isErrorType(CustomException.ErrorCode.DATA_NOT_FOUND));
        assertFalse(ex.isErrorType(CustomException.ErrorCode.INVALID_INPUT));
        assertFalse(ex.isErrorType(CustomException.ErrorCode.SYSTEM_ERROR));
    }
    
    @Test
    public void testIsSystemError() {
        CustomException systemError = new CustomException(CustomException.ErrorCode.SYSTEM_ERROR);
        CustomException resourceError = new CustomException(CustomException.ErrorCode.RESOURCE_EXHAUSTED);
        CustomException userError = new CustomException(CustomException.ErrorCode.INVALID_INPUT);
        
        assertTrue(systemError.isSystemError());
        assertTrue(resourceError.isSystemError());
        assertFalse(userError.isSystemError());
    }
    
    @Test
    public void testIsUserError() {
        CustomException invalidInput = new CustomException(CustomException.ErrorCode.INVALID_INPUT);
        CustomException dataNotFound = new CustomException(CustomException.ErrorCode.DATA_NOT_FOUND);
        CustomException permissionDenied = new CustomException(CustomException.ErrorCode.PERMISSION_DENIED);
        CustomException systemError = new CustomException(CustomException.ErrorCode.SYSTEM_ERROR);
        
        assertTrue(invalidInput.isUserError());
        assertTrue(dataNotFound.isUserError());
        assertTrue(permissionDenied.isUserError());
        assertFalse(systemError.isUserError());
    }
    
    @Test
    public void testGetDetailedMessage() {
        CustomException ex = new CustomException(
            CustomException.ErrorCode.INVALID_INPUT,
            "詳細情報"
        );
        
        String detailedMessage = ex.getDetailedMessage();
        assertNotNull(detailedMessage);
        assertTrue(detailedMessage.contains("E001"));
        assertTrue(detailedMessage.contains("無効な入力です"));
        assertTrue(detailedMessage.contains("詳細情報"));
        assertTrue(detailedMessage.contains("発生時刻"));
    }
    
    @Test
    public void testGetDetailedMessageWithCause() {
        Exception cause = new RuntimeException("Root cause");
        CustomException ex = new CustomException(
            CustomException.ErrorCode.SYSTEM_ERROR,
            "詳細情報",
            cause
        );
        
        String detailedMessage = ex.getDetailedMessage();
        assertTrue(detailedMessage.contains("原因: RuntimeException"));
        assertTrue(detailedMessage.contains("Root cause"));
    }
    
    @Test
    public void testFindCauseByType() {
        IllegalArgumentException rootCause = new IllegalArgumentException("Root cause");
        RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
        CustomException ex = new CustomException(
            CustomException.ErrorCode.SYSTEM_ERROR,
            intermediateCause
        );
        
        // 存在する例外を検索
        IllegalArgumentException found = ex.findCauseByType(IllegalArgumentException.class);
        assertNotNull(found);
        assertEquals(rootCause, found);
        
        // 存在しない例外を検索
        NumberFormatException notFound = ex.findCauseByType(NumberFormatException.class);
        assertNull(notFound);
        
        // 自分自身の型を検索
        CustomException self = ex.findCauseByType(CustomException.class);
        assertNotNull(self);
        assertEquals(ex, self);
    }
    
    @Test
    public void testGetSeverity() {
        // 重要度1（低）
        CustomException lowSeverity1 = new CustomException(CustomException.ErrorCode.INVALID_INPUT);
        CustomException lowSeverity2 = new CustomException(CustomException.ErrorCode.DATA_NOT_FOUND);
        assertEquals(1, lowSeverity1.getSeverity());
        assertEquals(1, lowSeverity2.getSeverity());
        
        // 重要度2（中）
        CustomException mediumSeverity = new CustomException(CustomException.ErrorCode.PERMISSION_DENIED);
        assertEquals(2, mediumSeverity.getSeverity());
        
        // 重要度3（高）
        CustomException highSeverity1 = new CustomException(CustomException.ErrorCode.SYSTEM_ERROR);
        CustomException highSeverity2 = new CustomException(CustomException.ErrorCode.RESOURCE_EXHAUSTED);
        assertEquals(3, highSeverity1.getSeverity());
        assertEquals(3, highSeverity2.getSeverity());
    }
    
    @Test
    public void testErrorCodeProperties() {
        CustomException.ErrorCode errorCode = CustomException.ErrorCode.INVALID_INPUT;
        assertEquals("E001", errorCode.getCode());
        assertEquals("無効な入力です", errorCode.getMessage());
        
        CustomException.ErrorCode systemError = CustomException.ErrorCode.SYSTEM_ERROR;
        assertEquals("E004", systemError.getCode());
        assertEquals("システムエラーが発生しました", systemError.getMessage());
    }
    
    @Test
    public void testTimestampIncrement() throws InterruptedException {
        CustomException ex1 = new CustomException(CustomException.ErrorCode.INVALID_INPUT);
        Thread.sleep(1); // 1ms待機
        CustomException ex2 = new CustomException(CustomException.ErrorCode.INVALID_INPUT);
        
        assertTrue(ex2.getTimestamp() > ex1.getTimestamp());
    }
}