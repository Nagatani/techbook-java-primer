public record Employee(String name, String id, Company company) {
    // 従業員の勤務先と、その所在地を返す
    public String getFullAddress() {
        return String.format("%sの勤務先は%s（所在地: %s %s %s）",
            name,
            company.name(),
            company.headquarters().country(),
            company.headquarters().city(),
            company.headquarters().street()
        );
    }
}
