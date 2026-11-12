/**
 * パーティーとモンスターの戦闘を進めるクラス
 *
 * 味方の行動はact()を呼ぶだけで、職業を調べない（instanceofを使わない）。
 */
public class Battle {
    private GameCharacter[] party;
    private GameCharacter monster;

    public Battle(GameCharacter[] party, GameCharacter monster) {
        this.party = party;
        this.monster = monster;
    }

    public void run(int maxTurns) {
        for (int turn = 1; turn <= maxTurns; turn++) {
            IO.println("--- ターン" + turn + " ---");
            for (GameCharacter member : party) {
                if (member.isAlive() && monster.isAlive()) {
                    member.act(party, monster);
                }
            }
            if (!monster.isAlive()) {
                IO.println(monster.getName() + "をたおした");
                return;
            }
            GameCharacter target = frontMember();
            monster.attack(target);
            if (frontMember() == null) {
                IO.println("パーティーは全滅した");
                return;
            }
        }
        IO.println(maxTurns + "ターンで決着がつかなかった");
    }

    // 前に並んでいる、まだ戦える味方
    private GameCharacter frontMember() {
        for (GameCharacter member : party) {
            if (member.isAlive()) {
                return member;
            }
        }
        return null;
    }

    public void showStatus() {
        for (GameCharacter member : party) {
            IO.println("  " + member);
        }
        IO.println("  " + monster);
    }
}
