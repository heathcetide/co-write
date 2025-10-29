package com.cowrite.project.exception;

public class PluginProcessException  extends RuntimeException {
    public PluginProcessException(String msg, Throwable cause) { super(msg, cause); }
    public PluginProcessException(String msg) { super(msg); }
}