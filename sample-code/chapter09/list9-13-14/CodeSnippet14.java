/**
 * リスト9-14
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (507行目)
 */

// 年齢による分類（ガード条件付き）
public static String categorizeAge(Person person) {
    return switch (person) {
        case Person(var name, var age, _) when age < 18 -> 
            name + "さんは未成年です";
        case Person(var name, var age, _) when age >= 65 -> 
            name + "さんは高齢者です";
        case Person(var name, var age, Address(_, _, "Japan")) when age >= 20 ->
            name + "さんは日本で飲酒可能な成人です";
        case Person(var name, _, _) -> 
            name + "さんは成人です";
    };
}