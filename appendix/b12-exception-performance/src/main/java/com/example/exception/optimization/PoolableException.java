package com.example.exception.optimization;

/**
 * Base class for poolable exceptions.
 */
public abstract class PoolableException extends RuntimeException {
    private String message;
    private boolean inUse;

    protected PoolableException() {
        super(null, null, false, false); // Disable everything for performance
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     * Reset the exception to its initial state for reuse.
     */
    public void reset() {
        this.message = null;
        // Subclasses can override to reset additional state
    }

    @Override
    public Throwable fillInStackTrace() {
        // Never fill stack trace for pooled exceptions
        return this;
    }
}
