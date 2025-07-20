package com.example.enumpatterns.configuration;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Type-safe configuration keys using enums.
 * Demonstrates how to create a robust configuration system with automatic validation and parsing.
 */
public enum ConfigKey {
    // Database configuration
    DB_HOST("db.host", "localhost", String.class, 
        value -> value, 
        value -> !value.trim().isEmpty(),
        "Database host address"),
    
    DB_PORT("db.port", "5432", Integer.class,
        Integer::valueOf,
        value -> {
            try {
                int port = Integer.parseInt(value);
                return port > 0 && port <= 65535;
            } catch (NumberFormatException e) {
                return false;
            }
        },
        "Database port (1-65535)"),
    
    DB_NAME("db.name", "myapp", String.class,
        value -> value,
        value -> value.matches("[a-zA-Z][a-zA-Z0-9_]*"),
        "Database name (alphanumeric and underscore)"),
    
    DB_USERNAME("db.username", "user", String.class,
        value -> value,
        value -> !value.trim().isEmpty(),
        "Database username"),
    
    DB_PASSWORD("db.password", "", String.class,
        value -> value,
        value -> true,  // Allow empty passwords for development
        "Database password"),
    
    DB_CONNECTION_POOL_SIZE("db.pool.size", "10", Integer.class,
        Integer::valueOf,
        value -> {
            try {
                int size = Integer.parseInt(value);
                return size >= 1 && size <= 100;
            } catch (NumberFormatException e) {
                return false;
            }
        },
        "Database connection pool size (1-100)"),
    
    // Server configuration
    SERVER_HOST("server.host", "0.0.0.0", String.class,
        value -> value,
        value -> isValidIpAddress(value) || isValidHostname(value),
        "Server host address"),
    
    SERVER_PORT("server.port", "8080", Integer.class,
        Integer::valueOf,
        value -> {
            try {
                int port = Integer.parseInt(value);
                return port > 0 && port <= 65535;
            } catch (NumberFormatException e) {
                return false;
            }
        },
        "Server port (1-65535)"),
    
    SERVER_MAX_THREADS("server.threads.max", "100", Integer.class,
        Integer::valueOf,
        value -> {
            try {
                int threads = Integer.parseInt(value);
                return threads >= 1 && threads <= 1000;
            } catch (NumberFormatException e) {
                return false;
            }
        },
        "Maximum server threads (1-1000)"),
    
    SERVER_TIMEOUT_MS("server.timeout.ms", "30000", Long.class,
        Long::valueOf,
        value -> {
            try {
                long timeout = Long.parseLong(value);
                return timeout >= 0 && timeout <= 300000;  // Max 5 minutes
            } catch (NumberFormatException e) {
                return false;
            }
        },
        "Server timeout in milliseconds (0-300000)"),
    
    // Feature flags
    FEATURE_NEW_UI("feature.ui.new", "false", Boolean.class,
        Boolean::valueOf,
        value -> value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"),
        "Enable new UI features"),
    
    FEATURE_ANALYTICS("feature.analytics", "true", Boolean.class,
        Boolean::valueOf,
        value -> value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"),
        "Enable analytics collection"),
    
    FEATURE_CACHE("feature.cache", "true", Boolean.class,
        Boolean::valueOf,
        value -> value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"),
        "Enable caching"),
    
    FEATURE_DEBUG_MODE("feature.debug", "false", Boolean.class,
        Boolean::valueOf,
        value -> value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"),
        "Enable debug mode"),
    
    // Application settings
    APP_NAME("app.name", "MyApplication", String.class,
        value -> value,
        value -> !value.trim().isEmpty() && value.length() <= 100,
        "Application name (max 100 characters)"),
    
    APP_VERSION("app.version", "1.0.0", String.class,
        value -> value,
        value -> value.matches("\\d+\\.\\d+\\.\\d+"),
        "Application version (semantic versioning)"),
    
    APP_LOG_LEVEL("app.log.level", "INFO", String.class,
        String::toUpperCase,
        value -> value.matches("TRACE|DEBUG|INFO|WARN|ERROR"),
        "Logging level (TRACE, DEBUG, INFO, WARN, ERROR)"),
    
    APP_LOG_FILE("app.log.file", "app.log", String.class,
        value -> value,
        value -> value.matches("[a-zA-Z0-9._/-]+"),
        "Log file path"),
    
    // Security settings
    SECURITY_JWT_SECRET("security.jwt.secret", "change-me-in-production", String.class,
        value -> value,
        value -> value.length() >= 32,
        "JWT secret key (minimum 32 characters)"),
    
    SECURITY_SESSION_TIMEOUT("security.session.timeout", "3600", Integer.class,
        Integer::valueOf,
        value -> {
            try {
                int timeout = Integer.parseInt(value);
                return timeout >= 60 && timeout <= 86400;  // 1 minute to 24 hours
            } catch (NumberFormatException e) {
                return false;
            }
        },
        "Session timeout in seconds (60-86400)");
    
    private final String key;
    private final String defaultValue;
    private final Class<?> type;
    private final Function<String, ?> parser;
    private final Function<String, Boolean> validator;
    private final String description;
    
    ConfigKey(String key, String defaultValue, Class<?> type,
              Function<String, ?> parser, Function<String, Boolean> validator,
              String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.parser = parser;
        this.validator = validator;
        this.description = description;
    }
    
    public String getKey() { return key; }
    public String getDefaultValue() { return defaultValue; }
    public Class<?> getType() { return type; }
    public String getDescription() { return description; }
    
    /**
     * Parse and validate a string value
     */
    @SuppressWarnings("unchecked")
    public <T> T parseValue(String value) {
        if (value == null) {
            value = defaultValue;
        }
        
        if (!validator.apply(value)) {
            throw new IllegalArgumentException(
                String.format("Invalid value '%s' for %s. %s", value, key, description)
            );
        }
        
        try {
            return (T) parser.apply(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                String.format("Cannot parse value '%s' for %s as %s", value, key, type.getSimpleName()),
                e
            );
        }
    }
    
    /**
     * Validate a string value without parsing
     */
    public boolean isValid(String value) {
        return validator.apply(value);
    }
    
    /**
     * Find config key by property key
     */
    public static ConfigKey fromKey(String propertyKey) {
        for (ConfigKey configKey : values()) {
            if (configKey.key.equals(propertyKey)) {
                return configKey;
            }
        }
        throw new IllegalArgumentException("Unknown configuration key: " + propertyKey);
    }
    
    // Validation helpers
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
        "(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );
    
    private static boolean isValidIpAddress(String value) {
        return IP_PATTERN.matcher(value).matches();
    }
    
    private static boolean isValidHostname(String value) {
        return HOSTNAME_PATTERN.matcher(value).matches() || 
               value.equals("localhost") || 
               value.equals("0.0.0.0");
    }
}