package com.cowrite.project.model.vo;

public class UploadPluginVO {

    private String status;

    private Long id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private final UploadPluginVO instance;

        public Builder() {
            this.instance = new UploadPluginVO();
        }

        public Builder status(String status) {
            this.instance.setStatus(status);
            return this;
        }

        public Builder id(Long id) {
            this.instance.setId(id);
            return this;
        }

        public UploadPluginVO build() {
            return this.instance;
        }
    }

    @Override
    public String toString() {
        return "UploadPluginVO{" +
                "status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
