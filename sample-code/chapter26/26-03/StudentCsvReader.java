// StudentCsvReader.java - Apache Commons CSVを使うのはこのクラスだけにする
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

public class StudentCsvReader {
    // 1行目を見出しとして読み、データの行には含めない
    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .setTrim(true)
        .get();

    // UTF-8のCSVを読む。先頭にBOMがあれば取り除く
    public List<CsvRow> read(InputStream in) throws IOException {
        InputStream withoutBom = BOMInputStream.builder().setInputStream(in).get();
        try (Reader reader = new InputStreamReader(withoutBom, StandardCharsets.UTF_8);
             CSVParser parser = FORMAT.parse(reader)) {
            List<CsvRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                // 見出しを1行目とした行の番号（改行を含む項目がなければファイルの行番号と同じ）
                rows.add(new CsvRow(record.getRecordNumber() + 1, record.toMap()));
            }
            return rows;
        }
    }
}
