public class TrafficLight {
    public enum Color {
        RED, YELLOW, GREEN
    }
    
    public void handleSignal(Color color) {
        switch (color) {
            case RED:
                System.out.println("停止してください");
                break;
            case YELLOW:
                System.out.println("注意して進んでください");
                break;
            case GREEN:
                System.out.println("進んでください");
                break;
        }
    }
}
