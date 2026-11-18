import java.util.List;

// スーパータイプトークンを抽象クラスにしておくと、{ }の書き忘れがコンパイルエラーになる
class TokenWithoutBody {
    abstract static class TypeRef<T> { }

    Object token = new TypeRef<List<String>>();
}
