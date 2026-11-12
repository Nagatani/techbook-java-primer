import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigurationBuilder {
    // 設定の操作をためておき、buildで順に適用するビルダー
    public static class FunctionalConfigBuilder<T> {
        private final List<Consumer<T>> operations = new ArrayList<>();

        public FunctionalConfigBuilder<T> with(Consumer<T> operation) {
            operations.add(operation);
            return this;
        }

        // 条件が成り立つときだけ、操作を加える
        public FunctionalConfigBuilder<T> whenCondition(boolean condition, Consumer<T> operation) {
            if (condition) {
                operations.add(operation);
            }
            return this;
        }

        public T build(Supplier<T> factory) {
            T config = factory.get();
            for (Consumer<T> operation : operations) {
                operation.accept(config);
            }
            return config;
        }
    }

    public static class DatabaseConfig {
        private String host;
        private int port;
        private String database = "app_db";
        private boolean sslEnabled;

        public void setHost(String host) { this.host = host; }
        public void setPort(int port) { this.port = port; }
        public void setDatabase(String database) { this.database = database; }
        public void setSslEnabled(boolean sslEnabled) { this.sslEnabled = sslEnabled; }

        @Override
        public String toString() {
            return host + ":" + port + "/" + database + " (SSL: " + sslEnabled + ")";
        }
    }

    public DatabaseConfig buildDatabaseConfig(String environment) {
        return new FunctionalConfigBuilder<DatabaseConfig>()
            .with(config -> config.setHost("localhost"))  // 既定の設定
            .with(config -> config.setPort(5432))
            .whenCondition("production".equals(environment),
                config -> {
                    config.setHost("db.example.com");
                    config.setSslEnabled(true);
                })
            .whenCondition("test".equals(environment),
                config -> config.setDatabase("test_db"))
            .build(DatabaseConfig::new);
    }

    public static void main(String[] args) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        IO.println("development: " + builder.buildDatabaseConfig("development"));
        IO.println("test: " + builder.buildDatabaseConfig("test"));
        IO.println("production: " + builder.buildDatabaseConfig("production"));
    }
}
