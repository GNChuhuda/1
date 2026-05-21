package org.example.abe_test.repository.custom;

import org.example.abe_test.model.TestFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义测试文件Repository实现类
 */
@Repository
public class CustomTestFileRepositoryImpl implements CustomTestFileRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Page<TestFile> findFilesByConditions(String userId, String fileName, String contentType,
                                              Boolean hasToken, LocalDateTime startDate, LocalDateTime endDate,
                                              Long minSize, Long maxSize, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT tf FROM TestFile tf WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();
        
        if (userId != null && !userId.trim().isEmpty()) {
            jpql.append(" AND tf.userId = :userId");
            parameters.put("userId", userId);
        }
        
        if (fileName != null && !fileName.trim().isEmpty()) {
            jpql.append(" AND (tf.name LIKE :fileName OR tf.fileName LIKE :fileName)");
            parameters.put("fileName", "%" + fileName + "%");
        }
        
        if (contentType != null && !contentType.trim().isEmpty()) {
            jpql.append(" AND tf.contentType = :contentType");
            parameters.put("contentType", contentType);
        }
        
        if (hasToken != null) {
            if (hasToken) {
                jpql.append(" AND tf.token IS NOT NULL AND tf.token != ''");
            } else {
                jpql.append(" AND (tf.token IS NULL OR tf.token = '')");
            }
        }
        
        if (startDate != null) {
            jpql.append(" AND tf.createdAt >= :startDate");
            parameters.put("startDate", startDate);
        }
        
        if (endDate != null) {
            jpql.append(" AND tf.createdAt <= :endDate");
            parameters.put("endDate", endDate);
        }
        
        if (minSize != null) {
            jpql.append(" AND LENGTH(tf.fileContent) >= :minSize");
            parameters.put("minSize", minSize);
        }
        
        if (maxSize != null) {
            jpql.append(" AND LENGTH(tf.fileContent) <= :maxSize");
            parameters.put("maxSize", maxSize);
        }
        
        // 查询总数
        String countJpql = jpql.toString().replace("SELECT tf", "SELECT COUNT(tf)");
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        parameters.forEach(countQuery::setParameter);
        long total = countQuery.getSingleResult();
        
        // 分页查询
        jpql.append(" ORDER BY tf.createdAt DESC");
        TypedQuery<TestFile> query = entityManager.createQuery(jpql.toString(), TestFile.class);
        parameters.forEach(query::setParameter);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<TestFile> files = query.getResultList();
        return new PageImpl<>(files, pageable, total);
    }
    
    @Override
    public Map<String, Object> getFileStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总文件数
        String totalFilesJpql = "SELECT COUNT(tf) FROM TestFile tf";
        Long totalFiles = entityManager.createQuery(totalFilesJpql, Long.class).getSingleResult();
        statistics.put("totalFiles", totalFiles);
        
        // 有Token的文件数
        String filesWithTokenJpql = "SELECT COUNT(tf) FROM TestFile tf WHERE tf.token IS NOT NULL AND tf.token != ''";
        Long filesWithToken = entityManager.createQuery(filesWithTokenJpql, Long.class).getSingleResult();
        statistics.put("filesWithToken", filesWithToken);
        
        // 总文件大小
        String totalSizeJpql = "SELECT SUM(LENGTH(tf.fileContent)) FROM TestFile tf";
        Long totalSize = entityManager.createQuery(totalSizeJpql, Long.class).getSingleResult();
        statistics.put("totalSize", totalSize != null ? totalSize : 0L);
        
        // 平均文件大小
        String avgSizeJpql = "SELECT AVG(LENGTH(tf.fileContent)) FROM TestFile tf";
        Double avgSize = entityManager.createQuery(avgSizeJpql, Double.class).getSingleResult();
        statistics.put("averageSize", avgSize != null ? avgSize : 0.0);
        
        // 最大文件大小
        String maxSizeJpql = "SELECT MAX(LENGTH(tf.fileContent)) FROM TestFile tf";
        Long maxSize = entityManager.createQuery(maxSizeJpql, Long.class).getSingleResult();
        statistics.put("maxSize", maxSize != null ? maxSize : 0L);
        
        // 最小文件大小
        String minSizeJpql = "SELECT MIN(LENGTH(tf.fileContent)) FROM TestFile tf";
        Long minSize = entityManager.createQuery(minSizeJpql, Long.class).getSingleResult();
        statistics.put("minSize", minSize != null ? minSize : 0L);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getUserFileStatistics(String userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户文件数
        String userFilesJpql = "SELECT COUNT(tf) FROM TestFile tf WHERE tf.userId = :userId";
        TypedQuery<Long> userFilesQuery = entityManager.createQuery(userFilesJpql, Long.class);
        userFilesQuery.setParameter("userId", userId);
        Long userFiles = userFilesQuery.getSingleResult();
        statistics.put("userFiles", userFiles);
        
        // 用户文件总大小
        String userTotalSizeJpql = "SELECT SUM(LENGTH(tf.fileContent)) FROM TestFile tf WHERE tf.userId = :userId";
        TypedQuery<Long> userTotalSizeQuery = entityManager.createQuery(userTotalSizeJpql, Long.class);
        userTotalSizeQuery.setParameter("userId", userId);
        Long userTotalSize = userTotalSizeQuery.getSingleResult();
        statistics.put("userTotalSize", userTotalSize != null ? userTotalSize : 0L);
        
        // 用户有Token的文件数
        String userFilesWithTokenJpql = "SELECT COUNT(tf) FROM TestFile tf WHERE tf.userId = :userId AND tf.token IS NOT NULL AND tf.token != ''";
        TypedQuery<Long> userFilesWithTokenQuery = entityManager.createQuery(userFilesWithTokenJpql, Long.class);
        userFilesWithTokenQuery.setParameter("userId", userId);
        Long userFilesWithToken = userFilesWithTokenQuery.getSingleResult();
        statistics.put("userFilesWithToken", userFilesWithToken);
        
        return statistics;
    }
    
    @Override
    public Map<String, Long> getFileCountByContentType() {
        String jpql = "SELECT tf.contentType, COUNT(tf) FROM TestFile tf GROUP BY tf.contentType ORDER BY COUNT(tf) DESC";
        Query query = entityManager.createQuery(jpql);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }
    
    @Override
    public Map<String, Long> getFileCountByUser() {
        String jpql = "SELECT tf.userId, COUNT(tf) FROM TestFile tf GROUP BY tf.userId ORDER BY COUNT(tf) DESC";
        Query query = entityManager.createQuery(jpql);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }
    
    @Override
    public List<List<TestFile>> findDuplicateFiles() {
        // 查找具有相同文件内容的文件
        String jpql = "SELECT tf FROM TestFile tf ORDER BY LENGTH(tf.fileContent), tf.createdAt";
        TypedQuery<TestFile> query = entityManager.createQuery(jpql, TestFile.class);
        List<TestFile> allFiles = query.getResultList();
        
        // 按文件内容分组
        Map<String, List<TestFile>> contentGroups = new HashMap<>();
        for (TestFile file : allFiles) {
            String contentHash = Arrays.hashCode(file.getFileContent()) + "_" + file.getFileContent().length;
            contentGroups.computeIfAbsent(contentHash, k -> new ArrayList<>()).add(file);
        }
        
        // 返回重复文件组
        return contentGroups.values().stream()
                .filter(group -> group.size() > 1)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public int batchUpdateTokens(List<Long> fileIds, List<String> tokens) {
        if (fileIds.size() != tokens.size()) {
            throw new IllegalArgumentException("文件ID列表和Token列表长度必须相同");
        }
        
        int successCount = 0;
        for (int i = 0; i < fileIds.size(); i++) {
            try {
                String updateJpql = "UPDATE TestFile tf SET tf.token = :token WHERE tf.id = :id";
                Query updateQuery = entityManager.createQuery(updateJpql);
                updateQuery.setParameter("token", tokens.get(i));
                updateQuery.setParameter("id", fileIds.get(i));
                
                int updated = updateQuery.executeUpdate();
                if (updated > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return successCount;
    }
    
    @Override
    @Transactional
    public int cleanupExpiredFiles(LocalDateTime beforeDate) {
        String deleteJpql = "DELETE FROM TestFile tf WHERE tf.createdAt < :beforeDate";
        Query deleteQuery = entityManager.createQuery(deleteJpql);
        deleteQuery.setParameter("beforeDate", beforeDate);
        
        return deleteQuery.executeUpdate();
    }
    
    @Override
    public Map<String, Long> getFileSizeDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        
        // 定义大小范围
        long[] sizeRanges = {0, 1024, 10240, 102400, 1048576, 10485760}; // 0, 1KB, 10KB, 100KB, 1MB, 10MB
        String[] rangeLabels = {"< 1KB", "1KB - 10KB", "10KB - 100KB", "100KB - 1MB", "1MB - 10MB", "> 10MB"};
        
        for (int i = 0; i < sizeRanges.length - 1; i++) {
            String jpql = "SELECT COUNT(tf) FROM TestFile tf WHERE LENGTH(tf.fileContent) >= :minSize AND LENGTH(tf.fileContent) < :maxSize";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("minSize", sizeRanges[i]);
            query.setParameter("maxSize", sizeRanges[i + 1]);
            
            Long count = query.getSingleResult();
            distribution.put(rangeLabels[i], count);
        }
        
        // 处理大于10MB的文件
        String jpql = "SELECT COUNT(tf) FROM TestFile tf WHERE LENGTH(tf.fileContent) >= :minSize";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("minSize", sizeRanges[sizeRanges.length - 1]);
        
        Long count = query.getSingleResult();
        distribution.put(rangeLabels[rangeLabels.length - 1], count);
        
        return distribution;
    }
    
    @Override
    public List<TestFile> findLargeFiles(long sizeThreshold) {
        String jpql = "SELECT tf FROM TestFile tf WHERE LENGTH(tf.fileContent) > :sizeThreshold ORDER BY LENGTH(tf.fileContent) DESC";
        TypedQuery<TestFile> query = entityManager.createQuery(jpql, TestFile.class);
        query.setParameter("sizeThreshold", sizeThreshold);
        
        return query.getResultList();
    }
    
    @Override
    public Map<String, Long> getFileUploadStatisticsByTime(LocalDateTime startDate, LocalDateTime endDate, int intervalHours) {
        Map<String, Long> statistics = new HashMap<>();
        
        LocalDateTime current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        while (current.isBefore(endDate)) {
            LocalDateTime next = current.plusHours(intervalHours);
            if (next.isAfter(endDate)) {
                next = endDate;
            }
            
            String jpql = "SELECT COUNT(tf) FROM TestFile tf WHERE tf.createdAt >= :startTime AND tf.createdAt < :endTime";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("startTime", current);
            query.setParameter("endTime", next);
            
            Long count = query.getSingleResult();
            String timeKey = current.format(formatter) + " - " + next.format(formatter);
            statistics.put(timeKey, count);
            
            current = next;
        }
        
        return statistics;
    }
}
