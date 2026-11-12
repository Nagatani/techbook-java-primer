import java.io.IOException;
import java.util.List;

/**
 * 手元のTodoApiServerを起動し、RestApiClientで一通りのリクエストを送る
 */
public class RestApiDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        TodoApiServer server = new TodoApiServer(0); // 空いているポートを使う
        server.start();
        String baseUrl = "http://localhost:" + server.getPort();
        try {
            RestApiClient api = new RestApiClient(baseUrl, TodoApiServer.TOKEN);
            IO.println("GET /todos");
            IO.println("  " + api.get("/todos"));
            IO.println("POST /todos");
            IO.println("  " + api.post("/todos", "{\"title\":\"部屋を片付ける\",\"done\":false}"));
            IO.println("PUT /todos/1");
            IO.println("  " + api.put("/todos/1", "{\"title\":\"牛乳を買う\",\"done\":true}"));
            IO.println("DELETE /todos/2");
            IO.println("  本文: \"" + api.delete("/todos/2") + "\"");
            IO.println("GET /todos");
            IO.println("  " + api.get("/todos"));

            IO.println("\n--- 200番台以外は例外になる ---");
            try {
                api.get("/todos/2");
            } catch (ApiException e) {
                IO.println("GET /todos/2: " + e.getMessage());
            }
            try {
                new RestApiClient(baseUrl, null).get("/todos");
            } catch (ApiException e) {
                IO.println("トークンなし: " + e.getMessage());
            }

            IO.println("\n--- sendAsyncで同時に送る ---");
            List<String> results = api.getAll(List.of("/todos/1", "/todos/3", "/todos/9"));
            for (String r : results) {
                IO.println("  " + r);
            }
        } catch (ApiException e) {
            IO.println("想定外のエラー: " + e.getMessage());
        } finally {
            server.stop();
        }
    }
}
