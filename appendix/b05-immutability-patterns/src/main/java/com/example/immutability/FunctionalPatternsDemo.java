package com.example.immutability;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 関数型プログラミングでの不変性パターンのデモンストレーション
 * レンズパターン、状態管理、関数合成の実装
 */
public class FunctionalPatternsDemo {
    
    /**
     * レンズパターンの実装
     */
    public static class Lens<T, V> {
        private final Function<T, V> getter;
        private final BiFunction<T, V, T> setter;
        
        public Lens(Function<T, V> getter, BiFunction<T, V, T> setter) {
            this.getter = getter;
            this.setter = setter;
        }
        
        public V get(T target) {
            return getter.apply(target);
        }
        
        public T set(T target, V value) {
            return setter.apply(target, value);
        }
        
        public T modify(T target, Function<V, V> modifier) {
            return set(target, modifier.apply(get(target)));
        }
        
        // レンズの合成
        public <W> Lens<T, W> compose(Lens<V, W> other) {
            return new Lens<>(
                target -> other.get(getter.apply(target)),
                (target, value) -> setter.apply(target, other.set(getter.apply(target), value))
            );
        }
        
        // 条件付き更新
        public T modifyIf(T target, Predicate<V> condition, Function<V, V> modifier) {
            V currentValue = get(target);
            return condition.test(currentValue) ? set(target, modifier.apply(currentValue)) : target;
        }
        
        public static <T, V> Lens<T, V> of(Function<T, V> getter, BiFunction<T, V, T> setter) {
            return new Lens<>(getter, setter);
        }
    }
    
    /**
     * 住所を表す不変クラス
     */
    public static final class Address {
        private final String street;
        private final String city;
        private final String zipCode;
        private final String country;
        
        public Address(String street, String city, String zipCode, String country) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
            this.country = country;
        }
        
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getZipCode() { return zipCode; }
        public String getCountry() { return country; }
        
        public Address withStreet(String street) {
            return new Address(street, city, zipCode, country);
        }
        
        public Address withCity(String city) {
            return new Address(street, city, zipCode, country);
        }
        
        public Address withZipCode(String zipCode) {
            return new Address(street, city, zipCode, country);
        }
        
