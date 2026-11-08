public enum OrderStatus {
    PENDING("注文受付", false),
    PROCESSING("処理中", false),
    SHIPPED("発送済み", false),
    DELIVERED("配達完了", true),
    CANCELLED("キャンセル", true);
    
    private final String description;
    private final boolean isFinal;
    
    OrderStatus(String description, boolean isFinal) {
        this.description = description;
        this.isFinal = isFinal;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
}
