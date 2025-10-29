package com.cowrite.project.exception;

public class SSHException extends RuntimeException {
    private final String sessionId;

    public SSHException(String message, String sessionId) {
        super(message);
        this.sessionId = sessionId;
    }

    public SSHException(String message, String sessionId, Throwable cause) {
        super(message, cause);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}