package com.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoAppTest {

    @Test
    void addは余分な空白を詰めて追加する() {
        TodoApp app = new TodoApp();
        app.add("  レポートを   書く  ");

        assertEquals(1, app.getTodos().size());
        assertEquals("レポートを 書く", app.getTodos().get(0).getTitle());
        assertFalse(app.getTodos().get(0).isDone());
    }

    @Test
    void 空のタイトルは追加できない() {
        TodoApp app = new TodoApp();
        assertThrows(IllegalArgumentException.class, () -> app.add("   "));
    }

    @Test
    void toJsonはタイトルを含むJSONを返す() {
        TodoApp app = new TodoApp();
        app.add("牛乳を買う");

        String json = app.toJson();
        assertTrue(json.contains("\"title\": \"牛乳を買う\""));
        assertTrue(json.contains("\"done\": false"));
    }

    @Test
    void 設定ファイルをUTF_8で読み込む() throws IOException {
        Properties settings = TodoApp.loadSettings();
        assertEquals("Todoアプリ", settings.getProperty("app.name"));
        assertEquals("1.0.0", settings.getProperty("app.version"));
    }
}
