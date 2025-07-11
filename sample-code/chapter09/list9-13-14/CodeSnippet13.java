/**
 * リスト9-13
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (473行目)
 */

// ネストしたRecord構造
public record Address(String street, String city, String country) {}
public record Person(String name, int age, Address address) {}
public record Company(String name, Person ceo, List<Person> employees) {}

// 深いパターンマッチング
public static String getLocationInfo(Object obj) {
    return switch (obj) {
        // ネストしたRecordの分解
        case Person(var name, var age, Address(var street, var city, "Japan")) ->
            name + "さんは日本の" + city + "在住です";
        
        case Person(var name, var age, Address(var street, var city, var country)) ->
            name + "さんは" + country + "の" + city + "在住です";
        
        // リストのパターンマッチング
        case Company(var companyName, Person(var ceoName, _, _), var employees) 
            when employees.size() > 100 ->
            companyName + "は" + ceoName + "がCEOの大企業です";
        
        case Company(var companyName, Person(var ceoName, _, _), var employees) ->
            companyName + "は" + ceoName + "がCEOの会社です（従業員" + employees.size() + "名）";
        
        default -> "不明な情報です";
    };
}