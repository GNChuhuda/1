package org.example.abe_test.repository.custom;

import org.example.abe_test.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 自定义用户Repository接口
 * 用于处理复杂的业务查询逻辑
 */
public interface CustomUserRepository {
    
    /**
     * 根据多个条件动态查询用户
     * @param userId 用户ID（可选）
     * @param attributeNames 属性名称列表（可选）
     * @param hasPrivateKey 是否有私钥（可选）
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    Page<User> findUsersByConditions(String userId, List<String> attributeNames, 
                                   Boolean hasPrivateKey, Pageable pageable);
    
    /**
     * 统计用户属性分布情况
     * @return 属性名称和用户数量的映射
     */
    Map<String, Long> getAttributeDistribution();
    
    /**
     * 查找具有特定属性组合的用户
     * @param requiredAttributes 必需属性列表
     * @param optionalAttributes 可选属性列表
     * @return 符合条件的用户列表
     */
    List<User> findUsersByAttributeCombination(List<String> requiredAttributes, 
                                             List<String> optionalAttributes);
    
    /**
     * 批量更新用户属性
     * @param userId 用户ID
     * @param newAttributes 新属性列表
     * @return 更新是否成功
     */
    boolean updateUserAttributes(String userId, List<String> newAttributes);
    
    /**
     * 查找最近创建的用户
     * @param days 最近天数
     * @return 用户列表
     */
    List<User> findRecentUsers(int days);
    
    /**
     * 获取用户统计信息
     * @return 统计信息映射
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 查找具有相同属性的用户组
     * @param minGroupSize 最小组大小
     * @return 用户组列表
     */
    List<List<User>> findUserGroupsByAttributes(int minGroupSize);
    
    /**
     * 批量生成用户私钥
     * @param userIds 用户ID列表
     * @return 更新成功的用户数量
     */
    int batchGeneratePrivateKeys(List<String> userIds);
}
