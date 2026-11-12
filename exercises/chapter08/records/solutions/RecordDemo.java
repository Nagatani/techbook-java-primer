import java.time.LocalDate;
import java.util.List;

/**
 * レコードのデモ（課題1〜4の解答例）
 */
public class RecordDemo {

    public static void demonstratePoint() {
        IO.println("=== Pointレコードのデモ ===");
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        IO.println("p1: " + p1);
        IO.println("x座標: " + p1.x() + ", y座標: " + p1.y());
        IO.println("原点からの距離: " + p1.distanceFromOrigin());
        IO.println("(6, 8)までの距離: " + p1.distanceTo(new Point(6, 8)));
        IO.println("(1, -2)だけ移動: " + p1.translate(1, -2) + "（p1は " + p1 + " のまま）");
        IO.println("原点: " + Point.origin());
        IO.println("p1.equals(p2): " + p1.equals(p2) + "、p1 == p2: " + (p1 == p2));
        IO.println("hashCodeが等しい: " + (p1.hashCode() == p2.hashCode()));
        try {
            new Point(Double.NaN, 0);
        } catch (IllegalArgumentException e) {
            // 例外の受け取り方（try-catch）は第13章で学ぶ
            IO.println("作れない点: " + e.getMessage());
        }
    }

    public static void demonstratePerson() {
        IO.println("\n=== Personレコードのデモ ===");
        Person person = new Person("taro yamada", LocalDate.of(2005, 6, 15), "taro@example.com");
        IO.println("person: " + person);
        IO.println("2026年4月1日の年齢: " + person.getAgeOn(LocalDate.of(2026, 4, 1)));
        IO.println("メールのドメイン: " + person.getEmailDomain());
        IO.println("整えた名前: " + person.getFormattedName());
        Person other = Person.of("hanako", "hanako@example.org");
        IO.println("Person.ofで作った人: " + other.name() + "（" + other.getEmailDomain() + "）");
        try {
            new Person("jiro", LocalDate.of(2000, 1, 1), "jiro.example.com");
        } catch (IllegalArgumentException e) {
            IO.println("作れない人: " + e.getMessage());
        }
    }

    public static void demonstrateDTO() {
        IO.println("\n=== DTOパターンのデモ ===");
        ProductEntity entity = new ProductEntity(1L, "Laptop", 120000.0, "Electronics");
        ProductDTO dto = ProductDTO.fromEntity(entity);
        IO.println("dto: " + dto);
        IO.println("税込み価格: " + "%,.0f".formatted(dto.getPriceWithTax()) + "円");
        IO.println("表示用の価格: " + dto.getFormattedPrice());
        IO.println("JSON: " + dto.toJson());
        entity.setPrice(99000.0);
        IO.println("エンティティの価格を変えたあとのDTO: " + dto.getFormattedPrice());
    }

    public static void demonstrateNestedRecords() {
        IO.println("\n=== ネストされたRecordのデモ ===");
        Address address = new Address("千代田1-1", "東京都千代田区", "100-0001", "日本");
        Customer customer = Customer.builder()
                .name("山田太郎")
                .email("yamada@example.com")
                .address(address)
                .phoneNumbers(List.of("03-1234-5678", "090-1234-5678"))
                .build();
        IO.println("要約: " + customer.getSummary());
        IO.println("主な電話番号: " + customer.getPrimaryPhone());
        IO.println("日本の住所: " + customer.address().isJapanese());
        IO.println("住所:");
        IO.println(customer.address().getFormattedAddress());
        try {
            new Address("1 Main St", "Springfield", "AB-123", "USA");
        } catch (IllegalArgumentException e) {
            IO.println("作れない住所: " + e.getMessage());
        }
    }

    public static void demonstrateRecordCollections() {
        IO.println("\n=== Recordを配列で扱う ===");
        Point[] points = {new Point(5, 5), new Point(1, 2), new Point(-3, 0), new Point(1, 2)};

        // 原点にいちばん近い点
        Point nearest = points[0];
        for (Point p : points) {
            if (p.distanceFromOrigin() < nearest.distanceFromOrigin()) {
                nearest = p;
            }
        }
        IO.println("原点にいちばん近い点: " + nearest);

        // 原点から半径4以内の点の数
        int count = 0;
        for (Point p : points) {
            if (p.distanceFromOrigin() <= 4) {
                count++;
            }
        }
        IO.println("原点から半径4以内の点: " + count + "個");

        // equalsが値で比べるので、別々に作った(1, 2)どうしは等しい
        IO.println("points[1].equals(points[3]): " + points[1].equals(points[3]));
        // 第9章のHashSetやHashMapのキーにも、そのまま使える（equalsとhashCodeが自動で作られるため）
        // 第11章で学ぶComparatorを使うと、原点に近い順の並べ替えも1行で書ける:
        // Arrays.sort(points, Comparator.comparingDouble(Point::distanceFromOrigin));
    }

    public static void main(String[] args) {
        demonstratePoint();
        demonstratePerson();
        demonstrateDTO();
        demonstrateNestedRecords();
        demonstrateRecordCollections();
    }
}