        public Address withCountry(String country) {
            return new Address(street, city, zipCode, country);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Address)) return false;
            Address other = (Address) obj;
            return Objects.equals(street, other.street) &&
                   Objects.equals(city, other.city) &&
                   Objects.equals(zipCode, other.zipCode) &&
                   Objects.equals(country, other.country);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(street, city, zipCode, country);
        }
        
        @Override
        public String toString() {
            return String.format("Address{street='%s', city='%s', zipCode='%s', country='%s'}",
                               street, city, zipCode, country);
        }
    }
    
    /**
     * 人を表す不変クラス
     */
    public static final class Person {
        private final String name;
        private final int age;
        private final Address address;
        private final List<String> hobbies;
        
        public Person(String name, int age, Address address, List<String> hobbies) {
            this.name = name;
            this.age = age;
            this.address = address;
            this.hobbies = List.copyOf(hobbies);
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public Address getAddress() { return address; }
        public List<String> getHobbies() { return hobbies; }
        
        public Person withName(String name) {
            return new Person(name, age, address, hobbies);
        }
        
        public Person withAge(int age) {
            return new Person(name, age, address, hobbies);
        }
        
        public Person withAddress(Address address) {
            return new Person(name, age, address, hobbies);
        }
        
        public Person withHobbies(List<String> hobbies) {
            return new Person(name, age, address, hobbies);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Person)) return false;
            Person other = (Person) obj;
            return age == other.age &&
                   Objects.equals(name, other.name) &&
                   Objects.equals(address, other.address) &&
                   Objects.equals(hobbies, other.hobbies);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, age, address, hobbies);
        }
        
        @Override
        public String toString() {
            return String.format("Person{name='%s', age=%d, address=%s, hobbies=%s}",
                               name, age, address, hobbies);
        }
    }
    
    /**
     * レンズの使用例
     */
    public static class LensExample {
        // Personのフィールドへのレンズ
        static final Lens<Person, String> personNameLens = Lens.of(
            Person::getName,
            (person, name) -> person.withName(name)
        );
        
        static final Lens<Person, Integer> personAgeLens = Lens.of(
            Person::getAge,
            (person, age) -> person.withAge(age)
        );
        
        static final Lens<Person, Address> personAddressLens = Lens.of(
            Person::getAddress,
            (person, address) -> person.withAddress(address)
        );
        
        static final Lens<Person, List<String>> personHobbiesLens = Lens.of(
            Person::getHobbies,
            (person, hobbies) -> person.withHobbies(hobbies)
        );
        
        // Addressのフィールドへのレンズ
        static final Lens<Address, String> addressStreetLens = Lens.of(
            Address::getStreet,
            (address, street) -> address.withStreet(street)
        );
        
        static final Lens<Address, String> addressCityLens = Lens.of(
            Address::getCity,
            (address, city) -> address.withCity(city)
        );
        
        static final Lens<Address, String> addressZipCodeLens = Lens.of(
            Address::getZipCode,
            (address, zipCode) -> address.withZipCode(zipCode)
        );
        
        static final Lens<Address, String> addressCountryLens = Lens.of(
            Address::getCountry,
            (address, country) -> address.withCountry(country)
        );
        
        // 合成されたレンズ：Person -> City
        static final Lens<Person, String> personCityLens = 
            personAddressLens.compose(addressCityLens);
        
        // 合成されたレンズ：Person -> Street
        static final Lens<Person, String> personStreetLens = 
            personAddressLens.compose(addressStreetLens);
        
        // 合成されたレンズ：Person -> ZipCode
        static final Lens<Person, String> personZipCodeLens = 
            personAddressLens.compose(addressZipCodeLens);
        
        public static void demonstrateLenses() {
            IO.println("=== Lens Pattern Demo ===");
            
            Address address = new Address("123 Main St", "Tokyo", "100-0001", "Japan");
            Person person = new Person("Alice", 30, address, Arrays.asList("reading", "swimming"));
            
            IO.println("Original person: " + person);
            
            // 深くネストしたフィールドの更新
            Person updatedCity = personCityLens.set(person, "Osaka");
            IO.println("Updated city: " + updatedCity);
            
            // 複数のフィールドを連鎖的に更新
            Person multiUpdated = personNameLens.set(
                personAgeLens.set(
                    personCityLens.set(person, "Kyoto"), 
                    31
                ), 
                "Bob"
            );
            IO.println("Multi-updated: " + multiUpdated);
            
            // 条件付き更新
            Person conditionalUpdate = personAgeLens.modifyIf(
                person, 
                age -> age >= 30, 
                age -> age + 1
            );
            IO.println("Conditional update (age >= 30): " + conditionalUpdate);
            
            // 関数型更新
            Person functionalUpdate = personHobbiesLens.modify(
                person,
                hobbies -> {
                    List<String> newHobbies = new ArrayList<>(hobbies);
                    newHobbies.add("programming");
                    return newHobbies;
                }
            );
            IO.println("Added hobby: " + functionalUpdate);
        }
    }
    
    /**
     * 不変な状態管理
     */
    public static final class State<T> {
        private final T value;
        private final List<Consumer<T>> listeners;
        
        private State(T value, List<Consumer<T>> listeners) {
            this.value = value;
            this.listeners = List.copyOf(listeners);
        }
        
        public static <T> State<T> of(T initialValue) {
            return new State<>(initialValue, List.of());
        }
        
        public T getValue() {
            return value;
        }
        
        public State<T> update(Function<T, T> updater) {
            T newValue = updater.apply(value);
            State<T> newState = new State<>(newValue, listeners);
            
            // リスナーに通知
            listeners.forEach(listener -> listener.accept(newValue));
            
            return newState;
        }
        
        public State<T> subscribe(Consumer<T> listener) {
            List<Consumer<T>> newListeners = new ArrayList<>(listeners);
            newListeners.add(listener);
            return new State<>(value, newListeners);
        }
        
        public <U> State<U> map(Function<T, U> mapper) {
            return State.of(mapper.apply(value));
        }
        
        public <U> State<U> flatMap(Function<T, State<U>> mapper) {
            return mapper.apply(value);
        }
        
        @Override
        public String toString() {
            return String.format("State{value=%s, listeners=%d}", value, listeners.size());
        }
    }
    
    /**
     * アプリケーション状態の例
     */
    public static final class AppState {
        private final List<String> todos;
        private final boolean loading;
        private final Optional<String> error;
        private final Map<String, Object> metadata;
        
        public AppState(List<String> todos, boolean loading, Optional<String> error, Map<String, Object> metadata) {
            this.todos = List.copyOf(todos);
            this.loading = loading;
            this.error = error;
            this.metadata = Map.copyOf(metadata);
        }
        
        public List<String> getTodos() { return todos; }
        public boolean isLoading() { return loading; }
        public Optional<String> getError() { return error; }
        public Map<String, Object> getMetadata() { return metadata; }
        
        public static AppState initial() {
            return new AppState(List.of(), false, Optional.empty(), Map.of());
        }
        
        public AppState withTodos(List<String> todos) {
            return new AppState(todos, loading, error, metadata);
        }
        
        public AppState withLoading(boolean loading) {
            return new AppState(todos, loading, error, metadata);
        }
        
        public AppState withError(Optional<String> error) {
            return new AppState(todos, loading, error, metadata);
        }
        
        public AppState withMetadata(Map<String, Object> metadata) {
            return new AppState(todos, loading, error, metadata);
        }
        
        @Override
        public String toString() {
            return String.format("AppState{todos=%d, loading=%s, error=%s, metadata=%s}",
                               todos.size(), loading, error.orElse("none"), metadata);
        }
    }
    
    /**
     * Reduxスタイルのアクション
     */
    public interface Action {}
    
    public static final class AddTodo implements Action {
        private final String text;
        public AddTodo(String text) { this.text = text; }
        public String getText() { return text; }
        @Override public String toString() { return "AddTodo{" + text + "}"; }
    }
    
    public static final class RemoveTodo implements Action {
        private final int index;
        public RemoveTodo(int index) { this.index = index; }
        public int getIndex() { return index; }
        @Override public String toString() { return "RemoveTodo{" + index + "}"; }
    }
    
    public static final class SetLoading implements Action {
        private final boolean loading;
        public SetLoading(boolean loading) { this.loading = loading; }
        public boolean isLoading() { return loading; }
        @Override public String toString() { return "SetLoading{" + loading + "}"; }
    }
    
    public static final class SetError implements Action {
        private final String error;
        public SetError(String error) { this.error = error; }
        public String getError() { return error; }
        @Override public String toString() { return "SetError{" + error + "}"; }
    }
    
    public static final class ClearError implements Action {
        @Override public String toString() { return "ClearError{}"; }
    }
    
    /**
     * リデューサー
     */
    public static class AppReducer {
        public static AppState reduce(AppState state, Action action) {
            if (action instanceof AddTodo) {
                AddTodo addAction = (AddTodo) action;
                List<String> newTodos = new ArrayList<>(state.getTodos());
                newTodos.add(addAction.getText());
                return state.withTodos(newTodos);
                
            } else if (action instanceof RemoveTodo) {
                RemoveTodo removeAction = (RemoveTodo) action;
                List<String> newTodos = new ArrayList<>(state.getTodos());
                if (removeAction.getIndex() >= 0 && removeAction.getIndex() < newTodos.size()) {
                    newTodos.remove(removeAction.getIndex());
                }
                return state.withTodos(newTodos);
                
            } else if (action instanceof SetLoading) {
                SetLoading loadingAction = (SetLoading) action;
                return state.withLoading(loadingAction.isLoading());
                
            } else if (action instanceof SetError) {
                SetError errorAction = (SetError) action;
                return state
                    .withError(Optional.of(errorAction.getError()))
                    .withLoading(false);
                
            } else if (action instanceof ClearError) {
                return state.withError(Optional.empty());
            }
            
            return state;
        }
    }
    
    /**
     * 状態管理ストア
     */
    public static class Store<S> {
        private volatile S state;
        private final List<Consumer<S>> subscribers = new ArrayList<>();
        private final BiFunction<S, Action, S> reducer;
        
        public Store(S initialState, BiFunction<S, Action, S> reducer) {
            this.state = initialState;
            this.reducer = reducer;
        }
        
        public S getState() {
            return state;
        }
        
        public synchronized void dispatch(Action action) {
            S newState = reducer.apply(state, action);
            if (!Objects.equals(state, newState)) {
                state = newState;
                notifySubscribers();
            }
        }
        
        public synchronized void subscribe(Consumer<S> subscriber) {
            subscribers.add(subscriber);
        }
        
        private void notifySubscribers() {
            subscribers.forEach(subscriber -> subscriber.accept(state));
        }
    }
    
    /**
     * 関数合成のユーティリティ
     */
    public static class FunctionComposition {
        
        public static <T> Function<T, T> compose(Function<T, T>... functions) {
            return Arrays.stream(functions)
                .reduce(Function.identity(), Function::andThen);
        }
        
        public static <T> Function<T, T> pipe(Function<T, T>... functions) {
            return compose(functions);
        }
        
        public static <T, R> Function<T, Optional<R>> safe(Function<T, R> function) {
            return input -> {
                try {
                    return Optional.of(function.apply(input));
                } catch (Exception e) {
                    return Optional.empty();
                }
            };
        }
        
        public static <T> Function<T, T> memoize(Function<T, T> function) {
            Map<T, T> cache = new ConcurrentHashMap<>();
            return input -> cache.computeIfAbsent(input, function);
        }
        
        public static void demonstrateFunctionComposition() {
            IO.println("\n=== Function Composition Demo ===");
            
            // 基本的な関数
            Function<String, String> trim = String::trim;
            Function<String, String> uppercase = String::toUpperCase;
            Function<String, String> addPrefix = s -> "PREFIX_" + s;
            
            // 関数の合成
            Function<String, String> processText = compose(trim, uppercase, addPrefix);
            
            String input = "  hello world  ";
            String result = processText.apply(input);
            
            IO.println("Input: '" + input + "'");
            IO.println("Result: '" + result + "'");
            
            // 安全な関数
            Function<String, Integer> unsafeParse = Integer::parseInt;
            Function<String, Optional<Integer>> safeParse = safe(unsafeParse);
            
            IO.println("Safe parse '123': " + safeParse.apply("123"));
            IO.println("Safe parse 'abc': " + safeParse.apply("abc"));
            
            // メモ化
            Function<Integer, Integer> expensiveFunction = n -> {
                IO.println("Computing for: " + n);
                return n * n;
            };
            
            Function<Integer, Integer> memoizedFunction = memoize(expensiveFunction);
            
            IO.println("First call: " + memoizedFunction.apply(5));
            IO.println("Second call: " + memoizedFunction.apply(5)); // キャッシュから
            IO.println("Third call: " + memoizedFunction.apply(3));
        }
    }
    
    /**
     * 非同期状態管理
     */
    public static class AsyncStateManager<T> {
        private volatile State<T> currentState;
        private final List<Consumer<T>> subscribers = new ArrayList<>();
        
        public AsyncStateManager(T initialState) {
            this.currentState = State.of(initialState);
        }
        
        public T getCurrentState() {
            return currentState.getValue();
        }
        
        public CompletableFuture<T> updateAsync(Function<T, T> updater) {
            return CompletableFuture.supplyAsync(() -> {
                State<T> newState = currentState.update(updater);
                currentState = newState;
                notifySubscribers(newState.getValue());
                return newState.getValue();
            });
        }
        
        public synchronized void subscribe(Consumer<T> subscriber) {
            subscribers.add(subscriber);
        }
        
        private void notifySubscribers(T newState) {
            subscribers.forEach(subscriber -> subscriber.accept(newState));
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Functional Programming with Immutability Patterns Demo");
        IO.println("=====================================================");
        
        // レンズパターンのデモ
        LensExample.demonstrateLenses();
        
        // 状態管理のデモ
        demonstrateStateManagement();
        
        // Reduxスタイルの状態管理
        demonstrateReduxPattern();
        
        // 関数合成
        FunctionComposition.demonstrateFunctionComposition();
        
        // 非同期状態管理
        demonstrateAsyncStateManagement();
        
        IO.println("\nKey Insights:");
        IO.println("✓ Lens pattern enables clean nested data updates");
        IO.println("✓ Immutable state management prevents unexpected mutations");
        IO.println("✓ Redux pattern provides predictable state transitions");
        IO.println("✓ Function composition creates reusable transformations");
        IO.println("✓ Async state management maintains consistency under concurrency");
        
        IO.println("\nBest Practices:");
        IO.println("• Use lenses for deep object updates");
        IO.println("• Implement proper action typing for type safety");
        IO.println("• Compose small, pure functions for complex operations");
        IO.println("• Subscribe to state changes for reactive programming");
        IO.println("• Handle async operations with immutable state patterns");
    }
    
    private static void demonstrateStateManagement() {
        IO.println("\n=== State Management Demo ===");
        
        // 初期状態の作成
        State<Integer> counter = State.of(0);
        
        // 状態の変更
        counter = counter.update(value -> value + 1);
        IO.println("After increment: " + counter.getValue());
        
        counter = counter.update(value -> value * 2);
        IO.println("After doubling: " + counter.getValue());
        
        // リスナーの追加
        State<String> message = State.of("Hello");
        message = message.subscribe(newValue -> 
            IO.println("State changed to: " + newValue));
        
        message = message.update(value -> value + " World");
        message = message.update(String::toUpperCase);
        
        // 状態の変換
        State<Integer> length = message.map(String::length);
        IO.println("Message length: " + length.getValue());
    }
    
    private static void demonstrateReduxPattern() {
        IO.println("\n=== Redux Pattern Demo ===");
        
        // ストアの作成
        Store<AppState> store = new Store<>(AppState.initial(), AppReducer::reduce);
        
        // 状態変更の監視
        store.subscribe(state -> IO.println("State updated: " + state));
        
        // アクションのディスパッチ
        IO.println("Initial state: " + store.getState());
        
        store.dispatch(new SetLoading(true));
        store.dispatch(new AddTodo("Learn functional programming"));
        store.dispatch(new AddTodo("Implement immutable patterns"));
        store.dispatch(new SetLoading(false));
        
        IO.println("After adding todos: " + store.getState());
        
        store.dispatch(new RemoveTodo(0));
        IO.println("After removing first todo: " + store.getState());
        
        store.dispatch(new SetError("Network error"));
        IO.println("After setting error: " + store.getState());
        
        store.dispatch(new ClearError());
        IO.println("After clearing error: " + store.getState());
    }
    
    private static void demonstrateAsyncStateManagement() {
        IO.println("\n=== Async State Management Demo ===");
        
        AsyncStateManager<List<String>> asyncManager = new AsyncStateManager<>(new ArrayList<>());
        
        // 非同期状態変更の監視
        asyncManager.subscribe(state -> 
            IO.println("Async state updated: " + state));
        
        // 複数の非同期更新
        CompletableFuture<List<String>> future1 = asyncManager.updateAsync(list -> {
            List<String> newList = new ArrayList<>(list);
            newList.add("Item 1");
            return newList;
        });
        
        CompletableFuture<List<String>> future2 = asyncManager.updateAsync(list -> {
            List<String> newList = new ArrayList<>(list);
            newList.add("Item 2");
            return newList;
        });
        
        CompletableFuture<List<String>> future3 = asyncManager.updateAsync(list -> {
            List<String> newList = new ArrayList<>(list);
            newList.add("Item 3");
            return newList;
        });
        
        // すべての更新の完了を待つ
        CompletableFuture.allOf(future1, future2, future3)
            .thenRun(() -> {
                IO.println("All async updates completed");
                IO.println("Final state: " + asyncManager.getCurrentState());
            })
            .join();
    }
}