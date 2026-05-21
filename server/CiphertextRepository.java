package org.example.abe_test.repository;

import org.example.abe_test.model.Ciphertext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CiphertextRepository extends JpaRepository<Ciphertext, Long> {
    
    /**
     * 根据创建时间范围查找密文记录
     */
    List<Ciphertext> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据创建时间范围分页查找密文记录
     */
    Page<Ciphertext> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找有加密结果的记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE c.encryptedResult IS NOT NULL AND c.encryptedResult != ''")
    List<Ciphertext> findWithEncryptedResult();
    
    /**
     * 查找有解密结果的记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE c.decryptedResult IS NOT NULL AND c.decryptedResult != ''")
    List<Ciphertext> findWithDecryptedResult();
    
    /**
     * 查找有访问控制结构的记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE c.accessControlStructure IS NOT NULL AND c.accessControlStructure != ''")
    List<Ciphertext> findWithAccessControlStructure();
    
    /**
     * 根据访问控制结构查找记录
     */
    List<Ciphertext> findByAccessControlStructureContaining(String accessControlStructure);
    
    /**
     * 查找最近创建的密文记录
     */
    @Query("SELECT c FROM Ciphertext c ORDER BY c.createdAt DESC")
    List<Ciphertext> findRecentCiphertexts(Pageable pageable);
    
    /**
     * 统计密文记录数量
     */
    @Query("SELECT COUNT(c) FROM Ciphertext c")
    long countAllCiphertexts();
    
    /**
     * 统计有加密结果的记录数量
     */
    @Query("SELECT COUNT(c) FROM Ciphertext c WHERE c.encryptedResult IS NOT NULL AND c.encryptedResult != ''")
    long countWithEncryptedResult();
    
    /**
     * 统计有解密结果的记录数量
     */
    @Query("SELECT COUNT(c) FROM Ciphertext c WHERE c.decryptedResult IS NOT NULL AND c.decryptedResult != ''")
    long countWithDecryptedResult();
    
    /**
     * 根据明文文件大小范围查找记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE LENGTH(c.plainTextFile) BETWEEN :minSize AND :maxSize")
    List<Ciphertext> findByPlainTextFileSizeBetween(@Param("minSize") long minSize, @Param("maxSize") long maxSize);
    
    /**
     * 根据密文文件大小范围查找记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE LENGTH(c.ciphertextFile) BETWEEN :minSize AND :maxSize")
    List<Ciphertext> findByCiphertextFileSizeBetween(@Param("minSize") long minSize, @Param("maxSize") long maxSize);
    
    /**
     * 查找有明文文件但没有密文文件的记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE c.plainTextFile IS NOT NULL AND (c.ciphertextFile IS NULL OR LENGTH(c.ciphertextFile) = 0)")
    List<Ciphertext> findWithPlainTextButNoCiphertext();
    
    /**
     * 查找有密文文件但没有明文文件的记录
     */
    @Query("SELECT c FROM Ciphertext c WHERE c.ciphertextFile IS NOT NULL AND (c.plainTextFile IS NULL OR LENGTH(c.plainTextFile) = 0)")
    List<Ciphertext> findWithCiphertextButNoPlainText();
    
    /**
     * 根据加密结果内容查找记录
     */
    List<Ciphertext> findByEncryptedResultContaining(String encryptedResult);
    
    /**
     * 根据解密结果内容查找记录
     */
    List<Ciphertext> findByDecryptedResultContaining(String decryptedResult);
    
    /**
     * 批量删除指定ID的记录
     */
    void deleteByIdIn(List<Long> ids);
    
    /**
     * 删除指定时间之前的记录
     */
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
    
    /**
     * 查找指定时间之前的记录
     */
    List<Ciphertext> findByCreatedAtBefore(LocalDateTime dateTime);
    
    /**
     * 查找指定时间之后的记录
     */
    List<Ciphertext> findByCreatedAtAfter(LocalDateTime dateTime);
    
    /**
     * 根据访问控制结构精确匹配查找记录
     */
    Optional<Ciphertext> findByAccessControlStructure(String accessControlStructure);
    
    /**
     * 统计指定时间范围内的记录数量
     */
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
}
