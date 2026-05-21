package org.example.abe_test.dto;

import java.util.List;

public class UserDto {
    private String id;
    private String privateKey;
    private List<AttributeDto> attributes;

    public UserDto() {}

    public UserDto(String id, String privateKey, List<AttributeDto> attributes) {
        this.id = id;
        this.privateKey = privateKey;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public List<AttributeDto> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeDto> attributes) {
        this.attributes = attributes;
    }
}
