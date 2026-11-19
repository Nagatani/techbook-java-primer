// 第9章：SequencedCollection・SequencedSet・SequencedMap（Java 21で追加、JEP 431）
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;

public class Sequenced {
    static SequencedCollection<String> c;
    static SequencedSet<String> s;
    static SequencedMap<String, Integer> m;
}
