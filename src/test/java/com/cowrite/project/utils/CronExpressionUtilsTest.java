package com.cowrite.project.utils;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CronExpressionUtilsTest {

    @BeforeEach
    void setUp() {
        System.out.println("开始 CronExpression 测试");
    }

    @AfterEach
    void tearDown() {
        System.out.println("结束 CronExpression 测试");
    }

    @Test
    public void testGenerateCronExpressionWithValidHourMinute() {
        String cron = CronExpressionUtils.generateCronExpression(6, 30);
        assertEquals("0 30 6 * * ?", cron);
    }

    @Test
    public void testGenerateCronExpressionWithInvalidHour() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                CronExpressionUtils.generateCronExpression(24, 0));
        assertTrue(exception.getMessage().contains("小时"));
    }

    @Test
    public void testGenerateCronExpressionWithInvalidMinute() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                CronExpressionUtils.generateCronExpression(6, 60));
        assertTrue(exception.getMessage().contains("分钟"));
    }

    @Test
    public void testGenerateCronExpressionWithValidString() {
        String cron = CronExpressionUtils.generateCronExpression("08:15");
        assertEquals("0 15 8 * * ?", cron);
    }

    @Test
    public void testGenerateCronExpressionWithInvalidString() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                CronExpressionUtils.generateCronExpression("25:99"));
        assertTrue(exception.getMessage().contains("格式"));
    }

    @Test
    public void testValidateValidCronExpression() {
        assertTrue(CronExpressionUtils.validateCronExpression("0 15 8 * * ?"));
    }

    @Test
    public void testValidateInvalidCronExpression() {
        assertFalse(CronExpressionUtils.validateCronExpression("wrong cron"));
    }
}
