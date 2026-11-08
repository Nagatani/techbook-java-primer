public class Student {
    private String id;
    private String name;
    
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        // 同一オブジェクトの場合
        if (this == obj) return true;
        
        // nullまたは異なるクラスの場合
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // 型変換して比較
        Student student = (Student) obj;
        return id != null && id.equals(student.id);
    }
    
    @Override
    public int hashCode() {
        // equalsで使用するフィールドと同じフィールドを使用
        return id != null ? id.hashCode() : 0;
    }
}
