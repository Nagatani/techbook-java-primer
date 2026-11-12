/**
 * 車両のクラスを試すプログラム（課題4の解答例）
 */
public class VehicleTest {
    public static void main(String[] args) {
        IO.println("=== 車両管理システム ===");

        Vehicle[] vehicles = {
            new Car("トヨタ", 5),
            new Motorcycle("ホンダ", 250)
        };
        int[] accelerations = {60, 80};

        for (int i = 0; i < vehicles.length; i++) {
            IO.print("車両" + (i + 1) + ": ");
            vehicles[i].showInfo();
            IO.println();
            vehicles[i].accelerate(accelerations[i]);
            IO.println();
        }

        IO.println("全車両の情報:");
        for (Vehicle vehicle : vehicles) {
            IO.println(vehicle.getSummary());
        }
    }
}
