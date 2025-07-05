package chapter12.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * PersonRecordクラスのテストクラス
 */
public class PersonRecordTest {
    
    private PersonRecord person;
    private LocalDate birthDate;
    
    @BeforeEach
    void setUp() {
        birthDate = LocalDate.of(1990, 5, 15);
        person = new PersonRecord("John", "Doe", birthDate, "john.doe@example.com", 
                                Arrays.asList("123-456-7890", "098-765-4321"));
    }
    
    @Test
    void testBasicConstruction() {
        assertEquals("John", person.firstName());
        assertEquals("Doe", person.lastName());
        assertEquals(birthDate, person.birthDate());
        assertEquals("john.doe@example.com", person.email());
        assertEquals(2, person.phoneNumbers().size());
    }
    
    @Test
    void testCompactConstructorValidation() {
        // null値のテスト
        assertThrows(NullPointerException.class, () -> 
            new PersonRecord(null, "Doe", birthDate, "john@example.com"));
        
        assertThrows(NullPointerException.class, () -> 
            new PersonRecord("John", null, birthDate, "john@example.com"));
        
        assertThrows(NullPointerException.class, () -> 
            new PersonRecord("John", "Doe", null, "john@example.com"));
        
        assertThrows(NullPointerException.class, () -> 
            new PersonRecord("John", "Doe", birthDate, null));
        
        // 空文字列のテスト
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonRecord("", "Doe", birthDate, "john@example.com"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonRecord("John", "", birthDate, "john@example.com"));
        
        // 未来の日付のテスト
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonRecord("John", "Doe", LocalDate.now().plusDays(1), "john@example.com"));
        
