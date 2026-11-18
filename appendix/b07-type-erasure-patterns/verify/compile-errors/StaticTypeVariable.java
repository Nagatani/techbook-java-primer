// 型パラメーターはインスタンスごとに決まるので、static文脈では使えない
class StaticTypeVariable<T> {
    static T shared;
}
