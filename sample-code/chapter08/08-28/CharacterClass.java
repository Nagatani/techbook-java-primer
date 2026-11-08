// ゲームのキャラクタークラス
public enum CharacterClass {
    WARRIOR("戦士", 100, 20, 10),
    MAGE("魔法使い", 60, 10, 30),
    ARCHER("弓使い", 80, 15, 15);
    
    private final String name;
    private final int baseHealth;
    private final int baseAttack;
    private final int baseMagic;
    
    CharacterClass(String name, int baseHealth, int baseAttack, int baseMagic) {
        this.name = name;
        this.baseHealth = baseHealth;
        this.baseAttack = baseAttack;
        this.baseMagic = baseMagic;
    }
    
    // ゲッターメソッド省略
    public String getName() { return name; }
    public int getBaseHealth() { return baseHealth; }
    public int getBaseAttack() { return baseAttack; }
    public int getBaseMagic() { return baseMagic; }
}
