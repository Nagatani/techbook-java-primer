// 第21章：JDK 21以降でMockitoがエージェントを後から組み込むときの警告
import java.util.List;
import org.mockito.Mockito;

public class MockitoSelfAttach {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.size()).thenReturn(3);
        System.out.println("size = " + list.size());
    }
}
