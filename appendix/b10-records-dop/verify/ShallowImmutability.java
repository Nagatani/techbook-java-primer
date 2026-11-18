import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * レコードの不変性が「浅い」こと（フィールドの参照は変えられないが、参照先は変えられる）を観察する。
 *
 *   javac ShallowImmutability.java
 *   java ShallowImmutability
 */
public class ShallowImmutability {
    record Team(String name, List<String> members) { }

    // コンパクトコンストラクターで変更できないコピーにする
    record SafeTeam(String name, List<String> members) {
        SafeTeam {
            members = List.copyOf(members);
        }
    }

    record Scores(int[] values) { }

    public static void main(String[] args) {
        var list = new ArrayList<>(List.of("Alice"));
        var team = new Team("A", list);
        int before = team.hashCode();
        var set = new HashSet<Team>();
        set.add(team);
        list.add("Bob");                        // 作ったあとに元のリストを変える
        IO.println("Team: " + team);
        IO.println("hashCodeが変わった: " + (before != team.hashCode()));
        IO.println("HashSetのcontains: " + set.contains(team));
        team.members().add("Carol");            // アクセサーで得たリストも変えられる
        IO.println("Team: " + team);

        var list2 = new ArrayList<>(List.of("Alice"));
        var safe = new SafeTeam("B", list2);
        list2.add("Bob");
        IO.println("SafeTeam: " + safe);
        try {
            safe.members().add("Carol");
        } catch (UnsupportedOperationException e) {
            IO.println("SafeTeamのリストは変更できない: " + e.getClass().getName());
        }

        var scores = new Scores(new int[] {1, 2, 3});
        scores.values()[0] = 99;                // 配列の要素は書き換えられる
        IO.println("Scores.values()[0]: " + scores.values()[0]);
        IO.println("ScoresのtoString: " + scores.toString().replaceAll("@[0-9a-f]+", "@..."));
    }
}
