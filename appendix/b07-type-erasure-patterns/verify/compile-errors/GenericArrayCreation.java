// 型パラメーターの配列は作れない
class GenericArrayCreation<T> {
    T[] create(int size) {
        return new T[size];
    }
}
