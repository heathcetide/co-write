package com.cowrite.project.model.dto.document;

public class ValidateSharePasswordRequest {
    private String code;
    private String password;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
