package com.cowrite.project.utils;

import com.cowrite.project.model.entity.PluginConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PluginFileUtilsTest {

    private final PluginFileUtils fileUtils = new PluginFileUtils();
    private File tempDir;

    @BeforeEach
    public void setup() throws IOException {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "plugin-test");
        tempDir.mkdirs();
        File pluginJson = new File(tempDir, "plugin.json");
        try (FileWriter writer = new FileWriter(pluginJson)) {
            writer.write("{\"pluginName\":\"Test\",\"version\":\"1.0.0\",\"author\":\"Tester\"}");
        }
    }

    @Test
    public void testValidatePluginStructure() {
        assertTrue(fileUtils.validatePluginStructure(tempDir));
    }

    @Test
    public void testGetAllFilePaths() {
        List<String> paths = fileUtils.getAllFilePaths(tempDir);
        assertFalse(paths.isEmpty());
    }

    @Test
    public void testGetPluginDescriptor() throws IOException {
        String json = fileUtils.getPluginDescriptor(tempDir);
        assertTrue(json.contains("Test"));
    }

    @Test
    public void testParsePluginConfig() throws IOException {
        PluginConfig config = fileUtils.parsePluginConfig(tempDir);
        assertEquals("Test", config.getPluginName());
    }
}