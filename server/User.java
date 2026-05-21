package org.example.abe_test.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(length = 64)
    private String id;

    @Lob
    @Column(name = "private_key_base64")
    private String privateKeyBase64;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserAttribute> attributes = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public void setPrivateKeyBase64(String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
    }

    public List<UserAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<UserAttribute> attributes) {
        this.attributes = attributes;
    }
}


