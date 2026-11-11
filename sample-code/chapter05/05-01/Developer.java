public class Developer extends Employee {  // ③
    String primaryLanguage;  // ⑤

    void writeCode() {  // ④
        IO.println(this.name + "が" + primaryLanguage + "でコーディング中");
    }
}
