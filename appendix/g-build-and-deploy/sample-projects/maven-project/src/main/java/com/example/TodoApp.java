package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 外部ライブラリ（Gson、Commons Lang、Logback）を使う小さなTodoアプリケーション。
 * ビルドと配布（通常のJAR、Fat JAR、jpackage）を試すためのサンプルです。
 */
public class TodoApp {
    private static final Logger logger = LoggerFactory.getLogger(TodoApp.class);

    /** 1件のTodo。GsonがフィールドをJSONに変換する */
    public static class Todo {
        private final String title;
        private final boolean done;

        public Todo(String title, boolean done) {
            this.title = title;
            this.done = done;
        }

        public String getTitle() {
            return title;
        }

        public boolean isDone() {
            return done;
        }
    }

    private final List<Todo> todos = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** タイトルの前後と途中の余分な空白を詰めてTodoを追加する */
    public void add(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("タイトルが空です");
        }
        String normalized = StringUtils.normalizeSpace(title);
        todos.add(new Todo(normalized, false));
        logger.info("追加しました: {}", normalized);
    }

    public List<Todo> getTodos() {
        return List.copyOf(todos);
    }

    public String toJson() {
        return gson.toJson(todos);
    }

    /** クラスパス上のapplication.propertiesをUTF-8で読み込む */
    public static Properties loadSettings() throws IOException {
        try (InputStream in = TodoApp.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                throw new IOException("application.propertiesが見つかりません");
            }
            Properties settings = new Properties();
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                settings.load(reader);
            }
            return settings;
        }
    }

    public static void main(String[] args) throws IOException {
        Properties settings = loadSettings();
        IO.println(settings.getProperty("app.name") + " " + settings.getProperty("app.version"));

        TodoApp app = new TodoApp();
        app.add("牛乳を買う");
        app.add("  レポートを   書く  ");
        IO.println(app.toJson());
    }
}
