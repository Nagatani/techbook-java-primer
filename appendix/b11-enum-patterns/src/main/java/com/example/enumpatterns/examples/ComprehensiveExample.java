package com.example.enumpatterns.examples;

import com.example.enumpatterns.statemachine.*;
import com.example.enumpatterns.permission.*;
import com.example.enumpatterns.strategy.*;
import com.example.enumpatterns.configuration.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Comprehensive example showing how multiple enum patterns work together
 * in a real-world e-commerce system.
 */
public class ComprehensiveExample {
    
    public static void main(String[] args) {
        IO.println("=== E-Commerce System with Enum Patterns ===\n");
        
        // Initialize system
        ECommerceSystem system = new ECommerceSystem();
        
        // Demo user interactions
        demoUserPurchaseFlow(system);
        demoAdminOperations(system);
        demoSystemConfiguration(system);
    }
    
    private static void demoUserPurchaseFlow(ECommerceSystem system) {
        IO.println("User Purchase Flow Demo");
        IO.println("======================\n");
        
        // Create users with different roles
        ECommerceSystem.User regularUser = system.createUser("john_doe", Role.SystemRoles.USER);
        ECommerceSystem.User premiumUser = system.createUser("jane_smith", Role.SystemRoles.USER);
        system.upgradeMembership(premiumUser, PricingStrategy.MembershipTier.GOLD);
        
        // Regular user creates order
        IO.println("1. Regular User Order:");
        Order order1 = system.createOrder(regularUser, 299.99, "123 Main St");
        system.processOrder(order1, regularUser);
        
        // Premium user creates order
        IO.println("\n2. Premium User Order (Gold Member):");
        Order order2 = system.createOrder(premiumUser, 299.99, "456 Oak Ave");
        system.processOrder(order2, premiumUser);
        
        // Show price difference
        IO.println("\n3. Price Comparison:");
        IO.println("   Regular user paid: $" + order1.getTotalAmount());
        IO.println("   Gold member paid: $" + order2.getTotalAmount());
        IO.println("   Savings: $" + (order1.getTotalAmount() - order2.getTotalAmount()));
    }
    
    private static void demoAdminOperations(ECommerceSystem system) {
        IO.println("\n\nAdmin Operations Demo");
        IO.println("====================\n");
        
        // Create admin user
        ECommerceSystem.User admin = system.createUser("admin", Role.SystemRoles.ADMIN);
        
        // Try various operations
        IO.println("1. Admin accessing system resources:");
        system.accessResource(admin, "/api/users", Permission.USER_MANAGE);
        system.accessResource(admin, "/api/system/config", Permission.SYSTEM_CONFIG);
        system.accessResource(admin, "/api/audit-logs", Permission.AUDIT_VIEW);
        
        // Create moderator with limited permissions
        ECommerceSystem.User moderator = system.createUser("moderator", Role.SystemRoles.MODERATOR);
        
        IO.println("\n2. Moderator accessing system resources:");
        system.accessResource(moderator, "/api/users", Permission.USER_MANAGE);
        system.accessResource(moderator, "/api/system/config", Permission.SYSTEM_CONFIG);
        
        // Show order management
        IO.println("\n3. Order Management:");
        Order pendingOrder = system.createOrder(
            system.createUser("customer", Role.SystemRoles.USER),
            199.99,
            "789 Elm St"
        );
        
        system.manageOrder(admin, pendingOrder, "cancel", "Fraudulent payment");
    }
    
    private static void demoSystemConfiguration(ECommerceSystem system) {
        IO.println("\n\nSystem Configuration Demo");
        IO.println("========================\n");
        
        // Show current configuration
        IO.println("1. Current System Configuration:");
        system.showConfiguration();
        
        // Update configuration
        IO.println("\n2. Updating Configuration:");
        system.updateConfiguration(ConfigKey.FEATURE_NEW_UI, "true");
        system.updateConfiguration(ConfigKey.SERVER_PORT, "9090");
        system.updateConfiguration(ConfigKey.FEATURE_DEBUG_MODE, "true");
        
        // Validate configuration
        IO.println("\n3. Configuration Validation:");
        system.validateConfiguration();
    }
    
