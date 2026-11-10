public class GameDemo {
    public static void main(String[] args) {
        GameCharacter hero = GameCharacter.create("アリス", CharacterClass.MAGE);
        GameCharacter leveled = hero.levelUp().levelUp();
        IO.println(hero);
        IO.println(leveled);
    }
}
