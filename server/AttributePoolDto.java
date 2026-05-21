package org.example.abe_test.dto;

public class AttributePoolDto {
    private Long id;
    private String name;
    private Boolean selected;

    public AttributePoolDto() {}

    public AttributePoolDto(Long id, String name, Boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
