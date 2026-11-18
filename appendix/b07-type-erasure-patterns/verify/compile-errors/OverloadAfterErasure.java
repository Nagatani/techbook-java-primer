// 型パラメーターだけが違うオーバーロードも、消去後に同じシグネチャになる
class OverloadAfterErasure<T, U> {
    void put(T value) { }
    void put(U value) { }
}
