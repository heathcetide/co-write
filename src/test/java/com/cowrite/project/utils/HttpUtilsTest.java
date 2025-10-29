package com.cowrite.project.utils;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HttpUtilsTest {

    @BeforeEach
    void setUp() {
        System.out.println("开始 HttpUtils 测试");
    }

    @AfterEach
    void tearDown() {
        System.out.println("结束 HttpUtils 测试");
    }

    @Test
    public void testGetBodyString() throws IOException {
        String mockBody = "{\"name\":\"test\"}";
        ServletRequest request = new MockServletRequest(mockBody);
        String result = HttpUtils.getBodyString(request);
        assertEquals(mockBody, result);
    }

    @Test
    public void testSendGetWithEmptyParams() {
        String response = HttpUtils.sendGet("http://example.com");
        assertNotNull(response); // 不断言内容，因为我们无法控制远端
    }

    @Test
    public void testSendGetWithParams() {
        String response = HttpUtils.sendGet("http://example.com", "name=value");
        assertNotNull(response);
    }

    @Test
    public void testSendPostWithParams() {
        String response = HttpUtils.sendPost("http://example.com", "key=value");
        assertNotNull(response);
    }

    // --- 内部类用于伪造 ServletRequest ---
    static class MockServletRequest implements ServletRequest {
        private final ByteArrayInputStream inputStream;

        public MockServletRequest(String body) {
            this.inputStream = new ByteArrayInputStream(body.getBytes());
        }

        @Override
        public ServletInputStream getInputStream() {
            return new ServletInputStream() {
                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        // 其它接口方法留空或默认
        @Override public Object getAttribute(String name) { return null; }
        @Override public void setAttribute(String name, Object o) {}
        @Override public void removeAttribute(String name) {}
        @Override public String getParameter(String name) { return null; }
        @Override public String[] getParameterValues(String name) { return new String[0]; }
        @Override public String getProtocol() { return null; }
        @Override public String getScheme() { return null; }
        @Override public String getServerName() { return null; }
        @Override public int getServerPort() { return 0; }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override public String getRemoteAddr() { return null; }
        @Override public String getRemoteHost() { return null; }
        @Override public void setCharacterEncoding(String env) {}
        @Override public String getCharacterEncoding() { return null; }
        @Override public int getContentLength() { return 0; }
        @Override public long getContentLengthLong() { return 0; }
        @Override public String getContentType() { return null; }
        @Override public java.util.Enumeration<String> getParameterNames() { return null; }
        @Override public java.util.Map<String, String[]> getParameterMap() { return null; }
        @Override public java.util.Enumeration<String> getAttributeNames() { return null; }
        @Override public java.util.Locale getLocale() { return null; }
        @Override public java.util.Enumeration<java.util.Locale> getLocales() { return null; }
        @Override public boolean isSecure() { return false; }
        @Override public javax.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public String getRealPath(String path) { return null; }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override public javax.servlet.ServletContext getServletContext() { return null; }
        @Override public javax.servlet.AsyncContext startAsync() { return null; }
        @Override public javax.servlet.AsyncContext startAsync(ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public javax.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public javax.servlet.DispatcherType getDispatcherType() { return null; }
        @Override public String getLocalName() { return null; }
        @Override public String getLocalAddr() { return null; }
        @Override public int getLocalPort() { return 0; }
    }
}
