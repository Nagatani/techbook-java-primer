package chapter09.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class TypeSafeBuilderTest {
    
    @Test
    void testPersonBuilder() {
        TypeSafeBuilder.Person person = TypeSafeBuilder.personBuilder()
            .name("田中太郎")
            .age(30)
            .email("tanaka@example.com")
            .hobby("読書")
            .hobby("音楽鑑賞")
            .build();
        
        assertEquals("田中太郎", person.getName());
        assertEquals(30, person.getAge());
        assertEquals("tanaka@example.com", person.getEmail());
        assertEquals(2, person.getHobbies().size());
        assertTrue(person.getHobbies().contains("読書"));
    }
    
    @Test
    void testPersonBuilderWithMultipleHobbies() {
        TypeSafeBuilder.Person person = TypeSafeBuilder.personBuilder()
            .name("佐藤花子")
            .age(25)
            .hobbies("料理", "旅行", "写真")
            .build();
        
        assertEquals(3, person.getHobbies().size());
        assertTrue(person.getHobbies().contains("料理"));
        assertTrue(person.getHobbies().contains("旅行"));
        assertTrue(person.getHobbies().contains("写真"));
    }
    
    @Test
    void testPersonBuilderValidation() {
        assertThrows(NullPointerException.class, () -> {
            TypeSafeBuilder.personBuilder()
                .age(30)
                .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            TypeSafeBuilder.personBuilder()
                .name("テスト")
                .age(-1)
                .build();
        });
    }
    
    @Test
    void testGenericBuilder() {
        TypeSafeBuilder.Builder<String> builder = TypeSafeBuilder.builder(String.class)
            .set("property1", "value1")
            .set("property2", 42)
            .setIfNotNull("property3", null)
            .setIfNotNull("property4", "not null");
        
        Map<String, Object> properties = builder.getProperties();
        assertEquals("value1", properties.get("property1"));
        assertEquals(42, properties.get("property2"));
        assertFalse(properties.containsKey("property3"));
        assertEquals("not null", properties.get("property4"));
    }
    
    @Test
    void testBuilderConfiguration() {
        TypeSafeBuilder.Builder<Integer> builder = TypeSafeBuilder.builder(Integer.class)
            .configure(b -> {
                b.set("min", 0);
                b.set("max", 100);
            })
            .set("default", 50);
        
        Map<String, Object> properties = builder.getProperties();
        assertEquals(0, properties.get("min"));
        assertEquals(100, properties.get("max"));
        assertEquals(50, properties.get("default"));
    }
    
    @Test
    void testFluentBuilder() {
        TypeSafeBuilder.Person person = TypeSafeBuilder.fluentPerson()
            .name("鈴木次郎")
            .age(35)
            .email("suzuki@example.com")
            .hobby("ゴルフ")
            .build();
        
        assertEquals("鈴木次郎", person.getName());
        assertEquals(35, person.getAge());
        assertEquals("suzuki@example.com", person.getEmail());
        assertTrue(person.getHobbies().contains("ゴルフ"));
    }
    
    @Test
    void testFluentBuilderWithoutEmail() {
        TypeSafeBuilder.Person person = TypeSafeBuilder.fluentPerson()
            .name("高橋美咲")
            .age(28)
            .build();
        
        assertEquals("高橋美咲", person.getName());
        assertEquals(28, person.getAge());
        assertNull(person.getEmail());
    }
    
    @Test
    void testBuilderToString() {
        TypeSafeBuilder.Builder<String> builder = TypeSafeBuilder.builder(String.class);
        String str = builder.toString();
        assertTrue(str.contains("String"));
        assertTrue(str.contains("properties"));
    }
    
    @Test
    void testPersonToString() {
        TypeSafeBuilder.Person person = TypeSafeBuilder.personBuilder()
            .name("テスト")
            .age(25)
            .build();
        
        String str = person.toString();
        assertTrue(str.contains("テスト"));
        assertTrue(str.contains("25"));
    }
}