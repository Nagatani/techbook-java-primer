package com.example.enumpatterns.configuration;

import java.io.*;
import java.util.*;

/**
 * Type-safe configuration management using enum keys.
 * Provides a robust configuration system with validation, type safety, and convenience methods.
 */
public class Configuration {
    private final EnumMap<ConfigKey, String> values;
    private final List<ConfigurationListener> listeners;
    
    public Configuration() {
        this.values = new EnumMap<>(ConfigKey.class);
        this.listeners = new ArrayList<>();
        
        // Initialize with default values
        for (ConfigKey key : ConfigKey.values()) {
            values.put(key, key.getDefaultValue());
        }
    }
    
    /**
     * Get a configuration value with type safety
     */
    public <T> T get(ConfigKey key) {
        String value = values.get(key);
        return key.parseValue(value);
    }
    
    /**
     * Set a configuration value with validation
     */
    public void set(ConfigKey key, String value) {
        String oldValue = values.get(key);
        
        // Validate the new value
        if (!key.isValid(value)) {
            throw new IllegalArgumentException(
                String.format("Invalid value '%s' for %s", value, key.getKey())
            );
        }
        
        // Parse to ensure it's valid
        key.parseValue(value);
        
        // Update the value
        values.put(key, value);
        
        // Notify listeners
        notifyListeners(key, oldValue, value);
    }
    
    /**
     * Set a configuration value with type safety
     */
    public <T> void set(ConfigKey key, T value) {
        set(key, value.toString());
    }
    
