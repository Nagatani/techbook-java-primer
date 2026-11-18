/** レコードはextendsでクラスを継承できない（スーパークラスはjava.lang.Recordに決まっている） */
public class RecordExtends {
    static class Base { }

    record Point(int x, int y) extends Base { }
}
