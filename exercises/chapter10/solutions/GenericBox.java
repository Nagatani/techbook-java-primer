package chapter10.solutions;

import java.util.*;

/**
 * ジェネリクスを活用した汎用ボックスクラス
 * 
 * 型安全性を保ちながら、様々な型のオブジェクトを格納できます。
 * 境界ワイルドカードやジェネリクスメソッドの使用例を含みます。
 */
public class GenericBox<T> {
    
    private T content;
    private List<T> history;
    private final Class<T> type;
    
    /**
     * GenericBoxのコンストラクタ
     * 
     * @param type 格納する型のClassオブジェクト
     */
    public GenericBox(Class<T> type) {
        this.type = type;
        this.history = new ArrayList<>();
    }
    
    /**
     * 内容を設定する
     * 
     * @param content 設定する内容
     */
    public void setContent(T content) {
        if (this.content != null) {
            history.add(this.content);
        }
        this.content = content;
    }
    
    /**
     * 内容を取得する
     * 
     * @return 格納されている内容
     */
    public T getContent() {
        return content;
    }
    
    /**
     * ボックスが空かどうかを判定する
     * 
     * @return 空の場合true
     */
    public boolean isEmpty() {
        return content == null;
    }
    
    /**
     * 内容をクリアする
     */
    public void clear() {
        if (content != null) {
            history.add(content);
            content = null;
        }
    }
    
    /**
     * 履歴を取得する
     * 
     * @return 過去に格納されていた内容のリスト
     */
    public List<T> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * 履歴をクリアする
     */
    public void clearHistory() {
        history.clear();
    }
    
    /**
     * 型情報を取得する
     * 
     * @return 格納する型のClassオブジェクト
     */
    public Class<T> getType() {
        return type;
    }
    
    /**
     * 別のボックスから内容をコピーする（共変性の例）
     * 
     * @param other コピー元のボックス
     * @param <U> コピー元の型（Tのサブタイプ）
     */
    public <U extends T> void copyFrom(GenericBox<U> other) {
        if (!other.isEmpty()) {
            setContent(other.getContent());
        }
    }
    
    /**
     * 別のボックスに内容をコピーする（反変性の例）
     * 
     * 型パラメータには下限（super）を指定できないため、ワイルドカード「? super T」を使う
     *
     * @param other コピー先のボックス（中身の型はTかそのスーパータイプ）
     */
    public void copyTo(GenericBox<? super T> other) {
        if (!isEmpty()) {
            other.setContent(getContent());
        }
    }
    
    /**
     * 内容が指定した型のインスタンスかどうかを判定する
     * 
     * @param clazz 判定する型
     * @param <U> 判定する型
     * @return 指定した型のインスタンスの場合true
     */
    public <U> boolean isInstanceOf(Class<U> clazz) {
        return content != null && clazz.isInstance(content);
    }
    
    /**
     * 内容を指定した型にキャストして取得する
     * 
     * @param clazz キャスト先の型
     * @param <U> キャスト先の型
     * @return キャストされた内容
     * @throws ClassCastException キャストできない場合
     */
    public <U> U getContentAs(Class<U> clazz) {
        if (content == null) {
            return null;
        }
        if (clazz.isInstance(content)) {
            return clazz.cast(content);
        }
        throw new ClassCastException("Cannot cast " + content.getClass() + " to " + clazz);
    }
    
    /**
     * 内容に対して操作を実行する
     * 
     * @param operation 実行する操作
     * @param <R> 操作の戻り値の型
     * @return 操作の結果
     */
    public <R> R applyOperation(java.util.function.Function<T, R> operation) {
        if (content == null) {
            return null;
        }
        return operation.apply(content);
    }
    
    /**
     * 内容が条件を満たすかどうかを判定する
     * 
     * @param predicate 判定条件
     * @return 条件を満たす場合true
     */
    public boolean matches(java.util.function.Predicate<T> predicate) {
        return content != null && predicate.test(content);
    }
    
    /**
     * 複数のボックスの内容を比較する（共変ワイルドカードの例）
     * 
     * @param boxes 比較するボックスのリスト
     * @param <U> 比較する型（Tのサブタイプ）
     * @return すべてのボックスの内容が等しい場合true
     */
    public static <T, U extends T> boolean allEqual(List<? extends GenericBox<U>> boxes) {
        if (boxes.isEmpty()) {
            return true;
        }
        
        T firstContent = boxes.get(0).getContent();
        return boxes.stream()
                   .allMatch(box -> Objects.equals(box.getContent(), firstContent));
    }
    
    /**
     * 複数のボックスに同じ内容を設定する（反変ワイルドカードの例）
     * 
     * @param boxes 設定先のボックスのリスト
     * @param content 設定する内容
     * @param <T> 設定する型
     */
    public static <T> void setAll(List<? super GenericBox<T>> boxes, T content) {
        // Note: この例では具体的な実装は困難ですが、概念を示しています
        // 実際の用途では、より具体的な型関係がある場合に有効です
    }
    
    /**
     * ボックスの内容を交換する
     * 
     * @param other 交換相手のボックス
     */
    public void swap(GenericBox<T> other) {
        T temp = this.content;
        this.content = other.content;
        other.content = temp;
    }
    
    /**
     * ボックスの内容を文字列として取得する
     * 
     * @return 内容の文字列表現
     */
    @Override
    public String toString() {
        return String.format("GenericBox<%s>{content=%s, historySize=%d}",
                           type.getSimpleName(), content, history.size());
    }
    
    /**
     * ボックスの等価性を判定する
     * 
     * @param obj 比較対象
     * @return 等しい場合true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GenericBox<?> that = (GenericBox<?>) obj;
        return Objects.equals(content, that.content) && 
               Objects.equals(type, that.type);
    }
    
    /**
     * ハッシュコードを取得する
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(content, type);
    }
    
    /**
     * ファクトリーメソッド：値を持つボックスを作成する
     * 
     * @param value 初期値
     * @param type 型のClassオブジェクト
     * @param <T> 型パラメータ
     * @return 作成されたボックス
     */
    public static <T> GenericBox<T> of(T value, Class<T> type) {
        GenericBox<T> box = new GenericBox<>(type);
        box.setContent(value);
        return box;
    }
    
    /**
     * ファクトリーメソッド：空のボックスを作成する
     * 
     * @param type 型のClassオブジェクト
     * @param <T> 型パラメータ
     * @return 作成されたボックス
     */
    public static <T> GenericBox<T> empty(Class<T> type) {
        return new GenericBox<>(type);
    }
}
