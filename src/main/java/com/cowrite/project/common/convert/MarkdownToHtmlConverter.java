package com.cowrite.project.common.convert;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class MarkdownToHtmlConverter implements FileFormatConverter {

    @Override
    public boolean supports(String sourceFormat, String targetFormat) {
        return "md".equalsIgnoreCase(sourceFormat) && "html".equalsIgnoreCase(targetFormat);
    }

    @Override
    public void convert(File source, File target) throws IOException {
        String markdown = Files.readString(source.toPath());
        String html = "<html><body>" + markdown.replaceAll("# (.*)", "<h1>$1</h1>") + "</body></html>";
        Files.writeString(target.toPath(), html);
    }
}
