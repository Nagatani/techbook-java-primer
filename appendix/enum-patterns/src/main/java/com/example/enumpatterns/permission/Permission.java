package com.example.enumpatterns.permission;

import java.util.*;

/**
 * Permission system using enums with EnumSet for high-performance operations.
 * Demonstrates type-safe permission management and role-based access control.
 */
public enum Permission {
    // Basic permissions
    READ(1, "Read access to resources"),
    WRITE(2, "Write/modify access to resources"),
    EXECUTE(4, "Execute operations"),
    DELETE(8, "Delete resources"),
    
    // Administrative permissions
    USER_MANAGE(16, "Manage user accounts"),
    ROLE_MANAGE(32, "Manage roles and permissions"),
    SYSTEM_CONFIG(64, "Configure system settings"),
    AUDIT_VIEW(128, "View audit logs"),
    
    // Composite permissions
    READ_WRITE(READ.mask | WRITE.mask, "Read and write access"),
    FULL_CRUD(READ.mask | WRITE.mask | EXECUTE.mask | DELETE.mask, "Full CRUD operations"),
    ADMIN(USER_MANAGE.mask | ROLE_MANAGE.mask | SYSTEM_CONFIG.mask | AUDIT_VIEW.mask, "Full admin access");
    
    private final int mask;
    private final String description;
    
    Permission(int mask, String description) {
        this.mask = mask;
        this.description = description;
    }
    
    public int getMask() {
        return mask;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this permission includes another permission
     */
    public boolean includes(Permission other) {
        return (this.mask & other.mask) == other.mask;
    }
    
    /**
     * Get all atomic permissions included in this permission
     */
    public EnumSet<Permission> getIncludedPermissions() {
        EnumSet<Permission> included = EnumSet.noneOf(Permission.class);
        
        for (Permission perm : values()) {
            if ((this.mask & perm.mask) == perm.mask && perm.mask == Integer.bitCount(perm.mask)) {
                included.add(perm);
            }
        }
        
        return included;
    }
    
    /**
     * Create a permission set from bit mask
     */
    public static EnumSet<Permission> fromMask(int mask) {
        EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
        
        for (Permission perm : values()) {
            if ((mask & perm.mask) == perm.mask) {
                permissions.add(perm);
            }
        }
        
        return permissions;
    }
    
    /**
     * Convert permission set to bit mask
     */
    public static int toMask(Set<Permission> permissions) {
        int mask = 0;
        for (Permission perm : permissions) {
            mask |= perm.mask;
        }
        return mask;
    }
}