package chapter23.solutions;

import com.google.gson.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * JSONデータ処理ツール
 * JSON形式のデータを整形、検証、変換する機能を提供
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class JsonTool {
    
    private static final String VERSION = "1.0";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("help") || args.length == 0) {
                formatter.printHelp("json-tool", options);
                return;
            }
            
            if (cmd.hasOption("version")) {
                System.out.println("JSON Tool v" + VERSION);
                return;
            }
            
            // コマンドの実行
            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length == 0) {
                System.err.println("エラー: コマンドが指定されていません");
                formatter.printHelp("json-tool", options);
                System.exit(1);
            }
            
            String command = remainingArgs[0];
            
            switch (command.toLowerCase()) {
                case "format":
                    handleFormat(remainingArgs);
                    break;
                case "validate":
                    handleValidate(remainingArgs);
                    break;
                case "convert":
                    handleConvert(cmd, remainingArgs);
                    break;
                default:
                    System.err.println("エラー: 未知のコマンド: " + command);
                    formatter.printHelp("json-tool", options);
                    System.exit(1);
            }
            
        } catch (ParseException e) {
            System.err.println("エラー: " + e.getMessage());
            formatter.printHelp("json-tool", options);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("エラー: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * コマンドラインオプションを作成
     */
    private static Options createOptions() {
        Options options = new Options();
        
        options.addOption("h", "help", false, "ヘルプを表示");
        options.addOption("v", "version", false, "バージョンを表示");
        options.addOption("f", "from", true, "変換元の形式（json, csv）");
        options.addOption("t", "to", true, "変換先の形式（json, csv）");
        options.addOption("i", "indent", true, "インデントサイズ（デフォルト: 2）");
        options.addOption("c", "compact", false, "コンパクト形式で出力");
        
        return options;
    }
    
    /**
     * formatコマンドを処理
     */
    private static void handleFormat(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("エラー: 引数が不足しています");
            System.err.println("使用方法: json-tool format <input.json> <output.json>");
            System.exit(1);
        }
        
        String inputFile = args[1];
        String outputFile = args[2];
        
        // JSONファイルを読み込む
        String jsonContent = Files.readString(Paths.get(inputFile), StandardCharsets.UTF_8);
        
        // パースして整形
        try {
            JsonElement element = JsonParser.parseString(jsonContent);
            String formatted = GSON.toJson(element);
            
            // ファイルに書き込む
            Files.writeString(Paths.get(outputFile), formatted, StandardCharsets.UTF_8);
            
            System.out.println("JSONファイルを整形しました: " + outputFile);
        } catch (JsonSyntaxException e) {
            System.err.println("エラー: 無効なJSON形式です - " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * validateコマンドを処理
     */
    private static void handleValidate(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("エラー: ファイルが指定されていません");
            System.err.println("使用方法: json-tool validate <file.json>");
            System.exit(1);
        }
        
        String inputFile = args[1];
        
        try {
            // JSONファイルを読み込んでパース
            String jsonContent = Files.readString(Paths.get(inputFile), StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(jsonContent);
            
            // 検証結果を表示
            System.out.println("✓ 有効なJSONファイルです: " + inputFile);
            
            // 詳細情報を表示
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                System.out.println("  タイプ: オブジェクト");
                System.out.println("  プロパティ数: " + obj.size());
                System.out.println("  プロパティ: " + obj.keySet());
            } else if (element.isJsonArray()) {
                JsonArray arr = element.getAsJsonArray();
                System.out.println("  タイプ: 配列");
                System.out.println("  要素数: " + arr.size());
            } else if (element.isJsonPrimitive()) {
                System.out.println("  タイプ: プリミティブ");
                System.out.println("  値: " + element.getAsJsonPrimitive());
            } else if (element.isJsonNull()) {
                System.out.println("  タイプ: null");
            }
            
        } catch (JsonSyntaxException e) {
            System.err.println("✗ 無効なJSONファイルです: " + inputFile);
            System.err.println("  エラー: " + e.getMessage());
            
            // エラー位置を特定できる場合は表示
            String message = e.getMessage();
            if (message.contains("line") && message.contains("column")) {
                System.err.println("  詳細: " + message);
            }
            
            System.exit(1);
        }
    }
    
    /**
     * convertコマンドを処理
     */
    private static void handleConvert(CommandLine cmd, String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("エラー: 引数が不足しています");
            System.err.println("使用方法: json-tool convert --from json --to csv <input> <output>");
            System.exit(1);
        }
        
        String fromFormat = cmd.getOptionValue("from", "json").toLowerCase();
        String toFormat = cmd.getOptionValue("to", "csv").toLowerCase();
        String inputFile = args[1];
        String outputFile = args[2];
        
        if (fromFormat.equals("json") && toFormat.equals("csv")) {
            convertJsonToCsv(inputFile, outputFile);
        } else if (fromFormat.equals("csv") && toFormat.equals("json")) {
            convertCsvToJson(inputFile, outputFile);
        } else {
            System.err.println("エラー: サポートされていない変換です: " + fromFormat + " → " + toFormat);
            System.exit(1);
        }
    }
    
    /**
     * JSONをCSVに変換
     */
    private static void convertJsonToCsv(String inputFile, String outputFile) throws IOException {
        String jsonContent = Files.readString(Paths.get(inputFile), StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(jsonContent);
        
        if (!element.isJsonArray()) {
            System.err.println("エラー: CSVへの変換にはJSON配列が必要です");
            System.exit(1);
        }
        
        JsonArray array = element.getAsJsonArray();
        if (array.size() == 0) {
            System.err.println("エラー: 空の配列です");
            System.exit(1);
        }
        
        // ヘッダーを取得
        Set<String> headers = new LinkedHashSet<>();
        for (JsonElement elem : array) {
            if (elem.isJsonObject()) {
                headers.addAll(elem.getAsJsonObject().keySet());
            }
        }
        
        // CSVに書き込む
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // ヘッダー行
            writer.println(String.join(",", headers));
            
            // データ行
            for (JsonElement elem : array) {
                if (elem.isJsonObject()) {
                    JsonObject obj = elem.getAsJsonObject();
                    List<String> values = new ArrayList<>();
                    
                    for (String header : headers) {
                        JsonElement value = obj.get(header);
                        if (value == null || value.isJsonNull()) {
                            values.add("");
                        } else if (value.isJsonPrimitive()) {
                            String strValue = value.getAsString();
                            // カンマや改行を含む場合はクォート
                            if (strValue.contains(",") || strValue.contains("\n") || strValue.contains("\"")) {
                                strValue = "\"" + strValue.replace("\"", "\"\"") + "\"";
                            }
                            values.add(strValue);
                        } else {
                            // オブジェクトや配列はJSON文字列として保存
                            values.add("\"" + GSON.toJson(value).replace("\"", "\"\"") + "\"");
                        }
                    }
                    
                    writer.println(String.join(",", values));
                }
            }
        }
        
        System.out.println("JSONをCSVに変換しました: " + outputFile);
    }
    
    /**
     * CSVをJSONに変換
     */
    private static void convertCsvToJson(String inputFile, String outputFile) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8);
        
        if (lines.isEmpty()) {
            System.err.println("エラー: 空のCSVファイルです");
            System.exit(1);
        }
        
        // ヘッダーを解析
        String[] headers = parseCsvLine(lines.get(0));
        
        // データを変換
        JsonArray array = new JsonArray();
        
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            
            String[] values = parseCsvLine(line);
            JsonObject obj = new JsonObject();
            
            for (int j = 0; j < Math.min(headers.length, values.length); j++) {
                String value = values[j];
                
                // 数値として解析を試みる
                try {
                    if (value.contains(".")) {
                        obj.addProperty(headers[j], Double.parseDouble(value));
                    } else {
                        obj.addProperty(headers[j], Long.parseLong(value));
                    }
                } catch (NumberFormatException e) {
                    // 文字列として保存
                    obj.addProperty(headers[j], value);
                }
            }
            
            array.add(obj);
        }
        
        // JSONファイルに書き込む
        String json = GSON.toJson(array);
        Files.writeString(Paths.get(outputFile), json, StandardCharsets.UTF_8);
        
        System.out.println("CSVをJSONに変換しました: " + outputFile);
    }
    
    /**
     * CSV行を解析（簡易版）
     */
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // スキップ
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}