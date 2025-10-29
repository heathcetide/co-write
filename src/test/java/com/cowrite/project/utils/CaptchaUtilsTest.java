package com.cowrite.project.utils;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CaptchaUtilsTest {

    private String captchaText;

    @BeforeEach
    public void setup() {
        captchaText = CaptchaUtils.generateCaptchaText(6);
        assertNotNull(captchaText);
        assertEquals(6, captchaText.length());
    }

    @AfterEach
    public void teardown() {
        captchaText = null;
    }

    @Test
    @Order(1)
    public void testGenerateCaptchaTextLength() {
        assertEquals(6, captchaText.length(), "验证码长度应该为6");
    }

    @Test
    @Order(2)
    public void testGenerateCaptchaImageNotNull() {
        BufferedImage image = CaptchaUtils.generateCaptchaImage(captchaText);
        assertNotNull(image);
        assertEquals(160, image.getWidth());
        assertEquals(40, image.getHeight());
    }
}
