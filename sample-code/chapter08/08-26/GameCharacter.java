public record GameCharacter(
    String playerName,
    CharacterClass characterClass,
    CharacterStats stats
) {
    public static GameCharacter create(String playerName, CharacterClass characterClass) {
        return new GameCharacter(
            playerName,
            characterClass,
            new CharacterStats(
                1,
                characterClass.getBaseHealth(),
                characterClass.getBaseAttack(),
                characterClass.getBaseMagic()
            )
        );
    }
    
    public GameCharacter levelUp() {
        return new GameCharacter(playerName, characterClass, stats.levelUp());
    }
}
