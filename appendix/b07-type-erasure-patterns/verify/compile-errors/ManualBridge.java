// ブリッジメソッドと同じシグネチャのメソッドを自分で書くと衝突する
class ManualBridge implements Comparable<ManualBridge> {
    public int compareTo(ManualBridge other) { return 0; }
    public int compareTo(Object other) { return 0; }
}
