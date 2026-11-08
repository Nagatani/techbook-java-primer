public class GameDemo {
    public static void main(String[] args) {
        GameCharacter hero = GameCharacter.create("アリス", CharacterClass.MAGE);
        GameCharacter leveled = hero.levelUp().levelUp();
        System.out.println(hero);
        System.out.println(leveled);
    }
}
