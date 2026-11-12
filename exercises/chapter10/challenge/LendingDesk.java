import java.util.ArrayList;
import java.util.List;

/**
 * 備品の貸出窓口。会員と備品を、同じRepositoryクラスで管理する
 */
public class LendingDesk {
    private static final int LOAN_DAYS = 7;
    private static final int MAX_LOANS = 2;  // 1人が同時に借りられる数

    private final Repository<String, Member> members = new Repository<>();
    private final Repository<Integer, Equipment> equipment = new Repository<>();

    public Repository<String, Member> getMembers() {
        return members;
    }

    public Repository<Integer, Equipment> getEquipment() {
        return equipment;
    }

    public String lend(String memberId, int equipmentId, int day) {
        Member member = members.find(memberId);
        if (member == null) {
            return memberId + "という会員はいません";
        }
        Equipment item = equipment.find(equipmentId);
        if (item == null) {
            return equipmentId + "という備品はありません";
        }
        if (item.isOnLoan()) {
            return item.getName() + "（" + equipmentId + "）は貸出中です";
        }
        if (loansOf(memberId).size() >= MAX_LOANS) {
            return member.name() + "さんはすでに" + MAX_LOANS + "点借りています";
        }
        item.lendTo(memberId, day + LOAN_DAYS);
        return day + "日: " + member.name() + "さんに" + item.getName() + "（" + equipmentId
                + "）を貸しました（" + item.getDueDay() + "日まで）";
    }

    public String giveBack(int equipmentId, int day) {
        Equipment item = equipment.find(equipmentId);
        if (item == null || !item.isOnLoan()) {
            return equipmentId + "は貸し出していません";
        }
        int lateDays = day - item.getDueDay();
        item.giveBack();
        String message = day + "日: " + item.getName() + "（" + equipmentId + "）が返却されました";
        if (lateDays > 0) {
            message += "（" + lateDays + "日遅れ）";
        }
        return message;
    }

    // 会員が借りている備品
    public List<Equipment> loansOf(String memberId) {
        List<Equipment> result = new ArrayList<>();
        for (Equipment item : equipment.all()) {
            if (memberId.equals(item.getBorrowerId())) {
                result.add(item);
            }
        }
        return result;
    }

    // 指定した日に返却期限を過ぎている備品
    public List<Equipment> overdue(int day) {
        List<Equipment> result = new ArrayList<>();
        for (Equipment item : equipment.all()) {
            if (item.isOnLoan() && item.getDueDay() < day) {
                result.add(item);
            }
        }
        return result;
    }
}
