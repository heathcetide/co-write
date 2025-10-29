package com.cowrite.project.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SnowflakeIdGeneratorTest {

    private Set<Long> idSet;

    @BeforeEach
    public void setup() {
        idSet = new HashSet<>();
    }

    @Test
    public void testNextIdIsPositive() {
        long id = SnowflakeIdGenerator.nextId();
        assertTrue(id > 0, "Generated ID should be positive");
    }

    @RepeatedTest(1000)
    public void testUniquenessOfGeneratedIds() {
        long id = SnowflakeIdGenerator.nextId();
        assertFalse(idSet.contains(id), "Duplicate ID detected");
        idSet.add(id);
    }

    @Test
    public void testNextIdMonotonicity() {
        long id1 = SnowflakeIdGenerator.nextId();
        long id2 = SnowflakeIdGenerator.nextId();
        assertTrue(id2 > id1, "IDs should increase over time");
    }
}
