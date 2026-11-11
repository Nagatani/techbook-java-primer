public class TrafficLightDemo {
    public static void main(String[] args) {
        TrafficLight light = new TrafficLight();
        IO.println("信号のテスト:");
        for (TrafficLight.Color color : TrafficLight.Color.values()) {
            light.handleSignal(color);
        }
    }
}
