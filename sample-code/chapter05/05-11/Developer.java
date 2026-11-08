public class Developer extends Employee {  // ③
    String primaryLanguage;  // ⑤

    void writeCode() {  // ④
        System.out.println(this.name + "が" + primaryLanguage + "でコーディング中");
    }
}
