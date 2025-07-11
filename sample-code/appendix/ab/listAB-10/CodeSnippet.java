/**
 * リストAB-10
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (251行目)
 */

// チャーチ数の実装
interface ChurchNumeral {
    <T> Function<T, T> apply(Function<T, T> f);
}

// 0 = λf.λx.x
ChurchNumeral zero = new ChurchNumeral() {
    @Override
    public <T> Function<T, T> apply(Function<T, T> f) {
        return x -> x;
    }
};

// 1 = λf.λx.f x
ChurchNumeral one = new ChurchNumeral() {
    @Override
    public <T> Function<T, T> apply(Function<T, T> f) {
        return x -> f.apply(x);
    }
};

// 2 = λf.λx.f (f x)
ChurchNumeral two = new ChurchNumeral() {
    @Override
    public <T> Function<T, T> apply(Function<T, T> f) {
        return x -> f.apply(f.apply(x));
    }
};

// 後者関数：succ = λn.λf.λx.f (n f x)
ChurchNumeral succ(ChurchNumeral n) {
    return new ChurchNumeral() {
        @Override
        public <T> Function<T, T> apply(Function<T, T> f) {
            return x -> f.apply(n.apply(f).apply(x));
        }
    };
}

// チャーチ数を通常の整数に変換
int toInt(ChurchNumeral n) {
    return n.<Integer>apply(x -> x + 1).apply(0);
}

// 使用例
System.out.println(toInt(zero));        // 0
System.out.println(toInt(one));         // 1
System.out.println(toInt(succ(two)));   // 3