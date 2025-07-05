import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * 不変クラス（Immutable Class）の実装例
 * 
 * 【学習ポイント】
 * 1. final クラス: 継承を防ぐ
 * 2. final フィールド: 再代入を防ぐ
 * 3. プライベートフィールド: 外部からの直接アクセスを防ぐ
 * 4. ミュータブルオブジェクトの防御的コピー
 * 5. セッター無し: 変更メソッドは提供しない
 * 
 * 【よくある間違い】
 * - ミュータブルなフィールドをそのまま返す
 * - コンストラクタでミュータブル引数をそのまま代入
 * - finalを付け忘れる
 */
public final class ImmutablePerson {
    
    // すべてのフィールドをfinal、privateにする
    private final String name;
    private final LocalDate birthDate;
    private final List<String> hobbies;  // ミュータブルなオブジェクト
    
    /**
     * コンストラクタ
     * 【重要】ミュータブルな引数は防御的コピーを行う
     */
    public ImmutablePerson(String name, LocalDate birthDate, List<String> hobbies) {
        // null チェック
        this.name = Objects.requireNonNull(name, "名前はnullにできません");
        this.birthDate = Objects.requireNonNull(birthDate, "生年月日はnullにできません");
        
        // 防御的コピー: 元のリストを変更されても影響を受けない
        this.hobbies = hobbies == null ? 
            Collections.emptyList() : 
            new ArrayList<>(hobbies);
    }
    
    // ゲッターのみ提供（セッターは無し）
    public String getName() {
        return name;  // Stringは不変なのでそのまま返せる
    }
    
    public LocalDate getBirthDate() {
        return birthDate;  // LocalDateも不変なのでそのまま返せる
    }
    
    /**
     * ミュータブルなオブジェクトは防御的コピーを返す
     */
    public List<String> getHobbies() {
        // 不変なビューを返す（変更不可）
        return Collections.unmodifiableList(hobbies);
    }
    
    /**
     * withメソッドパターン: 新しいインスタンスを返す
     * 既存のインスタンスは変更せず、新しい値で新しいインスタンスを作成
     */
    public ImmutablePerson withName(String newName) {
        return new ImmutablePerson(newName, this.birthDate, this.hobbies);
    }
    
    public ImmutablePerson withBirthDate(LocalDate newBirthDate) {
        return new ImmutablePerson(this.name, newBirthDate, this.hobbies);
    }
    
    public ImmutablePerson withHobbies(List<String> newHobbies) {
        return new ImmutablePerson(this.name, this.birthDate, newHobbies);
    }
    
    /**
     * 趣味を追加した新しいインスタンスを返す
     */
    public ImmutablePerson addHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(this.hobbies);
        newHobbies.add(hobby);
        return new ImmutablePerson(this.name, this.birthDate, newHobbies);
    }
    
    /**
     * equals と hashCode は不変クラスでは重要
     * 値が同じなら同じオブジェクトとして扱える
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ImmutablePerson that = (ImmutablePerson) obj;
        return Objects.equals(name, that.name) &&
               Objects.equals(birthDate, that.birthDate) &&
               Objects.equals(hobbies, that.hobbies);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, birthDate, hobbies);
    }
    
    @Override
    public String toString() {
        return String.format("ImmutablePerson{name='%s', birthDate=%s, hobbies=%s}", 
                           name, birthDate, hobbies);
    }
    
    /**
     * Builderパターンの実装（発展）
     * 複雑な不変オブジェクトの構築に使用
     */
    public static class Builder {
        private String name;
        private LocalDate birthDate;
        private List<String> hobbies = new ArrayList<>();
        
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }
        
        public Builder addHobby(String hobby) {
            this.hobbies.add(hobby);
            return this;
        }
        
        public Builder setHobbies(List<String> hobbies) {
            this.hobbies = new ArrayList<>(hobbies);
            return this;
        }
        
        public ImmutablePerson build() {
            return new ImmutablePerson(name, birthDate, hobbies);
        }
    }
    
    /**
     * Builderインスタンスを取得
     */
    public static Builder builder() {
        return new Builder();
    }
}