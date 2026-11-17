// SplitProblemDemo.java - split(",")で読むと崩れるCSVの行
public class SplitProblemDemo {
    public static void main(String[] args) {
        String[] lines = {
            "S001,山田太郎,工学部,2025",
            "S002,\"Smith, John\",理学部,2025",        // 氏名にカンマを含む
            "S003,\"佐藤 \"\"ハナ\"\" 花子\",文学部,2024" // 氏名に引用符を含む
        };
        for (String line : lines) {
            String[] cols = line.split(",");
            IO.println(cols.length + "列: " + String.join(" | ", cols));
        }

        // Excelで「CSV UTF-8」形式を選んで保存すると、ファイルの先頭にBOM（U+FEFF）が付く
        String withBom = "\uFEFFS004,鈴木一郎,工学部,2025";
        String id = withBom.split(",")[0];
        IO.println("S004と等しいか: " + id.equals("S004") + "（長さ"
                + id.length()
            + "、1文字目はU+%04X）".formatted((int) id.charAt(0)));
    }
}
