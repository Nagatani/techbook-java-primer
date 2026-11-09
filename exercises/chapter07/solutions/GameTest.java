import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameCharacterクラスとその継承クラス（Player・Enemy）のテストクラス
 */
class GameTest {

    private Player player;
    private Enemy enemy;

    @BeforeEach
    void setUp() {
        player = new Player("勇者", 0, 0);
        enemy = new Enemy("ゴブリン", Enemy.EnemyType.GOBLIN, 10, 0);
    }

    @Test
    void testGameCharacterInitialization() {
        assertEquals("勇者", player.getName());
        assertTrue(player.isAlive());
        assertEquals(player.getMaxHealth(), player.getCurrentHealth());

        assertEquals("ゴブリン", enemy.getName());
        assertTrue(enemy.isAlive());
        assertEquals(enemy.getMaxHealth(), enemy.getCurrentHealth());
    }

    @Test
    void testPlayerSpecialSkill() {
        assertDoesNotThrow(() -> player.useSpecialSkill());
        assertTrue(player.isAlive());
    }

    @Test
    void testEnemySpecialSkill() {
        assertDoesNotThrow(() -> enemy.useSpecialSkill());
        assertTrue(enemy.isAlive());
    }

    @Test
    void testDamage() {
        int originalHealth = player.getCurrentHealth();
        int damage = player.getDefense() + 10;  // 防御力を差し引いて10のダメージ
        player.takeDamage(damage);
        assertEquals(originalHealth - 10, player.getCurrentHealth());
        assertTrue(player.isAlive());
    }

    @Test
    void testDeath() {
        // 最大体力と防御力を上回るダメージ
        player.takeDamage(player.getMaxHealth() + player.getDefense() + 1);
        assertFalse(player.isAlive());
        assertEquals(0, player.getCurrentHealth());
    }

    @Test
    void testDrawable() {
        // プレイヤーとエネミーがDrawableインターフェイスを実装していることを確認
        assertInstanceOf(Drawable.class, player);
        assertInstanceOf(Drawable.class, enemy);

        // draw()メソッドを呼び出しても例外が発生しないことを確認
        assertDoesNotThrow(() -> player.draw());
        assertDoesNotThrow(() -> enemy.draw());
    }

    @Test
    void testPolymorphism() {
        // 基底クラスの参照で派生クラスのメソッドを呼び出す
        GameCharacter[] characters = {player, enemy};

        for (GameCharacter character : characters) {
            assertDoesNotThrow(character::useSpecialSkill);
            assertTrue(character.isAlive());
        }
    }

    @Test
    void testDrawablePolymorphism() {
        // Drawableインターフェイスを通じた多態性のテスト
        Drawable[] drawables = {player, enemy};

        for (Drawable drawable : drawables) {
            assertDoesNotThrow(drawable::draw);
        }
    }
}
