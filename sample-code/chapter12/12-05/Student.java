// 本章で使うStudentクラス（定義はサンプルコード12-4と同じ）
class Student {
    String name;
    int score;
    int height;
    public Student(String name, int score, int height) { this.name = name; this.score = score; this.height = height; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getHeight() { return height; }
    @Override public String toString() { return name + "(score:" + score + ", height:" + height + ")"; }
}
