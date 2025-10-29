package com.cowrite.project.model.vo;

public class QuickOrganizationVO {
    private Long id;

    private String name;

    private String currentMembers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentMembers() {
        return currentMembers;
    }

    public void setCurrentMembers(String currentMembers) {
        this.currentMembers = currentMembers;
    }
}
