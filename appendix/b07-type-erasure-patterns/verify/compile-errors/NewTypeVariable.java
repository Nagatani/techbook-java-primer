// 型パラメーターのインスタンスは作れない（実行時にTが何かわからない）
class NewTypeVariable<T> {
    T create() {
        return new T();
    }
}
