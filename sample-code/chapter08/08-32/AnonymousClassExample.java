import java.util.Arrays;
import java.util.Comparator;

public class AnonymousClassExample {
    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Charlie"};
        
        // 匿名内部クラスでComparatorを実装
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareTo(s1); // 逆順ソート
            }
        });
        
        // Java 8以降はラムダ式で簡潔に記述可能
        // Arrays.sort(names, (s1, s2) -> s2.compareTo(s1));
        
        System.out.println(Arrays.toString(names));
    }
}
