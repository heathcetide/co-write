package com.cowrite.project.utils;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CodeUtilTest {

    private long originalId;
    private String encoded;

    @BeforeEach
    public void init() {
        originalId = SnowflakeIdGenerator.nextId();
        encoded = CodeUtil.encode(originalId);
    }

    @AfterEach
    public void cleanup() {
        originalId = 0;
        encoded = null;
    }

    @Test
    @Order(1)
    public void testEncodeDecodeCorrectness() {
        long decoded = CodeUtil.decode(encoded);
        assertEquals(originalId, decoded);
    }

    @Test
    @Order(2)
    public void testGenerateRedisKey() {
        String redisKey = CodeUtil.generateRedisKey("test@example.com", "prefix:");
        assertTrue(redisKey.startsWith("prefix:"));
        assertEquals(39, redisKey.length()); // 6(prefix) + 32(md5) + 1(:)
    }

    @Test
    @Order(3)
    public void testGenerateRandomCode() {
        String code = CodeUtil.generateRandomCode();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }
}
