import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;

/**
 * ExceptionChainクラスのテストクラス
 */
public class ExceptionChainTest {
    
    @Test
    public void testDataAccessLayerException() {
        ExceptionChain.DataAccessException exception = assertThrows(
            ExceptionChain.DataAccessException.class,
            () -> ExceptionChain.dataAccessLayer()
        );
        
        assertEquals("primary_database", exception.getDataSource());
        assertEquals("SELECT", exception.getOperation());
        assertTrue(exception.getMessage().contains("ユーザーデータの取得に失敗"));
        
        // 原因例外がSQLExceptionであることを確認
        assertInstanceOf(SQLException.class, exception.getCause());
    }
    
    @Test
    public void testConfigurationLayerException() {
        ExceptionChain.ConfigurationException exception = assertThrows(
            ExceptionChain.ConfigurationException.class,
            () -> ExceptionChain.configurationLayer()
        );
        
        assertEquals("database.url", exception.getConfigKey());
        assertNull(exception.getConfigValue());
        assertTrue(exception.getMessage().contains("データベース設定の読み込みに失敗"));
        
        // 原因例外がIOExceptionであることを確認
        assertInstanceOf(IOException.class, exception.getCause());
    }
    
    @Test
    public void testBusinessLogicLayerException() {
        ExceptionChain.BusinessLogicException exception = assertThrows(
            ExceptionChain.BusinessLogicException.class,
            () -> ExceptionChain.businessLogicLayer()
        );
        
        assertEquals("user_registration", exception.getBusinessOperation());
        assertEquals("BL001", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("ユーザー登録処理に失敗"));
        
        // 原因例外がDataAccessExceptionであることを確認
        assertInstanceOf(ExceptionChain.DataAccessException.class, exception.getCause());
    }
    
    @Test
    public void testComplexExceptionChain() {
        ExceptionChain.BusinessLogicException exception = assertThrows(
            ExceptionChain.BusinessLogicException.class,
            () -> ExceptionChain.complexExceptionChain()
        );
        
        assertEquals("system_initialization", exception.getBusinessOperation());
        assertEquals("BL002", exception.getErrorCode());
        
        // 抑制された例外があることを確認
        Throwable[] suppressed = exception.getSuppressed();
        assertEquals(1, suppressed.length);
        assertInstanceOf(ExceptionChain.ConfigurationException.class, suppressed[0]);
    }
    
    @Test
    public void testNetworkExceptionChain() {
        ExceptionChain.BusinessLogicException exception = assertThrows(
            ExceptionChain.BusinessLogicException.class,
            () -> ExceptionChain.networkExceptionChain()
        );
        
        assertEquals("external_data_sync", exception.getBusinessOperation());
        assertEquals("BL003", exception.getErrorCode());
        
        // 例外チェーンの確認
        assertInstanceOf(ExceptionChain.DataAccessException.class, exception.getCause());
        assertInstanceOf(IOException.class, exception.getCause().getCause());
    }
    
    @Test
    public void testGetExceptionChainDetails() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            String details = ExceptionChain.getExceptionChainDetails(e);
            
