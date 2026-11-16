// GradeCsvReader.java - 成績のCSV（学生ID,科目,単位数,評価点）を読む
// Apache Commons CSVを使うのはこのクラスだけにし、結果は自分で定義したRecordで返す
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

public class GradeCsvReader {
    private static final String[] HEADERS = {"学生ID", "科目", "単位数", "評価点"};

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .setTrim(true)
        .setIgnoreEmptyLines(true)
        .get();

    // ファイル自体を読めないときはIOExceptionを投げる。1行ごとの誤りは結果に集める
    public GradeCsvResult read(InputStream in) throws IOException {
        InputStream withoutBom = BOMInputStream.builder().setInputStream(in).get();
        try (Reader reader = new InputStreamReader(withoutBom, StandardCharsets.UTF_8);
             CSVParser parser = FORMAT.parse(reader)) {
            for (String header : HEADERS) {
                if (!parser.getHeaderMap().containsKey(header)) {
                    throw new IOException("見出しに「" + header + "」がありません");
                }
            }
            List<GradeRow> rows = new ArrayList<>();
            List<GradeCsvError> errors = new ArrayList<>();
            for (CSVRecord record : parser) {
                long lineNumber = record.getRecordNumber() + 1;
                try {
                    rows.add(toRow(record));
                } catch (IllegalArgumentException e) {
                    errors.add(new GradeCsvError(lineNumber, e.getMessage()));
                }
            }
            return new GradeCsvResult(rows, errors);
        }
    }

    private GradeRow toRow(CSVRecord record) {
        if (!record.isConsistent()) {
            throw new IllegalArgumentException("列の数が見出しと合いません（" + record.size() + "列）");
        }
        String studentId = record.get("学生ID");
        if (studentId.isBlank()) {
            throw new IllegalArgumentException("学生IDが空です");
        }
        int credits;
        double point;
        try {
            credits = Integer.parseInt(record.get("単位数"));
            point = Double.parseDouble(record.get("評価点"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("単位数または評価点が数値ではありません");
        }
        // 範囲の検査はGradeのコンストラクターに任せる
        return new GradeRow(studentId, new Grade(record.get("科目"), credits, point));
    }
}
