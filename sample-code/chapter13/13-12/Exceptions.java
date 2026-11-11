// 本文では省略している補助の型
record User(String id) {}

class UserNotFoundException extends Exception {
    UserNotFoundException(String message) { super(message); }
}

class ValidationException extends Exception {
    ValidationException(String message) { super(message); }
}

class DatabaseException extends Exception {
    DatabaseException(String message, Throwable cause) { super(message, cause); }
}
