package com.example.enumpatterns.examples;

import com.example.enumpatterns.permission.*;
import java.util.*;

/**
 * Demonstrates the permission system using EnumSet for high-performance operations.
 * Shows role-based access control and permission management.
 */
public class PermissionDemo {
    
    public static void main(String[] args) {
        IO.println("=== Enum Permission System Demo ===\n");
        
        // Demo 1: Basic permission operations
        IO.println("Demo 1: Basic Permission Operations");
        IO.println("-----------------------------------");
        demoBasicPermissions();
        
        // Demo 2: Role-based permissions
        IO.println("\nDemo 2: Role-Based Access Control");
        IO.println("----------------------------------");
        demoRoleBasedPermissions();
        
        // Demo 3: Permission inheritance
        IO.println("\nDemo 3: Composite Permission Inheritance");
        IO.println("-----------------------------------------");
        demoPermissionInheritance();
        
        // Demo 4: Performance comparison
        IO.println("\nDemo 4: EnumSet vs HashSet Performance");
        IO.println("---------------------------------------");
        demoPerformanceComparison();
        
        // Demo 5: Real-world scenario
        IO.println("\nDemo 5: Real-World Access Control Scenario");
        IO.println("-------------------------------------------");
        demoRealWorldScenario();
    }
    
    private static void demoBasicPermissions() {
        // Create user permissions using EnumSet
        EnumSet<Permission> userPerms = EnumSet.of(Permission.READ, Permission.WRITE);
        
        IO.println("User permissions: " + userPerms);
        IO.println("Has READ: " + userPerms.contains(Permission.READ));
        IO.println("Has DELETE: " + userPerms.contains(Permission.DELETE));
        
        // Add permission
        userPerms.add(Permission.EXECUTE);
        IO.println("After adding EXECUTE: " + userPerms);
        
        // Remove permission
        userPerms.remove(Permission.WRITE);
        IO.println("After removing WRITE: " + userPerms);
        
        // Set operations
        EnumSet<Permission> adminPerms = EnumSet.of(Permission.USER_MANAGE, Permission.ROLE_MANAGE);
        EnumSet<Permission> combined = EnumSet.copyOf(userPerms);
        combined.addAll(adminPerms);
        IO.println("Combined permissions: " + combined);
    }
    
    private static void demoRoleBasedPermissions() {
        // Use predefined system roles
        IO.println("System Roles:");
        for (Role role : Role.SystemRoles.getAllRoles()) {
            IO.println("  " + role);
        }
        
        // Check permissions for different roles
        Role guest = Role.SystemRoles.GUEST;
        Role user = Role.SystemRoles.USER;
        Role admin = Role.SystemRoles.ADMIN;
        
        IO.println("\nPermission Checks:");
        IO.println("Guest can READ: " + guest.hasPermission(Permission.READ));
        IO.println("Guest can WRITE: " + guest.hasPermission(Permission.WRITE));
        IO.println("User can DELETE: " + user.hasPermission(Permission.DELETE));
        IO.println("Admin can USER_MANAGE: " + admin.hasPermission(Permission.USER_MANAGE));
        
        // Create custom role
        Role customRole = new Role(
            "ContentEditor",
            "Can manage content but not users",
            Permission.READ, Permission.WRITE, Permission.DELETE
        );
        IO.println("\nCustom role: " + customRole);
        IO.println("Custom role effective permissions: " + customRole.getEffectivePermissions());
    }
    
    private static void demoPermissionInheritance() {
        // Show composite permissions
        Permission fullCrud = Permission.FULL_CRUD;
        IO.println("FULL_CRUD permission mask: " + fullCrud.getMask());
        IO.println("FULL_CRUD includes READ: " + fullCrud.includes(Permission.READ));
        IO.println("FULL_CRUD includes WRITE: " + fullCrud.includes(Permission.WRITE));
        IO.println("FULL_CRUD includes USER_MANAGE: " + fullCrud.includes(Permission.USER_MANAGE));
        
        // Get included permissions
        IO.println("\nPermissions included in FULL_CRUD:");
        for (Permission perm : fullCrud.getIncludedPermissions()) {
            IO.println("  - " + perm + ": " + perm.getDescription());
        }
        
        // Admin composite permission
        Permission adminPerm = Permission.ADMIN;
        IO.println("\nPermissions included in ADMIN:");
        for (Permission perm : adminPerm.getIncludedPermissions()) {
            IO.println("  - " + perm + ": " + perm.getDescription());
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
        IO.println("Performance for " + iterations + " operations:");
        IO.println("EnumSet: " + enumSetTime / 1_000_000 + " ms");
        IO.println("HashSet: " + hashSetTime / 1_000_000 + " ms");
        IO.println("EnumSet is " + (hashSetTime / enumSetTime) + "x faster");
        
        // Memory comparison
        IO.println("\nMemory efficiency:");
        IO.println("EnumSet uses bit vector (1 bit per enum value)");
        IO.println("HashSet uses hash table with object references");
        IO.println("For " + Permission.values().length + " permissions:");
        IO.println("  EnumSet: ~8 bytes (64-bit long)");
        IO.println("  HashSet: ~" + (Permission.values().length * 32) + " bytes (estimate)");
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
        IO.println("Access Control Matrix:");
        IO.println("User     | Role       | " + String.join(" | ", resources.keySet()));
        IO.println("---------+------------+" + "-".repeat(resources.size() * 25));
        
        for (Map.Entry<String, Role> userEntry : users.entrySet()) {
            String userName = userEntry.getKey();
            Role userRole = userEntry.getValue();
            
            IO.print("%-8s | %-10s |".formatted(userName, userRole.getName()));
            
            for (Map.Entry<String, Permission> resourceEntry : resources.entrySet()) {
                Permission required = resourceEntry.getValue();
                boolean hasAccess = userRole.hasPermission(required);
                IO.print(" %-23s |".formatted(hasAccess ? "✓ Allowed" : "✗ Denied"));
            }
            IO.println();
        }
        
        // Dynamic permission granting
        IO.println("\nDynamic Permission Management:");
        Role bobRole = users.get("bob");
        IO.println("Bob's current permissions: " + bobRole.getPermissions());
        
        // Grant DELETE permission to Bob
        Role bobWithDelete = bobRole.withPermission(Permission.DELETE);
        users.put("bob", bobWithDelete);
        IO.println("Bob's new permissions: " + bobWithDelete.getPermissions());
        IO.println("Bob can now delete content: " + bobWithDelete.hasPermission(Permission.DELETE));
        
        // Create a new role with specific permissions
        Role contentManager = new Role(
            "ContentManager",
            "Full content management without admin rights",
            Permission.FULL_CRUD,
            Permission.AUDIT_VIEW
        );
        
        users.put("frank", contentManager);
        IO.println("\nNew user Frank with ContentManager role:");
        IO.println("Permissions: " + contentManager.getEffectivePermissions());
        IO.println("Can manage content: " + contentManager.hasAllPermissions(
            Permission.READ, Permission.WRITE, Permission.DELETE));
        IO.println("Can manage users: " + contentManager.hasPermission(Permission.USER_MANAGE));
    }
}