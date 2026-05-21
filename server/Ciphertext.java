package org.example.abe_test.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ciphertexts")
public class Ciphertext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "plain_text_file")
    private byte[] plainTextFile;

    @Lob
    @Column(name = "private_file")
    private byte[] privateFile;

    @Lob
    @Column(name = "ciphertext_file")
    private byte[] ciphertextFile;

    @Column(name = "encrypted_result", columnDefinition = "TEXT")
    private String encryptedResult;

    @Column(name = "decrypted_result", columnDefinition = "TEXT")
    private String decryptedResult;

    @Column(name = "access_control_structure", columnDefinition = "TEXT")
    private String accessControlStructure;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPlainTextFile() {
        return plainTextFile;
    }

    public void setPlainTextFile(byte[] plainTextFile) {
        this.plainTextFile = plainTextFile;
    }

    public byte[] getPrivateFile() {
        return privateFile;
    }

    public void setPrivateFile(byte[] privateFile) {
        this.privateFile = privateFile;
    }

    public byte[] getCiphertextFile() {
        return ciphertextFile;
    }

    public void setCiphertextFile(byte[] ciphertextFile) {
        this.ciphertextFile = ciphertextFile;
    }

    public String getEncryptedResult() {
        return encryptedResult;
    }

    public void setEncryptedResult(String encryptedResult) {
        this.encryptedResult = encryptedResult;
    }

    public String getDecryptedResult() {
        return decryptedResult;
    }

    public void setDecryptedResult(String decryptedResult) {
        this.decryptedResult = decryptedResult;
    }

    public String getAccessControlStructure() {
        return accessControlStructure;
    }

    public void setAccessControlStructure(String accessControlStructure) {
        this.accessControlStructure = accessControlStructure;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
