package com.example.exception.optimization;

import java.util.ArrayList;
import java.util.List;

/**
 * Example of a poolable validation exception for high-frequency validation scenarios.
 */
public class ValidationException extends PoolableException {
    private final List<String> errors = new ArrayList<>();
    private String field;

    public ValidationException() {
        super();
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors); // Return defensive copy
    }

    @Override
    public void reset() {
        super.reset();
        this.field = null;
        this.errors.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ValidationException");
        if (field != null) {
            sb.append(" [field=").append(field).append("]");
        }
        if (!errors.isEmpty()) {
            sb.append(": ").append(String.join(", ", errors));
        }
        return sb.toString();
    }

    /**
     * Factory method for use with ExceptionPool.
     */
    public static ValidationException create() {
        return new ValidationException();
    }
}