package com.cowrite.project.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StringUtilsTest {

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("abc"));
    }

    @Test
    public void testHide() {
        String result = StringUtils.hide("123456789", 2, 6);
        assertEquals("12****789", result);
    }

    @Test
    public void testToUnderScoreCase() {
        assertEquals("hello_world", StringUtils.toUnderScoreCase("HelloWorld"));
    }

    @Test
    public void testToCamelCase() {
        assertEquals("userName", StringUtils.toCamelCase("user_name"));
    }

    @Test
    public void testConvertToCamelCase() {
        assertEquals("HelloWorld", StringUtils.convertToCamelCase("HELLO_WORLD"));
    }

    @Test
    public void testFormat() {
        String formatted = StringUtils.format("This is {} and {}", "A", "B");
        assertEquals("This is A and B", formatted);
    }

    @Test
    public void testContainsAnyIgnoreCase() {
        assertTrue(StringUtils.containsAnyIgnoreCase("helloWorld", "WORLD"));
    }

    @Test
    public void testPadl() {
        assertEquals("00042", StringUtils.padl(42, 5));
    }

    @Test
    public void testStr2List() {
        List<String> list = StringUtils.str2List("a,b,,c", ",", true, true);
        assertEquals(Arrays.asList("a", "b", "c"), list);
    }

    @Test
    public void testMatchesAndIsMatch() {
        assertFalse(StringUtils.isMatch("/api/user/1", "/api/**"));
    }
}
