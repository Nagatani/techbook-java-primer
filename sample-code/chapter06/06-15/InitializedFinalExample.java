public class InitializedFinalExample {
    private final String name;
    
    public InitializedFinalExample(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        this.name = name; // コンストラクタで初期化
    }
    
    public String getName() {
        return name;
    }
}
