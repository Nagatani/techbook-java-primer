/**
 * リストAB-17
 * BiFunctorクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (672行目)
 */

// 二重ファンクタの扱い
public class BiFunctor {
    // Optional<List<T>>の変換
    public static <T, R> Optional<List<R>> mapOptionalList(
            Optional<List<T>> optList, 
            Function<T, R> f) {
        return optList.map(list -> 
            list.stream()
                .map(f)
                .collect(Collectors.toList())
        );
    }
    
    // List<Optional<T>>の変換
    public static <T, R> List<Optional<R>> mapListOptional(
            List<Optional<T>> listOpt, 
            Function<T, R> f) {
        return listOpt.stream()
            .map(opt -> opt.map(f))
            .collect(Collectors.toList());
    }
    
    // トラバース：List<Optional<T>> → Optional<List<T>>
    public static <T> Optional<List<T>> sequence(List<Optional<T>> listOpt) {
        return listOpt.stream()
            .reduce(
                Optional.of(new ArrayList<>()),
                (acc, opt) -> acc.flatMap(list -> 
                    opt.map(value -> {
                        List<T> newList = new ArrayList<>(list);
                        newList.add(value);
                        return newList;
                    })
                ),
                (a, b) -> a  // 並列処理では使用されない
            );
    }
}

// 使用例
List<Optional<Integer>> maybeNumbers = List.of(
    Optional.of(1),
    Optional.of(2),
    Optional.of(3)
);

Optional<List<Integer>> allNumbers = BiFunctor.sequence(maybeNumbers);
System.out.println(allNumbers); // Optional[[1, 2, 3]]

// 一つでもemptyがあれば全体がempty
List<Optional<Integer>> withEmpty = List.of(
    Optional.of(1),
    Optional.empty(),
    Optional.of(3)
);

Optional<List<Integer>> result = BiFunctor.sequence(withEmpty);
System.out.println(result); // Optional.empty