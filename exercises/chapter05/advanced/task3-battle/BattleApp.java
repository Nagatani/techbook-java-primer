public class BattleApp {
    public static void main(String[] args) {
        GameCharacter[] party = {
            new Knight("アーサー", 120, 30, 12),
            new Mage("マリン", 60, 8, 4, 30, 40),
            new Healer("セーラ", 70, 10, 6, 35),
        };
        GameCharacter dragon = new GameCharacter("ドラゴン", 230, 56, 14);

        Battle battle = new Battle(party, dragon);
        IO.println("=== 戦闘の前 ===");
        battle.showStatus();
        IO.println();
        battle.run(10);
        IO.println();
        IO.println("=== 戦闘の後 ===");
        battle.showStatus();
    }
}
