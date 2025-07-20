package com.example.exception.patterns;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Null Object Pattern implementations to avoid null checks and exceptions.
 */
public class NullObject {
    
    /**
     * Example User interface and implementations.
     */
    public interface User {
        String getId();
        String getName();
        String getEmail();
        boolean isValid();
        List<String> getPermissions();
    }
    
    /**
     * Regular user implementation.
     */
    public static class RegularUser implements User {
        private final String id;
        private final String name;
        private final String email;
        private final List<String> permissions;
        
        public RegularUser(String id, String name, String email, List<String> permissions) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.permissions = List.copyOf(permissions);
        }
        
        @Override
        public String getId() { return id; }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getEmail() { return email; }
        
        @Override
        public boolean isValid() { return true; }
        
        @Override
        public List<String> getPermissions() { return permissions; }
    }
    
    /**
     * Null Object implementation for User.
     */
    public static class NullUser implements User {
        private static final NullUser INSTANCE = new NullUser();
        
        private NullUser() {}
        
        public static NullUser getInstance() {
            return INSTANCE;
        }
        
        @Override
        public String getId() { return ""; }
        
        @Override
        public String getName() { return "Unknown User"; }
        
        @Override
        public String getEmail() { return ""; }
        
        @Override
        public boolean isValid() { return false; }
        
        @Override
        public List<String> getPermissions() { return Collections.emptyList(); }
    }
    
    /**
     * Example service that uses Null Object pattern.
     */
    public static class UserService {
        private final Map<String, User> users = new ConcurrentHashMap<>();
        
        public UserService() {
            // Add some sample users
            users.put("user1", new RegularUser("user1", "Alice", "alice@example.com", 
                List.of("read", "write")));
            users.put("user2", new RegularUser("user2", "Bob", "bob@example.com", 
                List.of("read")));
        }
        
        /**
         * Find user by ID, returning NullUser instead of null or throwing exception.
         */
        public User findById(String id) {
            return users.getOrDefault(id, NullUser.getInstance());
        }
        
        /**
         * Check if user has specific permission.
         * Works seamlessly with both regular and null users.
         */
        public boolean hasPermission(String userId, String permission) {
            User user = findById(userId);
            return user.getPermissions().contains(permission);
        }
        
        /**
         * Get user display name.
         * Works seamlessly with both regular and null users.
         */
        public String getUserDisplayName(String userId) {
            User user = findById(userId);
            return user.isValid() 
                ? user.getName() + " (" + user.getEmail() + ")"
                : user.getName();
        }
    }
    
    /**
     * Generic Null Object factory.
     */
    public interface NullObjectFactory<T> {
        T createNullObject();
    }
    
    /**
     * Registry for null objects.
     */
    public static class NullObjectRegistry {
        private static final Map<Class<?>, Object> nullObjects = new ConcurrentHashMap<>();
        
        @SuppressWarnings("unchecked")
        public static <T> T getNullObject(Class<T> type, NullObjectFactory<T> factory) {
            return (T) nullObjects.computeIfAbsent(type, k -> factory.createNullObject());
        }
        
        public static void register(Class<?> type, Object nullObject) {
            nullObjects.put(type, nullObject);
        }
        
        @SuppressWarnings("unchecked")
        public static <T> T get(Class<T> type) {
            T nullObject = (T) nullObjects.get(type);
            if (nullObject == null) {
                throw new IllegalArgumentException("No null object registered for " + type);
            }
            return nullObject;
        }
    }
}