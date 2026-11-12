public class Walk extends Transport {
    public Walk() {
        super("徒歩", 4);
    }

    // 3kmを超えると歩いては通わない
    @Override
    public boolean canUse(double distanceKm) {
        return super.canUse(distanceKm) && distanceKm <= 3;
    }
}
