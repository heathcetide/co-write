package com.cowrite.project.interceptor;


import com.cowrite.project.common.anno.Idempotent;
import com.cowrite.project.exception.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class AbstractIdempotentStrategy implements IdempotentStrategy {

    protected abstract void validateRequest(HttpServletRequest request, String token, Idempotent methodAnnotation, int timeout, int expire) throws UnauthorizedException, IOException;

    @Override
    public final void handle(HttpServletRequest request, String token, Idempotent methodAnnotation, int timeout, int expire) throws UnauthorizedException, IOException {
        validateRequest(request, token, methodAnnotation, timeout, expire);
    }
}