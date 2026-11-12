public class Bicycle extends Transport {
    private int parkingFeePerDay;

    public Bicycle(int parkingFeePerDay) {
        super("自転車", 15);
        this.parkingFeePerDay = parkingFeePerDay;
    }

    @Override
    public boolean canUse(double distanceKm) {
        return super.canUse(distanceKm) && distanceKm <= 10;
    }

    // 運賃はないが、駐輪場の料金が1日1回かかる
    @Override
    public int dailyCost(double distanceKm) {
        return parkingFeePerDay;
    }
}
