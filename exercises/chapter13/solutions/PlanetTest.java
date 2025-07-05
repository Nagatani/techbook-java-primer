import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Planetクラスのテストクラス
 */
public class PlanetTest {
    
    @Test
    public void testPlanetProperties() {
        // 地球の質量と半径をテスト
        Planet earth = Planet.EARTH;
        assertEquals(5.976e+24, earth.getMass(), 1e+20);
        assertEquals(6.37814e6, earth.getRadius(), 1e+3);
    }
    
    @Test
    public void testSurfaceGravity() {
        // 地球の表面重力は約9.8 m/s²
        Planet earth = Planet.EARTH;
        double gravity = earth.surfaceGravity();
        assertEquals(9.8, gravity, 0.1);
        
        // 木星の重力は地球より大きい
        Planet jupiter = Planet.JUPITER;
        assertTrue(jupiter.surfaceGravity() > earth.surfaceGravity());
        
        // 火星の重力は地球より小さい
        Planet mars = Planet.MARS;
        assertTrue(mars.surfaceGravity() < earth.surfaceGravity());
    }
    
    @Test
    public void testSurfaceWeight() {
        // 地球上で70kgの人の重量
        Planet earth = Planet.EARTH;
        double weightOnEarth = earth.surfaceWeight(70.0);
        assertEquals(686.0, weightOnEarth, 10.0);
        
        // 月には含まれていないので、火星でテスト
        Planet mars = Planet.MARS;
        double weightOnMars = mars.surfaceWeight(70.0);
        assertTrue(weightOnMars < weightOnEarth);
    }
    
    @Test
    public void testGetJapaneseName() {
        assertEquals("地球", Planet.EARTH.getJapaneseName());
        assertEquals("火星", Planet.MARS.getJapaneseName());
        assertEquals("木星", Planet.JUPITER.getJapaneseName());
        assertEquals("水星", Planet.MERCURY.getJapaneseName());
    }
    
    @Test
    public void testGetDistanceRank() {
        // 地球から最も近い惑星は金星
        assertEquals(1, Planet.VENUS.getDistanceRank());
        
        // 地球自身は順位0
        assertEquals(0, Planet.EARTH.getDistanceRank());
        
        // 火星は2番目に近い
        assertEquals(2, Planet.MARS.getDistanceRank());
        
        // 海王星は最も遠い
        assertEquals(7, Planet.NEPTUNE.getDistanceRank());
    }
    
    @Test
    public void testAllPlanets() {
        Planet[] planets = Planet.values();
        assertEquals(8, planets.length);
        
        // すべての惑星で重力計算が正常に動作することを確認
        for (Planet planet : planets) {
            assertTrue(planet.surfaceGravity() > 0);
            assertTrue(planet.getMass() > 0);
            assertTrue(planet.getRadius() > 0);
            assertNotNull(planet.getJapaneseName());
        }
    }
}