        // 無効なメールアドレスのテスト
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonRecord("John", "Doe", birthDate, "invalid-email"));
    }
    
    @Test
    void testAlternativeConstructors() {
        // 電話番号なしのコンストラクタ
        PersonRecord personWithoutPhone = new PersonRecord("Jane", "Smith", birthDate, "jane@example.com");
        assertEquals(0, personWithoutPhone.phoneNumbers().size());
        
        // 文字列の生年月日コンストラクタ
        PersonRecord personWithStringDate = new PersonRecord("Bob", "Johnson", "1985-12-25", "bob@example.com");
        assertEquals(LocalDate.of(1985, 12, 25), personWithStringDate.birthDate());
    }
    
    @Test
    void testFullName() {
        assertEquals("John Doe", person.fullName());
    }
    
    @Test
    void testAge() {
        // 年齢の計算は現在の日付に依存するため、概算をテスト
        int expectedAge = LocalDate.now().getYear() - 1990;
        int actualAge = person.age();
        
        // 誕生日が来ているかどうかで1歳の差がある可能性があるため、範囲でテスト
        assertTrue(actualAge >= expectedAge - 1 && actualAge <= expectedAge);
    }
    
    @Test
    void testIsAdult() {
        assertTrue(person.isAdult());
        
        // 子供の場合
        PersonRecord child = new PersonRecord("Child", "Test", LocalDate.now().minusYears(10), "child@example.com");
        assertFalse(child.isAdult());
    }
    
    @Test
    void testIsAge() {
        int currentAge = person.age();
        assertTrue(person.isAge(currentAge));
        assertFalse(person.isAge(currentAge + 1));
    }
    
    @Test
    void testIsAgeBetween() {
        int currentAge = person.age();
        assertTrue(person.isAgeBetween(currentAge - 1, currentAge + 1));
        assertFalse(person.isAgeBetween(currentAge + 1, currentAge + 5));
    }
    
    @Test
    void testPhoneNumberMethods() {
        assertTrue(person.hasPhoneNumber());
        assertEquals(2, person.phoneNumberCount());
        assertEquals("123-456-7890", person.primaryPhoneNumber());
        
        // 電話番号なしの場合
        PersonRecord personWithoutPhone = new PersonRecord("Jane", "Smith", birthDate, "jane@example.com");
        assertFalse(personWithoutPhone.hasPhoneNumber());
        assertEquals(0, personWithoutPhone.phoneNumberCount());
        assertNull(personWithoutPhone.primaryPhoneNumber());
    }
    
    @Test
    void testAddPhoneNumber() {
        PersonRecord updatedPerson = person.addPhoneNumber("555-123-4567");
        
        assertEquals(3, updatedPerson.phoneNumberCount());
        assertTrue(updatedPerson.phoneNumbers().contains("555-123-4567"));
        
        // 元のオブジェクトは変更されていない
        assertEquals(2, person.phoneNumberCount());
        
        // 無効な電話番号の追加
        assertThrows(IllegalArgumentException.class, () -> person.addPhoneNumber(""));
        assertThrows(IllegalArgumentException.class, () -> person.addPhoneNumber(null));
    }
    
    @Test
    void testRemovePhoneNumber() {
        PersonRecord updatedPerson = person.removePhoneNumber("123-456-7890");
        
        assertEquals(1, updatedPerson.phoneNumberCount());
        assertFalse(updatedPerson.phoneNumbers().contains("123-456-7890"));
        
        // 元のオブジェクトは変更されていない
        assertEquals(2, person.phoneNumberCount());
    }
    
    @Test
    void testWithMethods() {
        // 名前変更
        PersonRecord updatedName = person.withName("Jane", "Smith");
        assertEquals("Jane", updatedName.firstName());
        assertEquals("Smith", updatedName.lastName());
        
        // ファーストネーム変更
        PersonRecord updatedFirstName = person.withFirstName("Johnny");
        assertEquals("Johnny", updatedFirstName.firstName());
        assertEquals("Doe", updatedFirstName.lastName());
        
        // ラストネーム変更
        PersonRecord updatedLastName = person.withLastName("Johnson");
        assertEquals("John", updatedLastName.firstName());
        assertEquals("Johnson", updatedLastName.lastName());
        
        // メールアドレス変更
        PersonRecord updatedEmail = person.withEmail("newemail@example.com");
        assertEquals("newemail@example.com", updatedEmail.email());
        
        // 生年月日変更
        LocalDate newBirthDate = LocalDate.of(1995, 8, 20);
        PersonRecord updatedBirthDate = person.withBirthDate(newBirthDate);
        assertEquals(newBirthDate, updatedBirthDate.birthDate());
    }
    
    @Test
    void testInitials() {
        assertEquals("J", person.firstNameInitial());
        assertEquals("D", person.lastNameInitial());
        assertEquals("JD", person.initials());
    }
    
    @Test
    void testBirthDateMethods() {
        assertEquals("MAY", person.birthMonth());
        assertEquals(1990, person.birthYear());
        
        // 誕生日まで何日かは現在の日付に依存するため、負でないことを確認
        assertTrue(person.daysUntilBirthday() >= 0);
    }
    
    @Test
    void testIsBirthdayToday() {
        // 今日が誕生日のPersonRecordを作成
        LocalDate today = LocalDate.now();
        PersonRecord birthdayPerson = new PersonRecord("Birthday", "Person", 
            today.minusYears(20), "birthday@example.com");
        
        assertTrue(birthdayPerson.isBirthdayToday());
        assertFalse(person.isBirthdayToday()); // 5月15日でない限り
    }
    
    @Test
    void testEqualsAndHashCode() {
        PersonRecord person1 = new PersonRecord("John", "Doe", birthDate, "john.doe@example.com", 
                                               Arrays.asList("123-456-7890"));
        PersonRecord person2 = new PersonRecord("John", "Doe", birthDate, "john.doe@example.com", 
                                               Arrays.asList("123-456-7890"));
        PersonRecord person3 = new PersonRecord("Jane", "Doe", birthDate, "jane.doe@example.com", 
                                               Arrays.asList("123-456-7890"));
        
        assertEquals(person1, person2);
        assertNotEquals(person1, person3);
        assertEquals(person1.hashCode(), person2.hashCode());
    }
    
    @Test
    void testToString() {
        String personString = person.toString();
        assertTrue(personString.contains("John"));
        assertTrue(personString.contains("Doe"));
        assertTrue(personString.contains("john.doe@example.com"));
    }
    
    @Test
    void testComparators() {
        PersonRecord person1 = new PersonRecord("Alice", "Smith", LocalDate.of(1985, 3, 10), "alice@example.com");
        PersonRecord person2 = new PersonRecord("Bob", "Johnson", LocalDate.of(1990, 7, 20), "bob@example.com");
        PersonRecord person3 = new PersonRecord("Charlie", "Brown", LocalDate.of(1988, 12, 5), "charlie@example.com");
        
        List<PersonRecord> people = Arrays.asList(person1, person2, person3);
        
        // 年齢順でソート
        people.sort(PersonRecord.ageComparator());
        // 年齢が若い順（新しい生年月日順）になる
        
        // 名前順でソート
        people.sort(PersonRecord.nameComparator());
        assertEquals("Brown", people.get(0).lastName());
        assertEquals("Johnson", people.get(1).lastName());
        assertEquals("Smith", people.get(2).lastName());
        
        // 生年月日順でソート
        people.sort(PersonRecord.birthDateComparator());
        assertEquals(LocalDate.of(1985, 3, 10), people.get(0).birthDate());
    }
    
    @Test
    void testBuilder() {
        PersonRecord builtPerson = PersonRecord.builder()
            .firstName("Alice")
            .lastName("Wonder")
            .birthDate("1992-08-15")
            .email("alice@wonderland.com")
            .addPhoneNumber("111-222-3333")
            .addPhoneNumber("444-555-6666")
            .build();
        
        assertEquals("Alice", builtPerson.firstName());
        assertEquals("Wonder", builtPerson.lastName());
        assertEquals(LocalDate.of(1992, 8, 15), builtPerson.birthDate());
        assertEquals("alice@wonderland.com", builtPerson.email());
        assertEquals(2, builtPerson.phoneNumberCount());
    }
    
    @Test
    void testToBuilder() {
        PersonRecord modifiedPerson = person.toBuilder()
            .firstName("Johnny")
            .addPhoneNumber("777-888-9999")
            .build();
        
        assertEquals("Johnny", modifiedPerson.firstName());
        assertEquals("Doe", modifiedPerson.lastName());
        assertEquals(3, modifiedPerson.phoneNumberCount());
        
        // 元のオブジェクトは変更されていない
        assertEquals("John", person.firstName());
        assertEquals(2, person.phoneNumberCount());
    }
    
    @Test
    void testImmutability() {
        List<String> originalPhoneNumbers = person.phoneNumbers();
        
        // 返されたリストは変更できない
        assertThrows(UnsupportedOperationException.class, () -> 
            originalPhoneNumbers.add("999-888-7777"));
        
        // 元のオブジェクトは変更されていない
        assertEquals(2, person.phoneNumberCount());
    }
    
    @Test
    void testNormalization() {
        // 空白が正規化されることを確認
        PersonRecord normalizedPerson = new PersonRecord("  John  ", "  Doe  ", birthDate, 
                                                        "  JOHN.DOE@EXAMPLE.COM  ");
        
        assertEquals("John", normalizedPerson.firstName());
        assertEquals("Doe", normalizedPerson.lastName());
        assertEquals("john.doe@example.com", normalizedPerson.email());
    }
}