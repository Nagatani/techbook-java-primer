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
        IO.println("=== Enum Configuration Management Demo ===\n");
        
        // Demo 1: Basic configuration usage
        IO.println("Demo 1: Basic Configuration Usage");
        IO.println("---------------------------------");
        demoBasicConfiguration();
        
        // Demo 2: Configuration validation
        IO.println("\nDemo 2: Configuration Validation");
        IO.println("--------------------------------");
        demoConfigurationValidation();
        
        // Demo 3: Configuration persistence
        IO.println("\nDemo 3: Configuration Persistence");
        IO.println("---------------------------------");
        demoConfigurationPersistence();
        
        // Demo 4: Environment variables
        IO.println("\nDemo 4: Environment Variable Integration");
        IO.println("----------------------------------------");
        demoEnvironmentVariables();
        
        // Demo 5: Configuration listeners
        IO.println("\nDemo 5: Configuration Change Listeners");
        IO.println("--------------------------------------");
        demoConfigurationListeners();
    }
    
    private static void demoBasicConfiguration() {
        Configuration config = new Configuration();
        
        // Get default values
        IO.println("Default Configuration:");
        IO.println("  DB Host: " + config.getDbHost());
        IO.println("  DB Port: " + config.getDbPort());
        IO.println("  Server Port: " + config.getServerPort());
        IO.println("  Debug Mode: " + config.isDebugMode());
        
        // Set new values with type safety
        config.set(ConfigKey.DB_HOST, "production.db.example.com");
        config.set(ConfigKey.DB_PORT, 3306);
        config.set(ConfigKey.SERVER_PORT, 9090);
        config.set(ConfigKey.FEATURE_DEBUG_MODE, true);
        
        IO.println("\nUpdated Configuration:");
        IO.println("  DB Host: " + config.getDbHost());
        IO.println("  DB Port: " + config.getDbPort());
        IO.println("  Server Port: " + config.getServerPort());
        IO.println("  Debug Mode: " + config.isDebugMode());
        
        // Try invalid values
        IO.println("\nValidation Examples:");
        
        try {
            config.set(ConfigKey.DB_PORT, "invalid");
        } catch (IllegalArgumentException e) {
            IO.println("  Expected error for invalid port: " + e.getMessage());
        }
        
        try {
            config.set(ConfigKey.SERVER_PORT, "99999");
        } catch (IllegalArgumentException e) {
            IO.println("  Expected error for port out of range: " + e.getMessage());
        }
        
        try {
            config.set(ConfigKey.APP_VERSION, "1.2.x");
        } catch (IllegalArgumentException e) {
            IO.println("  Expected error for invalid version format: " + e.getMessage());
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
        
        IO.println("\nConfiguration with warnings:");
        Configuration.ValidationResult warningResult = configWithWarnings.validate();
        warningResult.print();
        
        // Show configuration summary with masked sensitive values
        IO.println("\n" + config.getSummary());
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
            IO.println("Configuration saved to: " + filename);
            
            // Create new configuration and load from file
            Configuration loadedConfig = new Configuration();
            loadedConfig.loadFromFile(filename);
            
            IO.println("\nLoaded configuration:");
            IO.println("  DB Host: " + loadedConfig.getDbHost());
            IO.println("  DB Name: " + loadedConfig.getDbName());
            IO.println("  Server Port: " + loadedConfig.getServerPort());
            IO.println("  New UI Feature: " + loadedConfig.isFeatureNewUiEnabled());
            IO.println("  Log Level: " + loadedConfig.getAppLogLevel());
            
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
        
        IO.println("\nConfiguration from Properties:");
        IO.println("  DB Host: " + propsConfig.getDbHost());
        IO.println("  Server Port: " + propsConfig.getServerPort());
        IO.println("  Cache Enabled: " + propsConfig.isFeatureCacheEnabled());
    }
    
    private static void demoEnvironmentVariables() {
        Configuration config = new Configuration();
        
        // Show environment variable format
        IO.println("Environment Variable Mapping:");
        IO.println("  db.host -> DB_HOST");
        IO.println("  server.port -> SERVER_PORT");
        IO.println("  feature.debug -> FEATURE_DEBUG");
        
        // Convert configuration to environment variables
        config.set(ConfigKey.DB_HOST, "env.db.example.com");
        config.set(ConfigKey.SERVER_PORT, 8888);
        config.set(ConfigKey.FEATURE_DEBUG_MODE, false);
        
        IO.println("\nConfiguration as Environment Variables:");
        config.toEnvironmentVariables().forEach((key, value) -> {
            if (key.startsWith("DB_") || key.startsWith("SERVER_") || key.equals("FEATURE_DEBUG")) {
                IO.println("  export " + key + "=\"" + value + "\"");
            }
        });
        
        // Simulate loading from environment
        // In real usage, this would read from System.getenv()
        IO.println("\nNote: loadFromEnvironment() would read from actual environment variables");
        IO.println("Example: export DB_HOST=\"production.db.com\"");
    }
    
    private static void demoConfigurationListeners() {
        Configuration config = new Configuration();
        
        // Add configuration change listener
        Configuration.ConfigurationListener listener = new Configuration.ConfigurationListener() {
            @Override
            public void onConfigurationChanged(ConfigKey key, String oldValue, String newValue) {
                IO.println(String.format(
                    "Configuration changed: %s = %s -> %s",
                    key.getKey(), oldValue, newValue
                ));
                
                // Perform actions based on specific changes
                if (key == ConfigKey.FEATURE_DEBUG_MODE && "true".equals(newValue)) {
                    IO.println("  WARNING: Debug mode enabled!");
                }
                
                if (key == ConfigKey.DB_HOST) {
                    IO.println("  INFO: Database host changed, connection pool will be reset");
                }
            }
        };
        
        config.addListener(listener);
        
        // Make configuration changes
        IO.println("Making configuration changes:");
        config.set(ConfigKey.DB_HOST, "new-db.example.com");
        config.set(ConfigKey.SERVER_PORT, 9000);
        config.set(ConfigKey.FEATURE_DEBUG_MODE, true);
        config.set(ConfigKey.APP_LOG_LEVEL, "WARN");
        
        // Remove listener
        config.removeListener(listener);
        
        IO.println("\nListener removed, making more changes (no output expected):");
        config.set(ConfigKey.DB_PORT, 5433);
        config.set(ConfigKey.FEATURE_CACHE, false);
        
        // Show all configuration keys with descriptions
        IO.println("\nAvailable Configuration Keys:");
        for (ConfigKey key : ConfigKey.values()) {
            IO.println(String.format(
                "  %-30s (%s): %s",
                key.getKey(),
                key.getType().getSimpleName(),
                key.getDescription()
            ));
        }
    }
}