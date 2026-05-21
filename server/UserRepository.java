package org.example.abe_test.repository;

import org.example.abe_test.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 根据用户ID查找用户（包含属性信息）
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.attributes WHERE u.id = :userId")
    Optional<User> findByIdWithAttributes(@Param("userId") String userId);
    
    /**
     * 查找所有用户（包含属性信息）
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.attributes")
    List<User> findAllWithAttributes();
    
    /**
     * 分页查询用户（包含属性信息）
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.attributes")
    Page<User> findAllWithAttributes(Pageable pageable);
    
    /**
     * 根据属性名称查找用户
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.attributes a WHERE a.name = :attributeName")
    List<User> findByAttributeName(@Param("attributeName") String attributeName);
    
    /**
     * 根据多个属性名称查找用户
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.attributes a WHERE a.name IN :attributeNames")
    List<User> findByAttributeNames(@Param("attributeNames") List<String> attributeNames);
    
    /**
     * 统计用户数量
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
    
    /**
     * 统计具有特定属性的用户数量
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.attributes a WHERE a.name = :attributeName")
    long countUsersByAttribute(@Param("attributeName") String attributeName);
    
    /**
     * 检查用户是否存在
     */
    boolean existsById(@NonNull String userId);
    
    /**
     * 批量删除用户
     */
    void deleteByIdIn(List<String> userIds);
    
    /**
     * 根据私钥查找用户
     */
    Optional<User> findByPrivateKeyBase64(String privateKeyBase64);
}
