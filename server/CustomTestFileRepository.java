package org.example.abe_test.repository.custom;

import org.example.abe_test.model.TestFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 自定义测试文件Repository接口
 * 用于处理复杂的文件查询和统计逻辑
 */
public interface CustomTestFileRepository {
    
    /**
     * 根据多个条件动态查询测试文件
     * @param userId 用户ID（可选）
     * @param fileName 文件名（可选）
     * @param contentType 内容类型（可选）
     * @param hasToken 是否有Token（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param minSize 最小文件大小（可选）
     * @param maxSize 最大文件大小（可选）
     * @param pageable 分页参数
     * @return 分页文件列表
     */
    Page<TestFile> findFilesByConditions(String userId, String fileName, String contentType,
                                       Boolean hasToken, LocalDateTime startDate, LocalDateTime endDate,
                                       Long minSize, Long maxSize, Pageable pageable);
    
    /**
     * 获取文件统计信息
     * @return 统计信息映射
     */
    Map<String, Object> getFileStatistics();
    
    /**
     * 获取用户文件统计信息
     * @param userId 用户ID
     * @return 统计信息映射
     */
    Map<String, Object> getUserFileStatistics(String userId);
    
    /**
     * 按内容类型统计文件数量
     * @return 内容类型和文件数量的映射
     */
    Map<String, Long> getFileCountByContentType();
    
    /**
     * 按用户统计文件数量
     * @return 用户ID和文件数量的映射
     */
    Map<String, Long> getFileCountByUser();
    
    /**
     * 查找重复文件（基于内容哈希）
     * @return 重复文件组列表
     */
    List<List<TestFile>> findDuplicateFiles();
    
    /**
     * 批量更新文件Token
     * @param fileIds 文件ID列表
     * @param tokens Token列表
     * @return 更新成功的文件数量
     */
    int batchUpdateTokens(List<Long> fileIds, List<String> tokens);
    
    /**
     * 清理过期文件
     * @param beforeDate 过期日期
     * @return 清理的文件数量
     */
    int cleanupExpiredFiles(LocalDateTime beforeDate);
    
    /**
     * 获取文件大小分布统计
     * @return 大小范围和文件数量的映射
     */
    Map<String, Long> getFileSizeDistribution();
    
    /**
     * 查找大文件
     * @param sizeThreshold 大小阈值（字节）
     * @return 大文件列表
     */
    List<TestFile> findLargeFiles(long sizeThreshold);
    
    /**
     * 按时间段统计文件上传情况
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param intervalHours 时间间隔（小时）
     * @return 时间段和文件数量的映射
     */
    Map<String, Long> getFileUploadStatisticsByTime(LocalDateTime startDate, LocalDateTime endDate, int intervalHours);
}
