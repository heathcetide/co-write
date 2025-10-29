package com.cowrite.project.model.dto.organization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("创建组织请求DTO")
public class CreateOrganizationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组织名称", required = true)
    @NotBlank(message = "组织名称不能为空")
    private String name;

    @ApiModelProperty(value = "组织描述")
    private String description;

    @ApiModelProperty(value = "是否公开发布 (1=是, 0=否)")
    private String published = "0";  // 默认不公开

    @ApiModelProperty(value = "最大成员数量限制")
    private Integer maxMembers;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        if ("yes".equals(published) || "1".equals(published)) {
            this.published = "1";
        } else {
            this.published = "0";
        }
    }
    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }
}