package com.example.enumpatterns.examples;

import com.example.enumpatterns.permission.*;
import java.util.*;

/**
 * Demonstrates the permission system using EnumSet for high-performance operations.
 * Shows role-based access control and permission management.
 */
public class PermissionDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Enum Permission System Demo ===\n");
        
        // Demo 1: Basic permission operations
        System.out.println("Demo 1: Basic Permission Operations");
        System.out.println("-----------------------------------");
        demoBasicPermissions();
        
        // Demo 2: Role-based permissions
        System.out.println("\nDemo 2: Role-Based Access Control");
        System.out.println("----------------------------------");
        demoRoleBasedPermissions();
        
        // Demo 3: Permission inheritance
        System.out.println("\nDemo 3: Composite Permission Inheritance");
        System.out.println("-----------------------------------------");
        demoPermissionInheritance();
        
        // Demo 4: Performance comparison
        System.out.println("\nDemo 4: EnumSet vs HashSet Performance");
        System.out.println("---------------------------------------");
        demoPerformanceComparison();
        
        // Demo 5: Real-world scenario
        System.out.println("\nDemo 5: Real-World Access Control Scenario");
        System.out.println("-------------------------------------------");
        demoRealWorldScenario();
    }
    
    private static void demoBasicPermissions() {
        // Create user permissions using EnumSet
        EnumSet<Permission> userPerms = EnumSet.of(Permission.READ, Permission.WRITE);
        
        System.out.println("User permissions: " + userPerms);
        System.out.println("Has READ: " + userPerms.contains(Permission.READ));
        System.out.println("Has DELETE: " + userPerms.contains(Permission.DELETE));
        
        // Add permission
        userPerms.add(Permission.EXECUTE);
        System.out.println("After adding EXECUTE: " + userPerms);
        
        // Remove permission
        userPerms.remove(Permission.WRITE);
        System.out.println("After removing WRITE: " + userPerms);
        
        // Set operations
        EnumSet<Permission> adminPerms = EnumSet.of(Permission.USER_MANAGE, Permission.ROLE_MANAGE);
        EnumSet<Permission> combined = EnumSet.copyOf(userPerms);
        combined.addAll(adminPerms);
        System.out.println("Combined permissions: " + combined);
    }
    
    private static void demoRoleBasedPermissions() {
        // Use predefined system roles
        System.out.println("System Roles:");
        for (Role role : Role.SystemRoles.getAllRoles()) {
            System.out.println("  " + role);
        }
        
        // Check permissions for different roles
        Role guest = Role.SystemRoles.GUEST;
        Role user = Role.SystemRoles.USER;
        Role admin = Role.SystemRoles.ADMIN;
        
        System.out.println("\nPermission Checks:");
        System.out.println("Guest can READ: " + guest.hasPermission(Permission.READ));
        System.out.println("Guest can WRITE: " + guest.hasPermission(Permission.WRITE));
        System.out.println("User can DELETE: " + user.hasPermission(Permission.DELETE));
        System.out.println("Admin can USER_MANAGE: " + admin.hasPermission(Permission.USER_MANAGE));
        
        // Create custom role
        Role customRole = new Role(
            "ContentEditor",
            "Can manage content but not users",
            Permission.READ, Permission.WRITE, Permission.DELETE
        );
        System.out.println("\nCustom role: " + customRole);
        System.out.println("Custom role effective permissions: " + customRole.getEffectivePermissions());
    }
    
    private static void demoPermissionInheritance() {
        // Show composite permissions
        Permission fullCrud = Permission.FULL_CRUD;
        System.out.println("FULL_CRUD permission mask: " + fullCrud.getMask());
        System.out.println("FULL_CRUD includes READ: " + fullCrud.includes(Permission.READ));
        System.out.println("FULL_CRUD includes WRITE: " + fullCrud.includes(Permission.WRITE));
        System.out.println("FULL_CRUD includes USER_MANAGE: " + fullCrud.includes(Permission.USER_MANAGE));
        
        // Get included permissions
        System.out.println("\nPermissions included in FULL_CRUD:");
        for (Permission perm : fullCrud.getIncludedPermissions()) {
            System.out.println("  - " + perm + ": " + perm.getDescription());
        }
        
        // Admin composite permission
        Permission adminPerm = Permission.ADMIN;
        System.out.println("\nPermissions included in ADMIN:");
        for (Permission perm : adminPerm.getIncludedPermissions()) {
            System.out.println("  - " + perm + ": " + perm.getDescription());
        }
    }
    
    private static void demoPerformanceComparison() {
        int iterations = 1_000_000;
        
        // EnumSet performance
        EnumSet<Permission> enumSet = EnumSet.allOf(Permission.class);
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            enumSet.contains(Permission.READ);
            enumSet.add(Permission.WRITE);
            enumSet.remove(Permission.EXECUTE);
        }
        
        long enumSetTime = System.nanoTime() - startTime;
        
        // HashSet performance
        Set<Permission> hashSet = new HashSet<>(Arrays.asList(Permission.values()));
        startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            hashSet.contains(Permission.READ);
            hashSet.add(Permission.WRITE);
            hashSet.remove(Permission.EXECUTE);
        }
        
        long hashSetTime = System.nanoTime() - startTime;
        
        // Results
        System.out.println("Performance for " + iterations + " operations:");
        System.out.println("EnumSet: " + enumSetTime / 1_000_000 + " ms");
        System.out.println("HashSet: " + hashSetTime / 1_000_000 + " ms");
        System.out.println("EnumSet is " + (hashSetTime / enumSetTime) + "x faster");
        
        // Memory comparison
        System.out.println("\nMemory efficiency:");
        System.out.println("EnumSet uses bit vector (1 bit per enum value)");
        System.out.println("HashSet uses hash table with object references");
        System.out.println("For " + Permission.values().length + " permissions:");
        System.out.println("  EnumSet: ~8 bytes (64-bit long)");
        System.out.println("  HashSet: ~" + (Permission.values().length * 32) + " bytes (estimate)");
    }
    
    private static void demoRealWorldScenario() {
        // Simulate a content management system
        Map<String, Role> users = new HashMap<>();
        users.put("alice", Role.SystemRoles.ADMIN);
        users.put("bob", Role.SystemRoles.USER);
        users.put("charlie", Role.SystemRoles.MODERATOR);
        users.put("dave", Role.SystemRoles.GUEST);
        users.put("eve", Role.SystemRoles.AUDITOR);
        
        // Resources with required permissions
        Map<String, Permission> resources = new HashMap<>();
        resources.put("/api/users", Permission.USER_MANAGE);
        resources.put("/api/content/read", Permission.READ);
        resources.put("/api/content/write", Permission.WRITE);
        resources.put("/api/content/delete", Permission.DELETE);
        resources.put("/api/audit-logs", Permission.AUDIT_VIEW);
        resources.put("/api/system/config", Permission.SYSTEM_CONFIG);
        
        // Check access
        System.out.println("Access Control Matrix:");
        System.out.println("User     | Role       | " + String.join(" | ", resources.keySet()));
        System.out.println("---------+------------+" + "-".repeat(resources.size() * 25));
        
        for (Map.Entry<String, Role> userEntry : users.entrySet()) {
            String userName = userEntry.getKey();
            Role userRole = userEntry.getValue();
            
            System.out.printf("%-8s | %-10s |", userName, userRole.getName());
            
            for (Map.Entry<String, Permission> resourceEntry : resources.entrySet()) {
                Permission required = resourceEntry.getValue();
                boolean hasAccess = userRole.hasPermission(required);
                System.out.printf(" %-23s |", hasAccess ? "✓ Allowed" : "✗ Denied");
            }
            System.out.println();
        }
        
        // Dynamic permission granting
        System.out.println("\nDynamic Permission Management:");
        Role bobRole = users.get("bob");
        System.out.println("Bob's current permissions: " + bobRole.getPermissions());
        
        // Grant DELETE permission to Bob
        Role bobWithDelete = bobRole.withPermission(Permission.DELETE);
        users.put("bob", bobWithDelete);
        System.out.println("Bob's new permissions: " + bobWithDelete.getPermissions());
        System.out.println("Bob can now delete content: " + bobWithDelete.hasPermission(Permission.DELETE));
        
        // Create a new role with specific permissions
        Role contentManager = new Role(
            "ContentManager",
            "Full content management without admin rights",
            Permission.FULL_CRUD,
            Permission.AUDIT_VIEW
        );
        
        users.put("frank", contentManager);
        System.out.println("\nNew user Frank with ContentManager role:");
        System.out.println("Permissions: " + contentManager.getEffectivePermissions());
        System.out.println("Can manage content: " + contentManager.hasAllPermissions(
            Permission.READ, Permission.WRITE, Permission.DELETE));
        System.out.println("Can manage users: " + contentManager.hasPermission(Permission.USER_MANAGE));
    }
}