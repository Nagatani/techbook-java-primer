public record Employee(String name, String id, Company company) {
    // 従業員の完全な住所を取得
    public String getFullAddress() {
        return String.format("%s works at %s, located at %s, %s %s",
            name,
            company.name(),
            company.headquarters().street(),
            company.headquarters().city(),
            company.headquarters().country()
        );
    }
}
