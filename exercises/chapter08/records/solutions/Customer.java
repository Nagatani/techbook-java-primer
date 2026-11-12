import java.util.List;

/**
 * 顧客（課題4の解答例）。レコードの中に、別のレコード（Address）を持つ
 *
 * List<String>は第9章で学ぶ。ここでは「List.copyOf(リスト)で、変更できないコピーを作れる」ことだけを使う。
 */
public record Customer(String name, String email, Address address, List<String> phoneNumbers) {

    public Customer {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            throw new IllegalArgumentException("電話番号は1つ以上要ります");
        }
        // 受け取ったリストを変更できないリストにコピーして、フィールドに入れる（防御的コピー）
        phoneNumbers = List.copyOf(phoneNumbers);
    }

    public String getPrimaryPhone() {
        return phoneNumbers.get(0);
    }

    public String getSummary() {
        return name + " (" + email + ") - " + address.city() + ", " + address.country();
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    // 項目の多いレコードを、名前を付けながら組み立てるビルダー（第6章）
    public static class CustomerBuilder {
        private String name;
        private String email;
        private Address address;
        private List<String> phoneNumbers;

        public CustomerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CustomerBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CustomerBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public CustomerBuilder phoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        public Customer build() {
            return new Customer(name, email, address, phoneNumbers);
        }
    }
}
