// 型パラメーターのクラスリテラルは書けない
class ClassLiteral<T> {
    Class<?> type() {
        return T.class;
    }
}
