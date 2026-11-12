/**
 * 1コマの授業。科目名と教室を持つ不変クラス
 */
public final class Lesson {
    private final String subject;
    private final String room;

    public Lesson(String subject, String room) {
        if (subject == null || subject.isBlank() || room == null || room.isBlank()) {
            throw new IllegalArgumentException("科目名と教室を指定してください");
        }
        this.subject = subject;
        this.room = room;
    }

    public String getSubject() {
        return subject;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return subject + "(" + room + ")";
    }
}
