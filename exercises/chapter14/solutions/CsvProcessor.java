import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * CSVの読み込みと集計（課題3の解答例）
 */
public class CsvProcessor {
    record Employee(int id, String name, String department, int age, int salary) {
        String toCsv() {
            return id + "," + name + "," + department + "," + age + "," + salary;
        }
    }

    public static void main(String[] args) {
        Path dir = Path.of("csv-work");
        Path input = dir.resolve("employees.csv");
        Path output = dir.resolve("development.csv");
        try {
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }
            // 読み込む見本のデータを用意する（4行目と7行目は壊れている）
            Files.writeString(input, """
                    ID,名前,部署,年齢,給与
                    1001,田中太郎,営業部,30,5000000
                    1002,佐藤花子,開発部,25,4500000
                    1003,不完全データ,総務部
                    1004,鈴木一郎,開発部,35,6000000
                    1005,山田次郎,総務部,28,4200000
                    1006,中村美咲,営業部,三十二,5200000
                    1007,小林健,開発部,41,6500000
                    """, StandardCharsets.UTF_8);
            List<Employee> employees = new ArrayList<>();
            int errors = 0;
            try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
                String header = reader.readLine();
                IO.println("見出し: " + header);
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    try {
                        employees.add(parse(line));
                    } catch (IllegalArgumentException e) {
                        // NumberFormatExceptionもIllegalArgumentExceptionの子
                        IO.println(lineNumber + "行目を読み飛ばします: " + e.getMessage());
                        errors++;
                    }
                }
            }
            IO.println("読み込み: " + employees.size() + "件、読み飛ばし: " + errors + "件");
            // 部署ごとの人数と平均給与（部署名の順）
            Map<String, List<Employee>> byDepartment = new TreeMap<>();
            for (Employee e : employees) {
                byDepartment.computeIfAbsent(e.department(), k -> new ArrayList<>()).add(e);
            }
            IO.println("\n--- 部署別 ---");
            for (Map.Entry<String, List<Employee>> entry : byDepartment.entrySet()) {
                long sum = 0;
                for (Employee e : entry.getValue()) {
                    sum += e.salary();
                }
                IO.println("%s: %d名（平均給与 %,d円）".formatted(entry.getKey(), entry.getValue().size(), sum / entry.getValue().size()));
            }
            // 開発部だけを別のCSVに書き出す
            int written = 0;
            try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
                writer.write("ID,名前,部署,年齢,給与");
                writer.newLine();
                for (Employee e : employees) {
                    if (e.department().equals("開発部")) {
                        writer.write(e.toCsv());
                        writer.newLine();
                        written++;
                    }
                }
            }
            IO.println("\n--- " + output.getFileName() + "（" + written + "件） ---");
            for (String line : Files.readAllLines(output, StandardCharsets.UTF_8)) {
                IO.println(line);
            }
        } catch (IOException e) {
            System.err.println("入出力エラー: " + e.getMessage());
        }
    }

    static Employee parse(String line) {
        String[] f = line.split(",");
        if (f.length != 5) {
            throw new IllegalArgumentException("項目の数が" + f.length + "個です（5個のはず）");
        }
        try {
            return new Employee(Integer.parseInt(f[0]), f[1], f[2], Integer.parseInt(f[3]), Integer.parseInt(f[4]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("数値に変換できません（" + line + "）");
        }
    }
}
