package com.cowrite.project.model.vo;

public class UploadFileVO {

    private String filename;

    private String refUrl;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder 静态内部类
    public static class Builder {
        private String filename;
        private String refUrl;

        private Builder() {}

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder refUrl(String refUrl) {
            this.refUrl = refUrl;
            return this;
        }

        public UploadFileVO build() {
            UploadFileVO uploadFileVO = new UploadFileVO();
            uploadFileVO.filename = this.filename;
            uploadFileVO.refUrl = this.refUrl;
            return uploadFileVO;
        }
    }
}
