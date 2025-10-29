package com.cowrite.project.utils;

import cn.hutool.core.exceptions.UtilException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlUtilTest {

    @Test
    public void testEscapeOrderBySqlValid() {
        String valid = "name ASC, age DESC";
        assertDoesNotThrow(() -> SqlUtil.escapeOrderBySql(valid));
    }

    @Test
    public void testEscapeOrderBySqlInvalid() {
        String invalid = "name; DROP TABLE users";
        assertThrows(UtilException.class, () -> SqlUtil.escapeOrderBySql(invalid));
    }

    @Test
    public void testIsValidOrderBySql() {
        assertTrue(SqlUtil.isValidOrderBySql("id, name DESC"));
        assertFalse(SqlUtil.isValidOrderBySql("name; DROP TABLE users"));
    }

    @Test
    public void testFilterKeyword() {
        assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("select * from user"));
    }

    @Test
    public void testFilterKeywordSafe() {
        assertDoesNotThrow(() -> SqlUtil.filterKeyword("username asc"));
    }
}
