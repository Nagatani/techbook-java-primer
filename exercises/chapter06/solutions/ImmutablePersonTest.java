import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ImmutablePersonクラスのテストクラス
 * 
 * 【テストの観点】
 * 1. 不変性の確認
 * 2. 防御的コピーの確認
 * 3. withメソッドの動作確認
 * 4. 境界値・異常値のテスト
 * 5. equals/hashCode の動作確認
 */
public class ImmutablePersonTest {
    
    private ImmutablePerson person;
    private List<String> hobbies;
    
    @BeforeEach
    void setUp() {
        hobbies = new ArrayList<>(Arrays.asList("読書", "映画鑑賞"));
        person = new ImmutablePerson("田中太郎", LocalDate.of(1990, 1, 1), hobbies);
    }
    
    @Test
    @DisplayName("基本的なゲッターのテスト")
    void testBasicGetters() {
        assertEquals("田中太郎", person.getName());
        assertEquals(LocalDate.of(1990, 1, 1), person.getBirthDate());
        assertEquals(Arrays.asList("読書", "映画鑑賞"), person.getHobbies());
    }
    
    @Test
    @DisplayName("不変性のテスト：元のリストを変更しても影響を受けない")
    void testImmutability_OriginalListModification() {
        // 元のリストを変更
        hobbies.add("スポーツ");
        hobbies.remove("読書");
        
        // ImmutablePersonのhobbiesは影響を受けない
        assertEquals(Arrays.asList("読書", "映画鑑賞"), person.getHobbies());
    }
    
    @Test
    @DisplayName("不変性のテスト：取得したリストは変更不可")
    void testImmutability_ReturnedListIsImmutable() {
        List<String> returnedHobbies = person.getHobbies();
        
        // 取得したリストの変更は例外が発生
        assertThrows(UnsupportedOperationException.class, () -> {
            returnedHobbies.add("新しい趣味");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            returnedHobbies.remove(0);
        });
    }
    
    @Test
    @DisplayName("withNameメソッドのテスト")
    void testWithName() {
        ImmutablePerson newPerson = person.withName("山田花子");
        
        // 新しいインスタンスが作成される
        assertNotSame(person, newPerson);
        
        // 元のインスタンスは変更されない
        assertEquals("田中太郎", person.getName());
        assertEquals("山田花子", newPerson.getName());
        
        // 他のフィールドは同じ値を持つ
        assertEquals(person.getBirthDate(), newPerson.getBirthDate());
        assertEquals(person.getHobbies(), newPerson.getHobbies());
    }
    
    @Test
    @DisplayName("withBirthDateメソッドのテスト")
    void testWithBirthDate() {
        LocalDate newBirthDate = LocalDate.of(1995, 5, 15);
        ImmutablePerson newPerson = person.withBirthDate(newBirthDate);
        
        assertNotSame(person, newPerson);
        assertEquals(LocalDate.of(1990, 1, 1), person.getBirthDate());
        assertEquals(newBirthDate, newPerson.getBirthDate());
        
        assertEquals(person.getName(), newPerson.getName());
        assertEquals(person.getHobbies(), newPerson.getHobbies());
    }
    
    @Test
    @DisplayName("withHobbiesメソッドのテスト")
    void testWithHobbies() {
        List<String> newHobbies = Arrays.asList("料理", "旅行", "写真");
        ImmutablePerson newPerson = person.withHobbies(newHobbies);
        
        assertNotSame(person, newPerson);
        assertEquals(Arrays.asList("読書", "映画鑑賞"), person.getHobbies());
        assertEquals(newHobbies, newPerson.getHobbies());
        
        // 元のnewHobbiesを変更しても影響を受けない
        newHobbies.add("追加された趣味");
        assertEquals(Arrays.asList("料理", "旅行", "写真"), newPerson.getHobbies());
    }
    
    @Test
    @DisplayName("addHobbyメソッドのテスト")
    void testAddHobby() {
        ImmutablePerson newPerson = person.addHobby("プログラミング");
        
        assertNotSame(person, newPerson);
        assertEquals(Arrays.asList("読書", "映画鑑賞"), person.getHobbies());
        assertEquals(Arrays.asList("読書", "映画鑑賞", "プログラミング"), newPerson.getHobbies());
    }
    
