package org.example.abe_test.repository;

import org.example.abe_test.model.TestFile;
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
public interface TestFileRepository extends JpaRepository<TestFile, Long> {
    
    /**
     * 根据用户ID查找测试文件
     */
    List<TestFile> findByUserId(String userId);
    
    /**
     * 根据用户ID分页查询测试文件
     */
    Page<TestFile> findByUserId(String userId, Pageable pageable);
    
    /**
     * 根据用户ID和文件名查找测试文件
     */
    List<TestFile> findByUserIdAndName(String userId, String name);
    
    /**
     * 根据文件名模糊查询
     */
    List<TestFile> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据内容类型查找文件
     */
    List<TestFile> findByContentType(String contentType);
    
    /**
     * 根据用户ID和内容类型查找文件
     */
    List<TestFile> findByUserIdAndContentType(String userId, String contentType);
    
    /**
     * 根据创建时间范围查找文件
     */
    List<TestFile> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和创建时间范围查找文件
     */
    List<TestFile> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找有Token的文件
     */
    @Query("SELECT tf FROM TestFile tf WHERE tf.token IS NOT NULL AND tf.token != ''")
    List<TestFile> findFilesWithToken();
    
    /**
     * 查找没有Token的文件
     */
    @Query("SELECT tf FROM TestFile tf WHERE tf.token IS NULL OR tf.token = ''")
    List<TestFile> findFilesWithoutToken();
    
    /**
     * 根据用户ID查找有Token的文件
     */
    @Query("SELECT tf FROM TestFile tf WHERE tf.userId = :userId AND tf.token IS NOT NULL AND tf.token != ''")
    List<TestFile> findFilesWithTokenByUserId(@Param("userId") String userId);
    
    /**
     * 统计用户文件数量
     */
    long countByUserId(String userId);
    
    /**
     * 统计特定内容类型的文件数量
     */
    long countByContentType(String contentType);
    
    /**
     * 统计用户特定内容类型的文件数量
     */
    long countByUserIdAndContentType(String userId, String contentType);
    
    /**
     * 根据文件大小范围查找文件
     */
    @Query("SELECT tf FROM TestFile tf WHERE LENGTH(tf.fileContent) BETWEEN :minSize AND :maxSize")
    List<TestFile> findByFileSizeBetween(@Param("minSize") long minSize, @Param("maxSize") long maxSize);
    
    /**
     * 根据用户ID和文件大小范围查找文件
     */
    @Query("SELECT tf FROM TestFile tf WHERE tf.userId = :userId AND LENGTH(tf.fileContent) BETWEEN :minSize AND :maxSize")
    List<TestFile> findByUserIdAndFileSizeBetween(@Param("userId") String userId, @Param("minSize") long minSize, @Param("maxSize") long maxSize);
    
    /**
     * 查找最近创建的文件
     */
    @Query("SELECT tf FROM TestFile tf ORDER BY tf.createdAt DESC")
    List<TestFile> findRecentFiles(Pageable pageable);
    
    /**
     * 根据用户ID查找最近创建的文件
     */
    @Query("SELECT tf FROM TestFile tf WHERE tf.userId = :userId ORDER BY tf.createdAt DESC")
    List<TestFile> findRecentFilesByUserId(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 批量删除用户的文件
     */
    void deleteByUserId(String userId);
    
    /**
     * 批量删除指定ID的文件
     */
    void deleteByIdIn(List<Long> ids);
    
    /**
     * 根据Token查找文件
     */
    Optional<TestFile> findByToken(String token);
    
    /**
     * 根据Token列表查找文件
     */
    List<TestFile> findByTokenIn(List<String> tokens);
}
