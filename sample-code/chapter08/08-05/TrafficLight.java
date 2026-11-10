public class TrafficLight {
    public enum Color {
        RED, YELLOW, GREEN
    }
    
    public void handleSignal(Color color) {
        switch (color) {
            case RED:
                IO.println("停止してください");
                break;
            case YELLOW:
                IO.println("注意して進んでください");
                break;
            case GREEN:
                IO.println("進んでください");
                break;
        }
    }
}
