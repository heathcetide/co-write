package com.cowrite.project.common.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class FormatConverter {

    private static final Logger log = LoggerFactory.getLogger(FormatConverter.class);

    private final List<FileFormatConverter> converters;

    public FormatConverter(List<FileFormatConverter> converters) {
        this.converters = converters;
    }

    public void convert(File source, String targetFormat) {
        String sourceFormat = getFileFormat(source);

        FileFormatConverter converter = getConverter(sourceFormat, targetFormat);
        if (converter != null) {
            try {
                converter.convert(source, createTargetFile(source, targetFormat));
                log.info("文件成功转换: {} -> {}", source.getName(), targetFormat);
            } catch (Exception e) {
                log.error("格式转换失败: {} -> {}", sourceFormat, targetFormat, e);
            }
        } else {
            log.warn("未找到转换器: {} -> {}", sourceFormat, targetFormat);
        }
    }

    private String getFileFormat(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

    private File createTargetFile(File source, String targetFormat) {
        String sourcePath = source.getAbsolutePath();
        String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "." + targetFormat;
        return new File(targetPath);
    }

    private FileFormatConverter getConverter(String sourceFormat, String targetFormat) {
        return converters.stream()
                .filter(c -> c.supports(sourceFormat, targetFormat))
                .findFirst()
                .orElse(null);
    }
}
