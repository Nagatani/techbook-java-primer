package chapter07.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GameCharacterクラスとその継承クラスのテストクラス
 */
class GameTest {
    
    private Player player;
    private Enemy enemy;
    
    @BeforeEach
    void setUp() {
        player = new Player("勇者", 100, 20);
        enemy = new Enemy("ゴブリン", 50, 15);
    }
    
    @Test
    void testGameCharacterInitialization() {
        assertEquals("勇者", player.getName());
        assertEquals(100, player.getHp());
        assertEquals(20, player.getAttack());
        assertTrue(player.isAlive());
        
        assertEquals("ゴブリン", enemy.getName());
        assertEquals(50, enemy.getHp());
        assertEquals(15, enemy.getAttack());
        assertTrue(enemy.isAlive());
    }
    
    @Test
    void testPlayerSpecialMove() {
        player.performSpecialMove();
        // 特殊技の実行を確認（コンソール出力のため、実際の動作を確認）
        assertTrue(player.isAlive());
    }
    
    @Test
    void testEnemySpecialMove() {
        enemy.performSpecialMove();
        // 特殊技の実行を確認（コンソール出力のため、実際の動作を確認）
        assertTrue(enemy.isAlive());
    }
    
    @Test
    void testDamage() {
        int originalHp = player.getHp();
        player.takeDamage(30);
        assertEquals(originalHp - 30, player.getHp());
        assertTrue(player.isAlive());
    }
    
    @Test
    void testDeath() {
        player.takeDamage(150); // HPを上回るダメージ
        assertFalse(player.isAlive());
        assertEquals(0, player.getHp());
    }
    
    @Test
    void testDrawable() {
        // プレイヤーとエネミーがDrawableインターフェースを実装していることを確認
        assertTrue(player instanceof Drawable);
        assertTrue(enemy instanceof Drawable);
        
        // draw()メソッドを呼び出しても例外が発生しないことを確認
        assertDoesNotThrow(() -> player.draw());
        assertDoesNotThrow(() -> enemy.draw());
    }
    
    @Test
    void testPolymorphism() {
        // 多態性のテスト
        GameCharacter[] characters = {player, enemy};
        
        for (GameCharacter character : characters) {
            // 基底クラスの参照で派生クラスのメソッドを呼び出し
            assertDoesNotThrow(() -> character.performSpecialMove());
            assertTrue(character.isAlive());
        }
    }
    
    @Test
    void testDrawablePolymorphism() {
        // Drawableインターフェースを通じた多態性のテスト
        Drawable[] drawables = {player, enemy};
        
        for (Drawable drawable : drawables) {
            assertDoesNotThrow(() -> drawable.draw());
        }
    }
}