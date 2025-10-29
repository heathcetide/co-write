package com.cowrite.project.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IpUtilsTest {

    private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void testGetIpAddr_withXForwardedFor() {
        when(request.getHeader("x-forwarded-for")).thenReturn("192.168.0.1");
        String ip = IpUtils.getIpAddr(request);
        assertEquals("192.168.0.1", ip);
    }

    @Test
    public void testInternalIp_true() {
        assertTrue(IpUtils.internalIp("10.0.0.1"));
        assertTrue(IpUtils.internalIp("127.0.0.1"));
        assertTrue(IpUtils.internalIp("192.168.1.1"));
    }

    @Test
    public void testInternalIp_false() {
        assertFalse(IpUtils.internalIp("8.8.8.8"));
    }

    @Test
    public void testTextToNumericFormatV4_valid() {
        byte[] bytes = IpUtils.textToNumericFormatV4("192.168.1.1");
        assertNotNull(bytes);
        assertEquals(4, bytes.length);
    }

    @Test
    public void testTextToNumericFormatV4_invalid() {
        byte[] bytes = IpUtils.textToNumericFormatV4("invalid.ip");
        assertNull(bytes);
    }

    @Test
    public void testIsMatchedIp_exactMatch() {
        assertTrue(IpUtils.isMatchedIp("192.168.0.1", "192.168.0.1"));
        assertFalse(IpUtils.isMatchedIp("192.168.0.1", "10.0.0.1"));
    }

    @Test
    public void testGetHostIpAndHostName() {
        assertNotNull(IpUtils.getHostIp());
        assertNotNull(IpUtils.getHostName());
    }
}
