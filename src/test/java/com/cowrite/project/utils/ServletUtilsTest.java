package com.cowrite.project.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ServletUtilsTest {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testUrlEncodeAndDecode() {
        String input = "email@domain.com 中文";
        String encoded = ServletUtils.urlEncode(input);
        String decoded = ServletUtils.urlDecode(encoded);

        assertNotNull(encoded);
        assertEquals(input, decoded);
    }

    @Test
    public void testIsAjaxRequest_acceptHeader() {
        when(request.getHeader("accept")).thenReturn("application/json");
        assertTrue(ServletUtils.isAjaxRequest(request));
    }

    @Test
    public void testIsAjaxRequest_xRequestedWithHeader() {
        when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
        assertTrue(ServletUtils.isAjaxRequest(request));
    }

    @Test
    public void testIsAjaxRequest_uri() {
        when(request.getRequestURI()).thenReturn("/api/test.json");
        assertFalse(ServletUtils.isAjaxRequest(request));
    }

    @Test
    public void testIsAjaxRequest_ajaxParam() {
        when(request.getParameter("__ajax")).thenReturn("json");
        assertTrue(ServletUtils.isAjaxRequest(request));
    }

    @Test
    public void testRenderStringDoesNotThrow() {
        try {
            when(response.getWriter()).thenReturn(new java.io.PrintWriter(System.out));
            ServletUtils.renderString(response, "{\"success\":true}");
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }
}
