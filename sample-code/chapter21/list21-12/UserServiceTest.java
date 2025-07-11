/**
 * リスト21-12
 * UserServiceTestクラス
 * 
 * 元ファイル: chapter21-unit-testing.md (380行目)
 */

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository mockRepository;
    
    @Test
    void testGetUserProfile() {
        // モックの振る舞いを定義
        when(mockRepository.findById("123"))
            .thenReturn("Test User");
        
        UserService service = new UserService(mockRepository);
        String profile = service.getUserProfile("123");
        
        assertEquals("【Profile】Test User", profile);
        verify(mockRepository).findById("123"); // 呼び出しを検証
    }
}