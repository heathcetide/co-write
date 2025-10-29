package com.cowrite.project.utils;

import com.cowrite.project.utils.SensitiveDataUtils.MaskType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SensitiveDataUtilsTest {

    @Test
    public void testIsValidEmail() {
        assertTrue(SensitiveDataUtils.isValidEmail("test@example.com"));
        assertFalse(SensitiveDataUtils.isValidEmail("invalid@com"));
    }

    @Test
    public void testMaskEmail() {
        assertEquals("t****@example.com", SensitiveDataUtils.maskEmail("test@example.com"));
        assertEquals("****", SensitiveDataUtils.maskEmail(null));
    }

    @Test
    public void testMaskPhone() {
        assertEquals("138****1234", SensitiveDataUtils.maskPhone("138001231234"));
        assertEquals("****", SensitiveDataUtils.maskPhone("123"));
    }

    @Test
    public void testMaskIdCard() {
        assertEquals("110********1234", SensitiveDataUtils.maskIdCard("110123199001011234"));
        assertEquals("****", SensitiveDataUtils.maskIdCard("123"));
    }

    @Test
    public void testMaskBankCard() {
        assertEquals("6222************1234", SensitiveDataUtils.maskBankCard("62223344556677881234"));
        assertEquals("****", SensitiveDataUtils.maskBankCard("1234"));
    }

    @Test
    public void testMaskGeneric() {
        assertEquals("张****丰", SensitiveDataUtils.maskGeneric("张三丰", 1, 1));
        assertEquals("****", SensitiveDataUtils.maskGeneric("小", 1, 1));
    }

    @Test
    public void testMaskName() {
        assertEquals("张*", SensitiveDataUtils.maskName("张三"));
        assertEquals("张*丰", SensitiveDataUtils.maskName("张三丰"));
        assertEquals("*", SensitiveDataUtils.maskName("张"));
    }

    @Test
    public void testMaskAddress() {
        assertEquals("北京市朝阳区****", SensitiveDataUtils.maskAddress("北京市朝阳区建国路99号"));
        assertEquals("****", SensitiveDataUtils.maskAddress("上海市"));
    }

    @Test
    public void testMaskPassport() {
        assertEquals("G******78", SensitiveDataUtils.maskPassport("G12345678"));
        assertEquals("****", SensitiveDataUtils.maskPassport("G12"));
    }

    @Test
    public void testMaskCustom() {
        assertEquals("12####78", SensitiveDataUtils.maskCustom("12345678", 2, 2, '#'));
        assertEquals("****", SensitiveDataUtils.maskCustom("12", 2, 2, '#'));
    }

    @Test
    public void testMaskByType() {
        assertEquals("t****@example.com", SensitiveDataUtils.maskByType("test@example.com", MaskType.EMAIL));
        assertEquals("****", SensitiveDataUtils.maskByType("test", MaskType.CUSTOM));
    }
}