            assertNotNull(details);
            assertTrue(details.contains("例外チェーン詳細"));
            assertTrue(details.contains("BusinessLogicException"));
            assertTrue(details.contains("DataAccessException"));
            assertTrue(details.contains("SQLException"));
            assertTrue(details.contains("ビジネス操作: user_registration"));
            assertTrue(details.contains("エラーコード: BL001"));
        }
    }
    
    @Test
    public void testFindExceptionByType() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            // BusinessLogicExceptionを検索
            ExceptionChain.BusinessLogicException found = 
                ExceptionChain.findExceptionByType(e, ExceptionChain.BusinessLogicException.class);
            assertNotNull(found);
            assertEquals(e, found);
            
            // DataAccessExceptionを検索
            ExceptionChain.DataAccessException dataEx = 
                ExceptionChain.findExceptionByType(e, ExceptionChain.DataAccessException.class);
            assertNotNull(dataEx);
            assertEquals("primary_database", dataEx.getDataSource());
            
            // SQLExceptionを検索
            SQLException sqlEx = ExceptionChain.findExceptionByType(e, SQLException.class);
            assertNotNull(sqlEx);
            
            // 存在しない例外タイプを検索
            NullPointerException npe = 
                ExceptionChain.findExceptionByType(e, NullPointerException.class);
            assertNull(npe);
        }
    }
    
    @Test
    public void testGetRootCause() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            Throwable rootCause = ExceptionChain.getRootCause(e);
            
            assertNotNull(rootCause);
            assertInstanceOf(SQLException.class, rootCause);
            assertTrue(rootCause.getMessage().contains("データベース接続に失敗"));
        }
    }
    
    @Test
    public void testGetExceptionChainDepth() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            int depth = ExceptionChain.getExceptionChainDepth(e);
            
            // BusinessLogicException -> DataAccessException -> SQLException
            assertEquals(3, depth);
        }
    }
    
    @Test
    public void testFindBusinessExceptionByCode() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            // 存在するエラーコードを検索
            ExceptionChain.BusinessLogicException found = 
                ExceptionChain.findBusinessExceptionByCode(e, "BL001");
            assertNotNull(found);
            assertEquals("user_registration", found.getBusinessOperation());
            
            // 存在しないエラーコードを検索
            ExceptionChain.BusinessLogicException notFound = 
                ExceptionChain.findBusinessExceptionByCode(e, "BL999");
            assertNull(notFound);
        }
    }
    
    @Test
    public void testGetExceptionSeverity() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            int severity = ExceptionChain.getExceptionSeverity(e);
            
            // 根本原因がSQLExceptionなので重要度は3（高）
            assertEquals(3, severity);
        }
        
        try {
            ExceptionChain.networkExceptionChain();
        } catch (ExceptionChain.BusinessLogicException e) {
            int severity = ExceptionChain.getExceptionSeverity(e);
            
            // 根本原因がIOExceptionなので重要度は2（中）
            assertEquals(2, severity);
        }
    }
    
    @Test
    public void testIsRetryableException() {
        try {
            ExceptionChain.businessLogicLayer();
        } catch (ExceptionChain.BusinessLogicException e) {
            boolean isRetryable = ExceptionChain.isRetryableException(e);
            
            // SQLExceptionは再試行可能
            assertTrue(isRetryable);
        }
        
        try {
            ExceptionChain.networkExceptionChain();
        } catch (ExceptionChain.BusinessLogicException e) {
            boolean isRetryable = ExceptionChain.isRetryableException(e);
            
            // ConnectExceptionは再試行可能
            assertTrue(isRetryable);
        }
    }
    
    @Test
    public void testCustomExceptionProperties() {
        ExceptionChain.BusinessLogicException businessEx = 
            new ExceptionChain.BusinessLogicException("test_operation", "TEST001", "Test message");
        
        assertEquals("test_operation", businessEx.getBusinessOperation());
        assertEquals("TEST001", businessEx.getErrorCode());
        assertEquals("Test message", businessEx.getMessage());
    }
    
    @Test
    public void testDataAccessExceptionProperties() {
        ExceptionChain.DataAccessException dataEx = 
            new ExceptionChain.DataAccessException("test_db", "INSERT", "Test message");
        
        assertEquals("test_db", dataEx.getDataSource());
        assertEquals("INSERT", dataEx.getOperation());
        assertEquals("Test message", dataEx.getMessage());
    }
    
    @Test
    public void testConfigurationExceptionProperties() {
        ExceptionChain.ConfigurationException configEx = 
            new ExceptionChain.ConfigurationException("test.key", "test.value", "Test message");
        
        assertEquals("test.key", configEx.getConfigKey());
        assertEquals("test.value", configEx.getConfigValue());
        assertEquals("Test message", configEx.getMessage());
    }
    
    @Test
    public void testExceptionChainDetailsWithSuppressed() {
        try {
            ExceptionChain.complexExceptionChain();
        } catch (ExceptionChain.BusinessLogicException e) {
            String details = ExceptionChain.getExceptionChainDetails(e);
            
            assertTrue(details.contains("抑制された例外"));
            assertTrue(details.contains("ConfigurationException"));
        }
    }
    
    @Test
    public void testSingleExceptionChain() {
        Exception singleEx = new Exception("Single exception");
        
        assertEquals(1, ExceptionChain.getExceptionChainDepth(singleEx));
        assertEquals(singleEx, ExceptionChain.getRootCause(singleEx));
    }
}