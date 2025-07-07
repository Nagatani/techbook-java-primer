package com.example.enumpatterns.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Enum-based singleton pattern for database connection management.
 * Demonstrates thread-safe singleton implementation using enum.
 */
public enum DatabaseConnection {
    INSTANCE;
    
    private final BlockingQueue<Connection> connectionPool;
    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;
    private volatile boolean initialized = false;
    
    DatabaseConnection() {
        // Load configuration (in real app, from config file)
        this.url = "jdbc:h2:mem:testdb";
        this.username = "sa";
        this.password = "";
        this.poolSize = 10;
        this.connectionPool = new LinkedBlockingQueue<>(poolSize);
    }
    
    /**
     * Initialize the connection pool (lazy initialization)
     */
    private synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Create initial connections
            for (int i = 0; i < poolSize; i++) {
                Connection conn = createNewConnection();
                connectionPool.offer(conn);
            }
            initialized = true;
            System.out.println("Database connection pool initialized with " + poolSize + " connections");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }
    
    /**
     * Get a connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        try {
            Connection conn = connectionPool.poll(5, TimeUnit.SECONDS);
            if (conn == null) {
                throw new SQLException("Timeout waiting for available connection");
            }
            
            // Validate connection is still alive
            if (conn.isClosed()) {
                // Create new connection if closed
                conn = createNewConnection();
            }
            
            return new PooledConnection(conn, this);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }
    
    /**
     * Return a connection to the pool
     */
    void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    connectionPool.offer(conn);
                }
            } catch (SQLException e) {
                // Log error and create new connection if needed
                System.err.println("Error returning connection to pool: " + e.getMessage());
            }
        }
    }
    
    /**
     * Create a new database connection
     */
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Get pool statistics
     */
    public PoolStatistics getStatistics() {
        return new PoolStatistics(
            poolSize,
            connectionPool.size(),
            poolSize - connectionPool.size(),
            initialized
        );
    }
    
    /**
     * Shutdown the connection pool
     */
    public synchronized void shutdown() {
        if (!initialized) {
            return;
        }
        
        // Close all connections
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        
        initialized = false;
        System.out.println("Database connection pool shut down");
    }
    
    /**
     * Pool statistics
     */
    public static record PoolStatistics(
        int totalConnections,
        int availableConnections,
        int activeConnections,
        boolean initialized
    ) {
        @Override
        public String toString() {
            return String.format(
                "Pool Statistics: Total=%d, Available=%d, Active=%d, Initialized=%s",
                totalConnections, availableConnections, activeConnections, initialized
            );
        }
    }
    
    /**
     * Wrapper for pooled connections that automatically returns to pool on close
     */
    private static class PooledConnection implements Connection {
        private final Connection delegate;
        private final DatabaseConnection pool;
        private boolean closed = false;
        
        PooledConnection(Connection delegate, DatabaseConnection pool) {
            this.delegate = delegate;
            this.pool = pool;
        }
        
        @Override
        public void close() throws SQLException {
            if (!closed) {
                closed = true;
                // Return to pool instead of closing
                pool.returnConnection(delegate);
            }
        }
        
        // Delegate all other methods to the actual connection
        @Override
        public boolean isClosed() throws SQLException {
            return closed || delegate.isClosed();
        }
        
        // ... implement other Connection methods by delegating to delegate
        // For brevity, using default methods that throw UnsupportedOperationException
        
        @Override
        public java.sql.Statement createStatement() throws SQLException {
            checkClosed();
            return delegate.createStatement();
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql);
        }
        
        private void checkClosed() throws SQLException {
            if (closed) {
                throw new SQLException("Connection is closed");
            }
        }
        
        // Implement remaining Connection interface methods...
        // In a real implementation, all methods would be properly delegated
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return delegate.unwrap(iface);
        }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return delegate.isWrapperFor(iface);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return delegate.prepareCall(sql);
        }
        
        @Override
        public String nativeSQL(String sql) throws SQLException {
            return delegate.nativeSQL(sql);
        }
        
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            delegate.setAutoCommit(autoCommit);
        }
        
        @Override
        public boolean getAutoCommit() throws SQLException {
            return delegate.getAutoCommit();
        }
        
        @Override
        public void commit() throws SQLException {
            delegate.commit();
        }
        
        @Override
        public void rollback() throws SQLException {
            delegate.rollback();
        }
        
        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return delegate.getMetaData();
        }
        
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            delegate.setReadOnly(readOnly);
        }
        
        @Override
        public boolean isReadOnly() throws SQLException {
            return delegate.isReadOnly();
        }
        
        @Override
        public void setCatalog(String catalog) throws SQLException {
            delegate.setCatalog(catalog);
        }
        
        @Override
        public String getCatalog() throws SQLException {
            return delegate.getCatalog();
        }
        
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            delegate.setTransactionIsolation(level);
        }
        
        @Override
        public int getTransactionIsolation() throws SQLException {
            return delegate.getTransactionIsolation();
        }
        
        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return delegate.getWarnings();
        }
        
        @Override
        public void clearWarnings() throws SQLException {
            delegate.clearWarnings();
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.createStatement(resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.util.Map<String,Class<?>> getTypeMap() throws SQLException {
            return delegate.getTypeMap();
        }
        
        @Override
        public void setTypeMap(java.util.Map<String,Class<?>> map) throws SQLException {
            delegate.setTypeMap(map);
        }
        
        @Override
        public void setHoldability(int holdability) throws SQLException {
            delegate.setHoldability(holdability);
        }
        
        @Override
        public int getHoldability() throws SQLException {
            return delegate.getHoldability();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return delegate.setSavepoint();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return delegate.setSavepoint(name);
        }
        
        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            delegate.rollback(savepoint);
        }
        
        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            delegate.releaseSavepoint(savepoint);
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return delegate.prepareStatement(sql, autoGeneratedKeys);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return delegate.prepareStatement(sql, columnIndexes);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return delegate.prepareStatement(sql, columnNames);
        }
        
        @Override
        public java.sql.Clob createClob() throws SQLException {
            return delegate.createClob();
        }
        
        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return delegate.createBlob();
        }
        
        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return delegate.createNClob();
        }
        
        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return delegate.createSQLXML();
        }
        
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return delegate.isValid(timeout);
        }
        
        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            delegate.setClientInfo(name, value);
        }
        
        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            delegate.setClientInfo(properties);
        }
        
        @Override
        public String getClientInfo(String name) throws SQLException {
            return delegate.getClientInfo(name);
        }
        
        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return delegate.getClientInfo();
        }
        
        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return delegate.createArrayOf(typeName, elements);
        }
        
        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return delegate.createStruct(typeName, attributes);
        }
        
        @Override
        public void setSchema(String schema) throws SQLException {
            delegate.setSchema(schema);
        }
        
        @Override
        public String getSchema() throws SQLException {
            return delegate.getSchema();
        }
        
        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            delegate.abort(executor);
        }
        
        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            delegate.setNetworkTimeout(executor, milliseconds);
        }
        
        @Override
        public int getNetworkTimeout() throws SQLException {
            return delegate.getNetworkTimeout();
        }
    }
}