// 不変オブジェクトの実装
public final class ImmutablePoint {
    private final int x;
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    // setterメソッドは存在しない
    
    // 値を変更したい場合は新しいオブジェクトを作成
    public ImmutablePoint withX(int newX) {
        return new ImmutablePoint(newX, this.y);
    }
    
    public ImmutablePoint withY(int newY) {
        return new ImmutablePoint(this.x, newY);
    }
}
