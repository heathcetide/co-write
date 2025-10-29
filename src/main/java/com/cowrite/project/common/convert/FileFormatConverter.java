package com.cowrite.project.common.convert;

import java.io.File;

public interface FileFormatConverter {
    boolean supports(String sourceFormat, String targetFormat);
    void convert(File source, File target) throws Exception;
}
