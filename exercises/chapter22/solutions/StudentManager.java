package chapter22.solutions;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Gsonライブラリを使用して学生情報を管理するクラス。
 * <p>
 * JSON形式でのファイルへの保存・読み込み機能を提供します。
 * </p>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 */
public class StudentManager {
    private static final Logger logger = Logger.getLogger(StudentManager.class.getName());
    
    private final Map<String, Student> students;
    private final Gson gson;
    
    /**
     * StudentManagerのインスタンスを作成します。
     */
    public StudentManager() {
        this.students = new HashMap<>();
        
        // GsonBuilderでカスタマイズされたGsonインスタンスを作成
        this.gson = new GsonBuilder()
                .setPrettyPrinting()  // 整形されたJSON出力
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)  // フィールド名の変換
                .excludeFieldsWithoutExposeAnnotation()  // @Exposeアノテーションがないフィールドを除外しない
                .serializeNulls()  // null値も出力
                .setDateFormat("yyyy-MM-dd HH:mm:ss")  // 日付フォーマット
                .disableHtmlEscaping()  // HTML文字をエスケープしない
                .create();
    }
    
    /**
     * 学生を追加します。
     * 
     * @param student 追加する学生
     * @throws IllegalArgumentException 同じIDの学生が既に存在する場合
     */
    public void addStudent(Student student) {
        Objects.requireNonNull(student, "学生はnullにできません");
        
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("学生ID " + student.getId() + " は既に存在します");
        }
        
        students.put(student.getId(), student);
        logger.info("学生を追加しました: " + student.getId());
    }
    
    /**
     * 学生情報を更新します。
     * 
     * @param student 更新する学生情報
     * @throws IllegalArgumentException 学生が存在しない場合
     */
    public void updateStudent(Student student) {
        Objects.requireNonNull(student, "学生はnullにできません");
        
        if (!students.containsKey(student.getId())) {
            throw new IllegalArgumentException("学生ID " + student.getId() + " は存在しません");
        }
        
        students.put(student.getId(), student);
        logger.info("学生情報を更新しました: " + student.getId());
    }
    
    /**
     * 学生IDで学生を検索します。
     * 
     * @param studentId 学生ID
     * @return 見つかった学生、存在しない場合はnull
     */
    public Student findStudent(String studentId) {
        return students.get(studentId);
    }
    
    /**
     * すべての学生のリストを取得します。
     * 
     * @return 学生のリスト
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    /**
     * 単一の学生情報をJSONファイルに保存します。
     * 
     * @param student 保存する学生
     * @param filePath 保存先のファイルパス
     * @throws IOException ファイル操作でエラーが発生した場合
     */
    public void saveStudentToJson(Student student, String filePath) throws IOException {
        Objects.requireNonNull(student, "学生はnullにできません");
        Objects.requireNonNull(filePath, "ファイルパスはnullにできません");
        
        Path path = Paths.get(filePath);
        
        // 親ディレクトリが存在しない場合は作成
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        
        // JSONファイルに書き込み
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(student, writer);
        }
        
        logger.info("学生情報をJSONファイルに保存しました: " + filePath);
    }
    
    /**
     * 学生リストをJSONファイルに保存します。
     * 
     * @param students 保存する学生のリスト
     * @param filePath 保存先のファイルパス
     * @throws IOException ファイル操作でエラーが発生した場合
     */
    public void saveStudentsToJson(List<Student> students, String filePath) throws IOException {
        Objects.requireNonNull(students, "学生リストはnullにできません");
        Objects.requireNonNull(filePath, "ファイルパスはnullにできません");
        
        Path path = Paths.get(filePath);
        
        // 親ディレクトリが存在しない場合は作成
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        
        // JSONファイルに書き込み
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(students, writer);
        }
        
        logger.info("学生リストをJSONファイルに保存しました: " + filePath);
    }
    
    /**
     * 現在管理しているすべての学生をJSONファイルに保存します。
     * 
     * @param filePath 保存先のファイルパス
     * @throws IOException ファイル操作でエラーが発生した場合
     */
    public void saveAllStudentsToJson(String filePath) throws IOException {
        saveStudentsToJson(getAllStudents(), filePath);
    }
    
    /**
     * JSONファイルから単一の学生情報を読み込みます。
     * 
     * @param filePath 読み込むファイルパス
     * @return 読み込んだ学生情報
     * @throws IOException ファイル操作でエラーが発生した場合
     */
    public Student loadStudentFromJson(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "ファイルパスはnullにできません");
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }
        
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Student student = gson.fromJson(reader, Student.class);
            if (student != null) {
                logger.info("学生情報をJSONファイルから読み込みました: " + student.getId());
            }
            return student;
        }
    }
    
    /**
     * JSONファイルから学生リストを読み込みます。
     * 
     * @param filePath 読み込むファイルパス
     * @return 読み込んだ学生のリスト
     * @throws IOException ファイル操作でエラーが発生した場合
     */
    public List<Student> loadStudentsFromJson(String filePath) throws IOException {
        Objects.requireNonNull(filePath, "ファイルパスはnullにできません");
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }
        
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<Student>>(){}.getType();
            List<Student> students = gson.fromJson(reader, listType);
            
            if (students != null) {
                logger.info(students.size() + " 人の学生情報を読み込みました");
                // 読み込んだ学生を管理対象に追加
                for (Student student : students) {
                    this.students.put(student.getId(), student);
                }
            }
            
            return students != null ? students : new ArrayList<>();
        }
    }
    
    /**
     * 学生情報をJSON形式の文字列として出力します。
     * 
     * @param student 出力する学生
     * @return JSON形式の文字列
     */
    public String toJsonString(Student student) {
        Objects.requireNonNull(student, "学生はnullにできません");
        return gson.toJson(student);
    }
    
    /**
     * 学生リストをJSON形式の文字列として出力します。
     * 
     * @param students 出力する学生リスト
     * @return JSON形式の文字列
     */
    public String toJsonString(List<Student> students) {
        Objects.requireNonNull(students, "学生リストはnullにできません");
        return gson.toJson(students);
    }
    
    /**
     * JSON文字列から学生情報を作成します。
     * 
     * @param json JSON文字列
     * @return 作成された学生情報
     */
    public Student fromJsonString(String json) {
        Objects.requireNonNull(json, "JSON文字列はnullにできません");
        return gson.fromJson(json, Student.class);
    }
    
    /**
     * JSON文字列から学生リストを作成します。
     * 
     * @param json JSON文字列
     * @return 作成された学生リスト
     */
    public List<Student> fromJsonStringToList(String json) {
        Objects.requireNonNull(json, "JSON文字列はnullにできません");
        Type listType = new TypeToken<List<Student>>(){}.getType();
        return gson.fromJson(json, listType);
    }
    
    /**
     * デモ用のメインメソッド
     */
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        
        // サンプルデータの作成
        Student student1 = new Student("S001", "田中太郎", 20);
        student1.addCourse(new Course("CS101", "プログラミング基礎", 4, 3.5));
        student1.addCourse(new Course("MA201", "線形代数", 3, 4.0));
        
        Student student2 = new Student("S002", "山田花子", 21);
        student2.addCourse(new Course("CS101", "プログラミング基礎", 4, 4.0));
        student2.addCourse(new Course("EN101", "英語I", 2, 3.0));
        
        // 学生を追加
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        try {
            // 単一の学生をJSONファイルに保存
            manager.saveStudentToJson(student1, "output/student_S001.json");
            
            // すべての学生をJSONファイルに保存
            manager.saveAllStudentsToJson("output/all_students.json");
            
            // JSON形式で出力
            System.out.println("=== 単一の学生（JSON形式） ===");
            System.out.println(manager.toJsonString(student1));
            
            System.out.println("\n=== すべての学生（JSON形式） ===");
            System.out.println(manager.toJsonString(manager.getAllStudents()));
            
            // JSONファイルから読み込み
            System.out.println("\n=== JSONファイルから読み込み ===");
            List<Student> loadedStudents = manager.loadStudentsFromJson("output/all_students.json");
            for (Student s : loadedStudents) {
                System.out.println(s);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}