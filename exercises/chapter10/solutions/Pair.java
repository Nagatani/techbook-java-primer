package chapter10.solutions;

import java.util.Objects;

/**
 * 2つの値の組（課題2の解答例）
 *
 * @param <F> 1つ目の値の型
 * @param <S> 2つ目の値の型
 */
public class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    /**
     * 値の順序を入れ替えた新しい組を返す。型パラメーターの順序も入れ替わる
     */
    public Pair<S, F> swap() {
        return new Pair<>(second, first);
    }

    public void displayInfo() {
        IO.println("Pair情報:");
        IO.println("First (" + typeName(first) + "): " + first);
        IO.println("Second (" + typeName(second) + "): " + second);
    }

    private static String typeName(Object value) {
        if (value == null) {
            return "null";
        }
        return value.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Pair{first=" + first + ", second=" + second + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // 型パラメーターは実行時に消えるので、Pair<F, S>かどうかは確かめられない。Pair<?, ?>にキャストする
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
