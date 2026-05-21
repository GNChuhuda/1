package org.example.abe_test.dto;

public class TestFileDto {
    private Long id;
    private String name;
    private String userId;
    private String token;

    public TestFileDto() {}

    public TestFileDto(Long id, String name, String userId, String token) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.token = token;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
