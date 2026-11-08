public record CharacterStats(int level, int health, int attack, int magic) {
    public CharacterStats levelUp() {
        return new CharacterStats(
            level + 1,
            health + 10,
            attack + 2,
            magic + 2
        );
    }
}
