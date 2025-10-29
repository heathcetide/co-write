package com.cowrite.project.interceptor;


import com.cowrite.project.common.anno.Idempotent;
import com.cowrite.project.exception.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface IdempotentStrategy {

    void handle(HttpServletRequest request, String token, Idempotent methodAnnotation, int timeout, int expire) throws UnauthorizedException, IOException;

}