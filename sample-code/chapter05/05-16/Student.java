import java.util.Objects;

public class Student {
    private String id;    // 学籍番号
    private String name;  // 名前
    
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Student[id=" + id + ", name=" + name + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {  // ①
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {  // ②
            return false;
        }
        Student other = (Student) obj;  // ③
        return Objects.equals(id, other.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);  // ④
    }
}
