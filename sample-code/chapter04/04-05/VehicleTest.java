import com.example.cars.Car;

public class VehicleTest {
    public static void main(String[] args) {
        Car car = new Car();
        car.initialize();
        
        // protectedメンバーは別パッケージから直接アクセスできない
        // IO.println(car.engineType); // コンパイルエラー
    }
}
