package org.example.abe_test.repository.custom;

import org.example.abe_test.model.User;
import org.example.abe_test.model.UserAttribute;
import org.example.abe_test.repository.UserRepository;
import org.example.abe_test.service.AbeService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义用户Repository实现类
 */
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AbeService abeService;
    
    @Override
 public Page<User> findUsersByConditions(String userId, List<String> attributeNames, 
                                      Boolean hasPrivateKey, Pageable pageable) {
    StringBuilder jpql = new StringBuilder("SELECT DISTINCT u FROM User u LEFT JOIN u.attributes a WHERE 1=1");
    Map<String, Object> parameters = new HashMap<>();
    
    if (userId != null && !userId.trim().isEmpty()) {
        jpql.append(" AND u.id LIKE :userId");
        parameters.put("userId", "%" + userId + "%");
    }
    
    // ✅ 修复：使用 EXISTS 子查询
    if (attributeNames != null && !attributeNames.isEmpty()) {
        jpql.append(" AND EXISTS (SELECT 1 FROM u.attributes a WHERE a.name IN :attributeNames)");
        parameters.put("attributeNames", attributeNames);
    }
    
    if (hasPrivateKey != null) {
        if (hasPrivateKey) {
            jpql.append(" AND u.privateKeyBase64 IS NOT NULL AND u.privateKeyBase64 != ''");
        } else {
            jpql.append(" AND (u.privateKeyBase64 IS NULL OR u.privateKeyBase64 = '')");
        }
    }
        
        // 查询总数
        String countJpql = jpql.toString().replace("SELECT DISTINCT u", "SELECT COUNT(DISTINCT u)");
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        parameters.forEach(countQuery::setParameter);
        long total = countQuery.getSingleResult();
        
        // 分页查询
        jpql.append(" ORDER BY u.id");
        TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class);
        parameters.forEach(query::setParameter);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<User> users = query.getResultList();
        return new PageImpl<>(users, pageable, total);
    }
    
    @Override
    public Map<String, Long> getAttributeDistribution() {
        String jpql = "SELECT a.name, COUNT(DISTINCT u.id) FROM User u JOIN u.attributes a GROUP BY a.name ORDER BY COUNT(DISTINCT u.id) DESC";
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
    public List<User> findUsersByAttributeCombination(List<String> requiredAttributes, 
                                                    List<String> optionalAttributes) {
        if (requiredAttributes == null || requiredAttributes.isEmpty()) {
            return Collections.emptyList();
        }
        
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT DISTINCT u FROM User u WHERE u.id IN (");
        jpql.append("SELECT u2.id FROM User u2 JOIN u2.attributes a2 WHERE a2.name IN :requiredAttributes ");
        jpql.append("GROUP BY u2.id HAVING COUNT(DISTINCT a2.name) = :requiredCount");
        
        if (optionalAttributes != null && !optionalAttributes.isEmpty()) {
            jpql.append(" AND u.id IN (");
            jpql.append("SELECT u3.id FROM User u3 JOIN u3.attributes a3 WHERE a3.name IN :optionalAttributes");
            jpql.append(")");
        }
        
        jpql.append(")");
        
        TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class);
        query.setParameter("requiredAttributes", requiredAttributes);
        query.setParameter("requiredCount", (long) requiredAttributes.size());
        
        if (optionalAttributes != null && !optionalAttributes.isEmpty()) {
            query.setParameter("optionalAttributes", optionalAttributes);
        }
        
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public boolean updateUserAttributes(String userId, List<String> newAttributes) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return false;
            }
            
            // 删除现有属性
            user.getAttributes().clear();
            
            // 添加新属性
            for (String attributeName : newAttributes) {
                UserAttribute userAttribute = new UserAttribute();
                userAttribute.setName(attributeName);
                userAttribute.setUser(user);
                user.getAttributes().add(userAttribute);
            }
            
            // 重新生成私钥
            String newPrivateKey = abeService.generatePrivateKey(newAttributes);
            user.setPrivateKeyBase64(newPrivateKey);
            
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<User> findRecentUsers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        String jpql = "SELECT u FROM User u WHERE u.id IN (SELECT DISTINCT tf.userId FROM TestFile tf WHERE tf.createdAt >= :cutoffDate)";
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("cutoffDate", cutoffDate);
        
        return query.getResultList();
    }
    
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总用户数
        String totalUsersJpql = "SELECT COUNT(u) FROM User u";
        Long totalUsers = entityManager.createQuery(totalUsersJpql, Long.class).getSingleResult();
        statistics.put("totalUsers", totalUsers);
        
        // 有私钥的用户数
        String usersWithKeyJpql = "SELECT COUNT(u) FROM User u WHERE u.privateKeyBase64 IS NOT NULL AND u.privateKeyBase64 != ''";
        Long usersWithKey = entityManager.createQuery(usersWithKeyJpql, Long.class).getSingleResult();
        statistics.put("usersWithPrivateKey", usersWithKey);
        
        // 平均属性数
        String avgAttributesJpql = "SELECT AVG(SIZE(u.attributes)) FROM User u";
        Double avgAttributes = entityManager.createQuery(avgAttributesJpql, Double.class).getSingleResult();
        statistics.put("averageAttributesPerUser", avgAttributes != null ? avgAttributes : 0.0);
        
        // 最多属性的用户数
        String maxAttributesJpql = "SELECT MAX(SIZE(u.attributes)) FROM User u";
        Integer maxAttributes = entityManager.createQuery(maxAttributesJpql, Integer.class).getSingleResult();
        statistics.put("maxAttributesPerUser", maxAttributes != null ? maxAttributes : 0);
        
        return statistics;
    }
    
    @Override
    public List<List<User>> findUserGroupsByAttributes(int minGroupSize) {
        // 查找具有相同属性组合的用户组
        String jpql = "SELECT u FROM User u JOIN u.attributes a GROUP BY u.id HAVING COUNT(a) >= :minAttributes";
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("minAttributes", (long) minGroupSize);
        
        List<User> users = query.getResultList();
        
        // 按属性组合分组
        Map<String, List<User>> groups = new HashMap<>();
        for (User user : users) {
            String attributeKey = user.getAttributes().stream()
                    .map(UserAttribute::getName)
                    .sorted()
                    .collect(Collectors.joining(","));
            
            groups.computeIfAbsent(attributeKey, k -> new ArrayList<>()).add(user);
        }
        
        // 过滤出满足最小组大小的组
        return groups.values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public int batchGeneratePrivateKeys(List<String> userIds) {
        int successCount = 0;
        
        for (String userId : userIds) {
            try {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty()) {
                    List<String> attributeNames = user.getAttributes().stream()
                            .map(UserAttribute::getName)
                            .collect(Collectors.toList());
                    
                    String newPrivateKey = abeService.generatePrivateKey(attributeNames);
                    user.setPrivateKeyBase64(newPrivateKey);
                    userRepository.save(user);
                    successCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return successCount;
    }
}