    /**
     * Mock e-commerce system that combines all enum patterns
     */
    static class ECommerceSystem {
        private final Map<String, User> users = new HashMap<>();
        private final List<Order> orders = new ArrayList<>();
        private final Configuration config = new Configuration();
        private final Map<User, PricingStrategy.MembershipTier> memberships = new HashMap<>();
        
        public ECommerceSystem() {
            // Initialize with default configuration
            config.addListener((key, oldValue, newValue) -> {
                IO.println("  Config changed: " + key.getKey() + " = " + newValue);
            });
        }
        
        public User createUser(String username, Role role) {
            User user = new User(username, role);
            users.put(username, user);
            memberships.put(user, PricingStrategy.MembershipTier.BRONZE);
            return user;
        }
        
        public void upgradeMembership(User user, PricingStrategy.MembershipTier tier) {
            memberships.put(user, tier);
            IO.println("Upgraded " + user.username + " to " + tier + " membership");
        }
        
        public Order createOrder(User user, double amount, String address) {
            // Apply pricing strategy based on user's membership
            PricingStrategy strategy = determinePricingStrategy(user);
            BigDecimal baseAmount = BigDecimal.valueOf(amount);
            
            // Calculate final price
            PricingStrategy.TaxContext tax = new PricingStrategy.TaxContext(new BigDecimal("0.08"));
            PricingStrategy.DiscountContext discount = new PricingStrategy.DiscountContext(
                BigDecimal.ZERO,
                memberships.get(user)
            );
            
            PricingStrategy.PriceCalculation calc = strategy.calculate(baseAmount, tax, discount);
            
            // Create order with calculated price
            Order order = new Order(user.username, calc.total().doubleValue(), address);
            orders.add(order);
            
            IO.println("Created order for " + user.username + " using " + strategy + " pricing");
            IO.println(calc.getBreakdown());
            
            return order;
        }
        
        public void processOrder(Order order, User user) {
            if (!user.role.hasPermission(Permission.EXECUTE)) {
                IO.println("User does not have permission to process orders");
                return;
            }
            
            try {
                // State machine progression
                order.confirm("PAY-" + System.currentTimeMillis());
                Thread.sleep(100);
                order.ship("TRACK-" + System.currentTimeMillis());
                Thread.sleep(100);
                order.deliver(user.username);
                
            } catch (Exception e) {
                System.err.println("Error processing order: " + e.getMessage());
            }
        }
        
        public void accessResource(User user, String resource, Permission required) {
            boolean hasAccess = user.role.hasPermission(required);
            IO.println("%s accessing %s: %s".formatted(
                user.username,
                resource,
                hasAccess ? "✓ Allowed" : "✗ Denied"
            ));
        }
        
        public void manageOrder(User user, Order order, String action, String reason) {
            if (!user.role.hasPermission(Permission.DELETE)) {
                IO.println(user.username + " cannot manage orders (insufficient permissions)");
                return;
            }
            
            IO.println(user.username + " managing order " + order.getOrderId());
            
            switch (action) {
                case "cancel" -> order.cancel(reason);
                case "return" -> order.returnOrder(reason);
                default -> IO.println("Unknown action: " + action);
            }
        }
        
        public void showConfiguration() {
            IO.println("Server Port: " + config.getServerPort());
            IO.println("Debug Mode: " + config.isDebugMode());
            IO.println("New UI Enabled: " + config.isFeatureNewUiEnabled());
            IO.println("Cache Enabled: " + config.isFeatureCacheEnabled());
        }
        
        public void updateConfiguration(ConfigKey key, String value) {
            config.set(key, value);
        }
        
        public void validateConfiguration() {
            Configuration.ValidationResult result = config.validate();
            result.print();
        }
        
        private PricingStrategy determinePricingStrategy(User user) {
            // Special promotional period check
            if (config.get(ConfigKey.FEATURE_NEW_UI)) {
                return PricingStrategy.PROMOTIONAL;
            }
            
            // Member pricing for non-bronze members
            PricingStrategy.MembershipTier tier = memberships.get(user);
            if (tier != PricingStrategy.MembershipTier.BRONZE) {
                return PricingStrategy.MEMBER;
            }
            
            // Default strategy
            return PricingStrategy.STANDARD;
        }
        
        /**
         * Simple user class
         */
        static class User {
            final String username;
            final Role role;
            
            User(String username, Role role) {
                this.username = username;
                this.role = role;
            }
        }
    }
}