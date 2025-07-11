/**
 * リストAB-20
 * ParserCombinatorクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (906行目)
 */

public class ParserCombinator {
    
    // パーサーの基本型
    @FunctionalInterface
    public interface Parser<T> {
        Optional<Pair<T, String>> parse(String input);
    }
    
    // 補助クラス
    public static class Pair<A, B> {
        public final A first;
        public final B second;
        
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
    
    // 基本的なパーサー
    public static Parser<Character> charParser(char c) {
        return input -> {
            if (input.isEmpty() || input.charAt(0) != c) {
                return Optional.empty();
            }
            return Optional.of(new Pair<>(c, input.substring(1)));
        };
    }
    
    public static Parser<String> stringParser(String s) {
        return input -> {
            if (input.startsWith(s)) {
                return Optional.of(new Pair<>(s, input.substring(s.length())));
            }
            return Optional.empty();
        };
    }
    
    // パーサーコンビネータ
    public static <T> Parser<T> or(Parser<T> p1, Parser<T> p2) {
        return input -> {
            Optional<Pair<T, String>> result = p1.parse(input);
            if (result.isPresent()) {
                return result;
            }
            return p2.parse(input);
        };
    }
    
    public static <A, B> Parser<Pair<A, B>> and(Parser<A> p1, Parser<B> p2) {
        return input -> p1.parse(input).flatMap(r1 ->
            p2.parse(r1.second).map(r2 ->
                new Pair<>(new Pair<>(r1.first, r2.first), r2.second)
            )
        );
    }
    
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> {
            List<T> results = new ArrayList<>();
            String remaining = input;
            
            while (true) {
                Optional<Pair<T, String>> result = parser.parse(remaining);
                if (result.isEmpty()) {
                    break;
                }
                results.add(result.get().first);
                remaining = result.get().second;
            }
            
            return Optional.of(new Pair<>(results, remaining));
        };
    }
    
    // 数値パーサーの例
    public static Parser<Integer> digit() {
        return input -> {
            if (input.isEmpty() || !Character.isDigit(input.charAt(0))) {
                return Optional.empty();
            }
            int digit = Character.getNumericValue(input.charAt(0));
            return Optional.of(new Pair<>(digit, input.substring(1)));
        };
    }
    
    public static Parser<Integer> number() {
        return input -> {
            Parser<List<Integer>> digits = many(digit());
            return digits.parse(input).flatMap(result -> {
                if (result.first.isEmpty()) {
                    return Optional.empty();
                }
                int value = result.first.stream()
                    .reduce(0, (acc, d) -> acc * 10 + d);
                return Optional.of(new Pair<>(value, result.second));
            });
        };
    }
    
    public static void main(String[] args) {
        // 使用例
        Parser<Character> aParser = charParser('a');
        Parser<Character> bParser = charParser('b');
        Parser<Character> aOrB = or(aParser, bParser);
        
        System.out.println(aOrB.parse("abc")); // Optional[(a, bc)]
        System.out.println(aOrB.parse("bcd")); // Optional[(b, cd)]
        System.out.println(aOrB.parse("xyz")); // Optional.empty
        
        // 数値のパース
        Parser<Integer> num = number();
        System.out.println(num.parse("123abc")); // Optional[(123, abc)]
        
        // 複雑なパーサーの組み合わせ
        Parser<Pair<String, Integer>> assignment = 
            and(stringParser("x="), number());
        System.out.println(assignment.parse("x=42")); // Optional[((x=, 42), )]
    }
}