package chapter09.solutions;

import java.util.*;
import java.util.function.*;

/**
 * 型安全なビルダーパターンの実装
 */
public class TypeSafeBuilder<T> {
    
    public static class Person {
        private final String name;
        private final int age;
        private final String email;
        private final List<String> hobbies;
        
        private Person(String name, int age, String email, List<String> hobbies) {
            this.name = name;
            this.age = age;
            this.email = email;
            this.hobbies = new ArrayList<>(hobbies);
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }
        public List<String> getHobbies() { return new ArrayList<>(hobbies); }
        
        @Override
        public String toString() {
            return String.format("Person{name='%s', age=%d, email='%s', hobbies=%s}",
                               name, age, email, hobbies);
        }
    }
    
    public static class PersonBuilder {
        private String name;
        private int age;
        private String email;
        private List<String> hobbies = new ArrayList<>();
        
        private PersonBuilder() {}
        
        public PersonBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public PersonBuilder age(int age) {
            this.age = age;
            return this;
        }
        
        public PersonBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public PersonBuilder hobby(String hobby) {
            this.hobbies.add(hobby);
            return this;
        }
        
        public PersonBuilder hobbies(String... hobbies) {
            this.hobbies.addAll(Arrays.asList(hobbies));
            return this;
        }
        
        public Person build() {
            Objects.requireNonNull(name, "名前は必須です");
            if (age < 0) throw new IllegalArgumentException("年齢は0以上である必要があります");
            return new Person(name, age, email, hobbies);
        }
    }
    
    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }
    
    public static PersonBuilder personBuilder() {
        return new PersonBuilder();
    }
    
    public static class Builder<T> {
        private final Class<T> type;
        private final Map<String, Object> properties = new HashMap<>();
        
        private Builder(Class<T> type) {
            this.type = type;
        }
        
        public <V> Builder<T> set(String property, V value) {
            properties.put(property, value);
            return this;
        }
        
        public <V> Builder<T> setIf(boolean condition, String property, V value) {
            if (condition) {
                properties.put(property, value);
            }
            return this;
        }
        
        public <V> Builder<T> setIfNotNull(String property, V value) {
            if (value != null) {
                properties.put(property, value);
            }
            return this;
        }
        
        public Builder<T> configure(Consumer<Builder<T>> configurator) {
            configurator.accept(this);
            return this;
        }
        
        public Map<String, Object> getProperties() {
            return new HashMap<>(properties);
        }
        
        public Class<T> getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return String.format("Builder<%s>{properties=%s}", 
                               type.getSimpleName(), properties);
        }
    }
    
    // フルーエントインターフェースの例
    public interface FluentBuilder<T> {
        T build();
    }
    
    public static class FluentPersonBuilder implements FluentBuilder<Person> {
        private String name;
        private int age;
        private String email;
        private List<String> hobbies = new ArrayList<>();
        
        public NameStep name(String name) {
            this.name = name;
            return new NameStep();
        }
        
        public class NameStep {
            public AgeStep age(int age) {
                FluentPersonBuilder.this.age = age;
                return new AgeStep();
            }
        }
        
        public class AgeStep {
            public EmailStep email(String email) {
                FluentPersonBuilder.this.email = email;
                return new EmailStep();
            }
            
            public Person build() {
                return new Person(name, age, null, hobbies);
            }
        }
        
        public class EmailStep {
            public EmailStep hobby(String hobby) {
                hobbies.add(hobby);
                return this;
            }
            
            public Person build() {
                return new Person(name, age, email, hobbies);
            }
        }
    }
    
    public static FluentPersonBuilder fluentPerson() {
        return new FluentPersonBuilder();
    }
}