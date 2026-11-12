/**
 * 取引をした窓口。出金と振込の手数料を窓口ごとに持つ
 */
public enum Channel {
    ATM("ATM", 110, 220),
    COUNTER("窓口", 0, 660),
    ONLINE("ネット", 0, 110);

    private final String label;
    private final int withdrawalFee;
    private final int transferFee;

    Channel(String label, int withdrawalFee, int transferFee) {
        this.label = label;
        this.withdrawalFee = withdrawalFee;
        this.transferFee = transferFee;
    }

    public String getLabel() {
        return label;
    }

    public int getWithdrawalFee() {
        return withdrawalFee;
    }

    public int getTransferFee() {
        return transferFee;
    }
}
