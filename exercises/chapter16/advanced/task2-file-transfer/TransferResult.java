/**
 * 1つのファイルを送った結果
 */
public record TransferResult(String fileName, long size, boolean sizeMatched, boolean checksumMatched) {
    public boolean succeeded() {
        return sizeMatched && checksumMatched;
    }
}
