// 本文では省略している補助クラス
record User(String userId, String name) {
    String getName() { return name; }
}

record Order(String orderId, int amount) {
    int getAmount() { return amount; }
}
