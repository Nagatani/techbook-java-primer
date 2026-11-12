import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
