package com.example.enumpatterns.permission;

import java.util.*;

/**
 * Role-based access control using enum permissions.
 * Shows how to build a complete RBAC system with enums.
 */
public class Role {
    private final String name;
    private final String description;
    private final EnumSet<Permission> permissions;
    
    public Role(String name, String description, Permission... permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions.length > 0 
            ? EnumSet.of(permissions[0], permissions)
            : EnumSet.noneOf(Permission.class);
    }
    
    public Role(String name, String description, EnumSet<Permission> permissions) {
        this.name = name;
        this.description = description;
        this.permissions = EnumSet.copyOf(permissions);
    }
    
    /**
     * Check if this role has a specific permission
     */
    public boolean hasPermission(Permission permission) {
        // Check direct permission
        if (permissions.contains(permission)) {
            return true;
        }
        
        // Check composite permissions
        return permissions.stream()
            .anyMatch(p -> p.includes(permission));
    }
    
    /**
     * Check if this role has all specified permissions
     */
    public boolean hasAllPermissions(Permission... required) {
        return Arrays.stream(required)
            .allMatch(this::hasPermission);
    }
    
    /**
     * Check if this role has any of the specified permissions
     */
    public boolean hasAnyPermission(Permission... required) {
        return Arrays.stream(required)
            .anyMatch(this::hasPermission);
    }
    
    /**
     * Grant a new permission to this role
     */
    public Role withPermission(Permission permission) {
        EnumSet<Permission> newPermissions = EnumSet.copyOf(permissions);
        newPermissions.add(permission);
        return new Role(name, description, newPermissions);
    }
    
    /**
     * Revoke a permission from this role
     */
    public Role withoutPermission(Permission permission) {
        EnumSet<Permission> newPermissions = EnumSet.copyOf(permissions);
        newPermissions.remove(permission);
        return new Role(name, description, newPermissions);
    }
    
    /**
     * Get all effective permissions (including those from composite permissions)
     */
    public Set<Permission> getEffectivePermissions() {
        EnumSet<Permission> effective = EnumSet.noneOf(Permission.class);
        
        for (Permission perm : permissions) {
            effective.add(perm);
            effective.addAll(perm.getIncludedPermissions());
        }
        
        return effective;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<Permission> getPermissions() { return EnumSet.copyOf(permissions); }
    
    @Override
    public String toString() {
        return String.format("%s (%s): %s", name, description, permissions);
    }
    
    /**
     * Pre-defined system roles
     */
    public static class SystemRoles {
        public static final Role GUEST = new Role(
            "Guest",
            "Limited read-only access",
            Permission.READ
        );
        
        public static final Role USER = new Role(
            "User",
            "Standard user access",
            Permission.READ,
            Permission.WRITE,
            Permission.EXECUTE
        );
        
        public static final Role MODERATOR = new Role(
            "Moderator",
            "User management and content moderation",
            Permission.FULL_CRUD,
            Permission.USER_MANAGE
        );
        
        public static final Role ADMIN = new Role(
            "Administrator",
            "Full system access",
            Permission.FULL_CRUD,
            Permission.ADMIN
        );
        
        public static final Role AUDITOR = new Role(
            "Auditor",
            "Read-only access with audit log viewing",
            Permission.READ,
            Permission.AUDIT_VIEW
        );
        
        /**
         * Get role by name
         */
        public static Optional<Role> getByName(String name) {
            return getAllRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase(name))
                .findFirst();
        }
        
        /**
         * Get all pre-defined roles
         */
        public static List<Role> getAllRoles() {
            return List.of(GUEST, USER, MODERATOR, ADMIN, AUDITOR);
        }
    }
}