    /**
     * Load configuration from properties file
     */
    public void loadFromFile(String filename) throws IOException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(filename)) {
            props.load(is);
        }
        
        loadFromProperties(props);
    }
    
    /**
     * Load configuration from properties
     */
    public void loadFromProperties(Properties properties) {
        for (String propertyKey : properties.stringPropertyNames()) {
            try {
                ConfigKey configKey = ConfigKey.fromKey(propertyKey);
                String value = properties.getProperty(propertyKey);
                set(configKey, value);
            } catch (IllegalArgumentException e) {
                // Skip unknown keys or invalid values
                System.err.println("Warning: " + e.getMessage());
            }
        }
    }
    
    /**
     * Save configuration to properties file
     */
    public void saveToFile(String filename) throws IOException {
        Properties props = toProperties();
        try (OutputStream os = new FileOutputStream(filename)) {
            props.store(os, "Configuration saved at " + new Date());
        }
    }
    
    /**
     * Convert to properties
     */
    public Properties toProperties() {
        Properties props = new Properties();
        for (Map.Entry<ConfigKey, String> entry : values.entrySet()) {
            props.setProperty(entry.getKey().getKey(), entry.getValue());
        }
        return props;
    }
    
    /**
     * Get configuration as environment variables format
     */
    public Map<String, String> toEnvironmentVariables() {
        Map<String, String> env = new HashMap<>();
        for (Map.Entry<ConfigKey, String> entry : values.entrySet()) {
            String envKey = entry.getKey().getKey()
                .toUpperCase()
                .replace('.', '_');
            env.put(envKey, entry.getValue());
        }
        return env;
    }
    
    /**
     * Load from environment variables
     */
    public void loadFromEnvironment() {
        Map<String, String> env = System.getenv();
        
        for (ConfigKey key : ConfigKey.values()) {
            String envKey = key.getKey()
                .toUpperCase()
                .replace('.', '_');
            
            String value = env.get(envKey);
            if (value != null) {
                try {
                    set(key, value);
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Invalid environment value for " + envKey + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Validate all configuration values
     */
    public ValidationResult validate() {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        for (Map.Entry<ConfigKey, String> entry : values.entrySet()) {
            ConfigKey key = entry.getKey();
            String value = entry.getValue();
            
            try {
                key.parseValue(value);
            } catch (Exception e) {
                errors.add(String.format("%s: %s", key.getKey(), e.getMessage()));
            }
        }
        
        // Add custom validation rules
        validateCustomRules(errors, warnings);
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    /**
     * Custom validation rules
     */
    private void validateCustomRules(List<String> errors, List<String> warnings) {
        // Example: DB port and server port should be different
        int dbPort = get(ConfigKey.DB_PORT);
        int serverPort = get(ConfigKey.SERVER_PORT);
        if (dbPort == serverPort) {
            warnings.add("Database port and server port are the same. This might cause conflicts.");
        }
        
        // Example: Production checks
        String jwtSecret = get(ConfigKey.SECURITY_JWT_SECRET);
        if (jwtSecret.equals(ConfigKey.SECURITY_JWT_SECRET.getDefaultValue())) {
            warnings.add("Using default JWT secret. Change this in production!");
        }
        
        // Example: Debug mode warning
        boolean debugMode = get(ConfigKey.FEATURE_DEBUG_MODE);
        if (debugMode) {
            warnings.add("Debug mode is enabled. Disable in production!");
        }
    }
    
    /**
     * Register a configuration change listener
     */
    public void addListener(ConfigurationListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a configuration change listener
     */
    public void removeListener(ConfigurationListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify listeners of configuration changes
     */
    private void notifyListeners(ConfigKey key, String oldValue, String newValue) {
        for (ConfigurationListener listener : listeners) {
            listener.onConfigurationChanged(key, oldValue, newValue);
        }
    }
    
    /**
     * Get configuration summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration Summary\n");
        sb.append("====================\n\n");
        
        // Group by category
        Map<String, List<ConfigKey>> categories = new LinkedHashMap<>();
        categories.put("Database", new ArrayList<>());
        categories.put("Server", new ArrayList<>());
        categories.put("Features", new ArrayList<>());
        categories.put("Application", new ArrayList<>());
        categories.put("Security", new ArrayList<>());
        
        for (ConfigKey key : ConfigKey.values()) {
            String keyName = key.getKey();
            if (keyName.startsWith("db.")) {
                categories.get("Database").add(key);
            } else if (keyName.startsWith("server.")) {
                categories.get("Server").add(key);
            } else if (keyName.startsWith("feature.")) {
                categories.get("Features").add(key);
            } else if (keyName.startsWith("app.")) {
                categories.get("Application").add(key);
            } else if (keyName.startsWith("security.")) {
                categories.get("Security").add(key);
            }
        }
        
        for (Map.Entry<String, List<ConfigKey>> category : categories.entrySet()) {
            sb.append(category.getKey()).append(" Configuration:\n");
            for (ConfigKey key : category.getValue()) {
                String value = values.get(key);
                // Mask sensitive values
                if (key == ConfigKey.DB_PASSWORD || key == ConfigKey.SECURITY_JWT_SECRET) {
                    value = "****" + value.substring(Math.max(0, value.length() - 4));
                }
                sb.append("  ").append(key.getKey()).append(" = ").append(value).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    // Convenience getters for common configuration values
    public String getDbHost() { return get(ConfigKey.DB_HOST); }
    public int getDbPort() { return get(ConfigKey.DB_PORT); }
    public String getDbName() { return get(ConfigKey.DB_NAME); }
    public String getDbUsername() { return get(ConfigKey.DB_USERNAME); }
    public String getDbPassword() { return get(ConfigKey.DB_PASSWORD); }
    public int getDbConnectionPoolSize() { return get(ConfigKey.DB_CONNECTION_POOL_SIZE); }
    
    public String getServerHost() { return get(ConfigKey.SERVER_HOST); }
    public int getServerPort() { return get(ConfigKey.SERVER_PORT); }
    public int getServerMaxThreads() { return get(ConfigKey.SERVER_MAX_THREADS); }
    public long getServerTimeoutMs() { return get(ConfigKey.SERVER_TIMEOUT_MS); }
    
    public boolean isFeatureNewUiEnabled() { return get(ConfigKey.FEATURE_NEW_UI); }
    public boolean isFeatureAnalyticsEnabled() { return get(ConfigKey.FEATURE_ANALYTICS); }
    public boolean isFeatureCacheEnabled() { return get(ConfigKey.FEATURE_CACHE); }
    public boolean isDebugMode() { return get(ConfigKey.FEATURE_DEBUG_MODE); }
    
    public String getAppName() { return get(ConfigKey.APP_NAME); }
    public String getAppVersion() { return get(ConfigKey.APP_VERSION); }
    public String getAppLogLevel() { return get(ConfigKey.APP_LOG_LEVEL); }
    public String getAppLogFile() { return get(ConfigKey.APP_LOG_FILE); }
    
    public String getSecurityJwtSecret() { return get(ConfigKey.SECURITY_JWT_SECRET); }
    public int getSecuritySessionTimeout() { return get(ConfigKey.SECURITY_SESSION_TIMEOUT); }
    
    /**
     * Configuration change listener interface
     */
    public interface ConfigurationListener {
        void onConfigurationChanged(ConfigKey key, String oldValue, String newValue);
    }
    
    /**
     * Validation result
     */
    public static record ValidationResult(
        boolean isValid,
        List<String> errors,
        List<String> warnings
    ) {
        public void print() {
            if (isValid) {
                System.out.println("Configuration is valid.");
            } else {
                System.out.println("Configuration has errors:");
                errors.forEach(error -> System.err.println("  ERROR: " + error));
            }
            
            if (!warnings.isEmpty()) {
                System.out.println("\nWarnings:");
                warnings.forEach(warning -> System.out.println("  WARNING: " + warning));
            }
        }
    }
}