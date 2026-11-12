public class Bus extends PublicTransport {
    public Bus() {
        super("バス", 18, 10);
    }

    // 距離によらず同じ運賃
    @Override
    public int fare(double distanceKm) {
        return 230;
    }
}