    @Test
    @DisplayName("nullチェックのテスト")
    void testNullChecks() {
        assertThrows(NullPointerException.class, () -> {
            new ImmutablePerson(null, LocalDate.now(), Arrays.asList("趣味"));
        });
        
        assertThrows(NullPointerException.class, () -> {
            new ImmutablePerson("名前", null, Arrays.asList("趣味"));
        });
        
        // hobbiesがnullの場合は空リストになる
        ImmutablePerson personWithNullHobbies = new ImmutablePerson("名前", LocalDate.now(), null);
        assertTrue(personWithNullHobbies.getHobbies().isEmpty());
    }
    
    @Test
    @DisplayName("equalsとhashCodeのテスト")
    void testEqualsAndHashCode() {
        // 同じ値を持つ別のインスタンス
        ImmutablePerson person2 = new ImmutablePerson("田中太郎", LocalDate.of(1990, 1, 1), 
                                                     Arrays.asList("読書", "映画鑑賞"));
        
        // equals
        assertEquals(person, person2);
        assertEquals(person, person);  // 自分自身
        assertNotEquals(person, null);
        assertNotEquals(person, "文字列");
        
        // hashCode
        assertEquals(person.hashCode(), person2.hashCode());
        
        // 異なる値を持つインスタンス
        ImmutablePerson person3 = new ImmutablePerson("田中太郎", LocalDate.of(1990, 1, 1), 
                                                     Arrays.asList("読書"));
        assertNotEquals(person, person3);
    }
    
    @Test
    @DisplayName("toStringのテスト")
    void testToString() {
        String str = person.toString();
        assertTrue(str.contains("田中太郎"));
        assertTrue(str.contains("1990-01-01"));
        assertTrue(str.contains("読書"));
        assertTrue(str.contains("映画鑑賞"));
    }
    
    @Test
    @DisplayName("Builderパターンのテスト")
    void testBuilderPattern() {
        ImmutablePerson builtPerson = ImmutablePerson.builder()
            .setName("佐藤次郎")
            .setBirthDate(LocalDate.of(1985, 3, 20))
            .addHobby("ランニング")
            .addHobby("料理")
            .build();
        
        assertEquals("佐藤次郎", builtPerson.getName());
        assertEquals(LocalDate.of(1985, 3, 20), builtPerson.getBirthDate());
        assertEquals(Arrays.asList("ランニング", "料理"), builtPerson.getHobbies());
    }
    
    @Test
    @DisplayName("Builderパターンのメソッドチェーンテスト")
    void testBuilderMethodChaining() {
        ImmutablePerson.Builder builder = ImmutablePerson.builder();
        
        // メソッドチェーンでBuilderインスタンスが返される
        assertSame(builder, builder.setName("テスト"));
        assertSame(builder, builder.setBirthDate(LocalDate.now()));
        assertSame(builder, builder.addHobby("テスト趣味"));
    }
    
    @Test
    @DisplayName("空のhobbiesリストのテスト")
    void testEmptyHobbiesList() {
        ImmutablePerson personWithEmptyHobbies = new ImmutablePerson("田中太郎", LocalDate.now(), 
                                                                   new ArrayList<>());
        assertTrue(personWithEmptyHobbies.getHobbies().isEmpty());
        
        // 空のリストにhobbyを追加
        ImmutablePerson newPerson = personWithEmptyHobbies.addHobby("新しい趣味");
        assertEquals(Arrays.asList("新しい趣味"), newPerson.getHobbies());
        assertTrue(personWithEmptyHobbies.getHobbies().isEmpty());  // 元は変更されない
    }
    
    @Test
    @DisplayName("大量のhobbiesを持つ場合のテスト")
    void testLargeHobbiesList() {
        List<String> largeHobbies = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeHobbies.add("趣味" + i);
        }
        
        ImmutablePerson personWithLargeHobbies = new ImmutablePerson("田中太郎", LocalDate.now(), 
                                                                   largeHobbies);
        
        assertEquals(1000, personWithLargeHobbies.getHobbies().size());
        
        // 元のリストを変更してもImmutablePersonは影響を受けない
        largeHobbies.clear();
        assertEquals(1000, personWithLargeHobbies.getHobbies().size());
    }
}