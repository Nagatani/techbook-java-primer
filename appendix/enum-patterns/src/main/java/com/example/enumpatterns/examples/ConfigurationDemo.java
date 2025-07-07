package com.example.enumpatterns.examples;

import com.example.enumpatterns.configuration.*;
import java.io.*;
import java.util.Properties;

/**
 * Demonstrates type-safe configuration management using enum keys.
 * Shows validation, persistence, and environment variable integration.
 */
public class ConfigurationDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Enum Configuration Management Demo ===\n");
        
        // Demo 1: Basic configuration usage
        System.out.println("Demo 1: Basic Configuration Usage");
        System.out.println("---------------------------------");
        demoBasicConfiguration();
        
        // Demo 2: Configuration validation
        System.out.println("\nDemo 2: Configuration Validation");
        System.out.println("--------------------------------");
        demoConfigurationValidation();
        
        // Demo 3: Configuration persistence
        System.out.println("\nDemo 3: Configuration Persistence");
        System.out.println("---------------------------------");
        demoConfigurationPersistence();
        
        // Demo 4: Environment variables
        System.out.println("\nDemo 4: Environment Variable Integration");
        System.out.println("----------------------------------------");
        demoEnvironmentVariables();
        
        // Demo 5: Configuration listeners
        System.out.println("\nDemo 5: Configuration Change Listeners");
        System.out.println("--------------------------------------");
        demoConfigurationListeners();
    }
    
    private static void demoBasicConfiguration() {
        Configuration config = new Configuration();
        
        // Get default values
        System.out.println("Default Configuration:");
        System.out.println("  DB Host: " + config.getDbHost());
        System.out.println("  DB Port: " + config.getDbPort());
        System.out.println("  Server Port: " + config.getServerPort());
        System.out.println("  Debug Mode: " + config.isDebugMode());
        
        // Set new values with type safety
        config.set(ConfigKey.DB_HOST, "production.db.example.com");
        config.set(ConfigKey.DB_PORT, 3306);
        config.set(ConfigKey.SERVER_PORT, 9090);
        config.set(ConfigKey.FEATURE_DEBUG_MODE, true);
        
        System.out.println("\nUpdated Configuration:");
        System.out.println("  DB Host: " + config.getDbHost());
        System.out.println("  DB Port: " + config.getDbPort());
        System.out.println("  Server Port: " + config.getServerPort());
        System.out.println("  Debug Mode: " + config.isDebugMode());
        
        // Try invalid values
        System.out.println("\nValidation Examples:");
        
        try {
            config.set(ConfigKey.DB_PORT, "invalid");
        } catch (IllegalArgumentException e) {
            System.out.println("  Expected error for invalid port: " + e.getMessage());
        }
        
        try {
            config.set(ConfigKey.SERVER_PORT, "99999");
        } catch (IllegalArgumentException e) {
            System.out.println("  Expected error for port out of range: " + e.getMessage());
        }
        
        try {
            config.set(ConfigKey.APP_VERSION, "1.2.x");
        } catch (IllegalArgumentException e) {
            System.out.println("  Expected error for invalid version format: " + e.getMessage());
        }
    }
    
    private static void demoConfigurationValidation() {
        Configuration config = new Configuration();
        
        // Set some valid values
        config.set(ConfigKey.DB_PORT, 5432);
        config.set(ConfigKey.SERVER_PORT, 8080);
        config.set(ConfigKey.SECURITY_JWT_SECRET, "this-is-a-very-long-secret-key-for-jwt-signing");
        
        // Validate configuration
        Configuration.ValidationResult result = config.validate();
        result.print();
        
        // Create configuration with warnings
        Configuration configWithWarnings = new Configuration();
        configWithWarnings.set(ConfigKey.DB_PORT, 8080);  // Same as server port
        configWithWarnings.set(ConfigKey.SERVER_PORT, 8080);
        configWithWarnings.set(ConfigKey.FEATURE_DEBUG_MODE, true);
        
        System.out.println("\nConfiguration with warnings:");
        Configuration.ValidationResult warningResult = configWithWarnings.validate();
        warningResult.print();
        
        // Show configuration summary with masked sensitive values
        System.out.println("\n" + config.getSummary());
    }
    
    private static void demoConfigurationPersistence() {
        Configuration config = new Configuration();
        
        // Set custom values
        config.set(ConfigKey.DB_HOST, "localhost");
        config.set(ConfigKey.DB_NAME, "myapp_dev");
        config.set(ConfigKey.DB_USERNAME, "developer");
        config.set(ConfigKey.SERVER_PORT, 3000);
        config.set(ConfigKey.FEATURE_NEW_UI, true);
        config.set(ConfigKey.APP_LOG_LEVEL, "DEBUG");
        
        try {
            // Save to file
            String filename = "demo-config.properties";
            config.saveToFile(filename);
            System.out.println("Configuration saved to: " + filename);
            
            // Create new configuration and load from file
            Configuration loadedConfig = new Configuration();
            loadedConfig.loadFromFile(filename);
            
            System.out.println("\nLoaded configuration:");
            System.out.println("  DB Host: " + loadedConfig.getDbHost());
            System.out.println("  DB Name: " + loadedConfig.getDbName());
            System.out.println("  Server Port: " + loadedConfig.getServerPort());
            System.out.println("  New UI Feature: " + loadedConfig.isFeatureNewUiEnabled());
            System.out.println("  Log Level: " + loadedConfig.getAppLogLevel());
            
            // Clean up
            new File(filename).delete();
            
        } catch (IOException e) {
            System.err.println("Error with file operations: " + e.getMessage());
        }
        
        // Load from Properties object
        Properties props = new Properties();
        props.setProperty("db.host", "production.example.com");
        props.setProperty("db.port", "5432");
        props.setProperty("server.port", "443");
        props.setProperty("feature.cache", "false");
        
        Configuration propsConfig = new Configuration();
        propsConfig.loadFromProperties(props);
        
        System.out.println("\nConfiguration from Properties:");
        System.out.println("  DB Host: " + propsConfig.getDbHost());
        System.out.println("  Server Port: " + propsConfig.getServerPort());
        System.out.println("  Cache Enabled: " + propsConfig.isFeatureCacheEnabled());
    }
    
    private static void demoEnvironmentVariables() {
        Configuration config = new Configuration();
        
        // Show environment variable format
        System.out.println("Environment Variable Mapping:");
        System.out.println("  db.host -> DB_HOST");
        System.out.println("  server.port -> SERVER_PORT");
        System.out.println("  feature.debug -> FEATURE_DEBUG");
        
        // Convert configuration to environment variables
        config.set(ConfigKey.DB_HOST, "env.db.example.com");
        config.set(ConfigKey.SERVER_PORT, 8888);
        config.set(ConfigKey.FEATURE_DEBUG_MODE, false);
        
        System.out.println("\nConfiguration as Environment Variables:");
        config.toEnvironmentVariables().forEach((key, value) -> {
            if (key.startsWith("DB_") || key.startsWith("SERVER_") || key.equals("FEATURE_DEBUG")) {
                System.out.println("  export " + key + "=\"" + value + "\"");
            }
        });
        
        // Simulate loading from environment
        // In real usage, this would read from System.getenv()
        System.out.println("\nNote: loadFromEnvironment() would read from actual environment variables");
        System.out.println("Example: export DB_HOST=\"production.db.com\"");
    }
    
    private static void demoConfigurationListeners() {
        Configuration config = new Configuration();
        
        // Add configuration change listener
        Configuration.ConfigurationListener listener = new Configuration.ConfigurationListener() {
            @Override
            public void onConfigurationChanged(ConfigKey key, String oldValue, String newValue) {
                System.out.println(String.format(
                    "Configuration changed: %s = %s -> %s",
                    key.getKey(), oldValue, newValue
                ));
                
                // Perform actions based on specific changes
                if (key == ConfigKey.FEATURE_DEBUG_MODE && "true".equals(newValue)) {
                    System.out.println("  WARNING: Debug mode enabled!");
                }
                
                if (key == ConfigKey.DB_HOST) {
                    System.out.println("  INFO: Database host changed, connection pool will be reset");
                }
            }
        };
        
        config.addListener(listener);
        
        // Make configuration changes
        System.out.println("Making configuration changes:");
        config.set(ConfigKey.DB_HOST, "new-db.example.com");
        config.set(ConfigKey.SERVER_PORT, 9000);
        config.set(ConfigKey.FEATURE_DEBUG_MODE, true);
        config.set(ConfigKey.APP_LOG_LEVEL, "WARN");
        
        // Remove listener
        config.removeListener(listener);
        
        System.out.println("\nListener removed, making more changes (no output expected):");
        config.set(ConfigKey.DB_PORT, 5433);
        config.set(ConfigKey.FEATURE_CACHE, false);
        
        // Show all configuration keys with descriptions
        System.out.println("\nAvailable Configuration Keys:");
        for (ConfigKey key : ConfigKey.values()) {
            System.out.println(String.format(
                "  %-30s (%s): %s",
                key.getKey(),
                key.getType().getSimpleName(),
                key.getDescription()
            ));
        }
    }
}