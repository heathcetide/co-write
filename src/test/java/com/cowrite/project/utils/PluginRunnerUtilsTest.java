package com.cowrite.project.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PluginRunnerUtilsTest {

    private final PluginRunnerUtils utils = new PluginRunnerUtils();

    @Test
    public void testConvertFileToMultipartFile() throws IOException {
        File temp = File.createTempFile("test-file", ".txt");
        assertNotNull(PluginRunnerUtils.convertFileToMultipartFile(temp));
    }

    @Test
    public void testConvertFileToMultipartFileWithStream() throws IOException {
        File temp = File.createTempFile("test-stream-file", ".txt");
        assertNotNull(PluginRunnerUtils.convertFileToMultipartFileWithStream(temp));
    }

    @Test
    public void testGetJarFileNotFound() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "no-jar-dir");
        tempDir.mkdirs();
        File result = utils.getJarFile(tempDir);
        assertNull(result);
    }
}