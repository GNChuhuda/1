package org.example.abe_test.repository;

import org.example.abe_test.model.AttributePool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributePoolRepository extends JpaRepository<AttributePool, Long> {
    
    /**
     * 根据名称查找属性池
     */
    Optional<AttributePool> findByName(String name);
    
    /**
     * 根据名称模糊查询属性池
     */
    List<AttributePool> findByNameContainingIgnoreCase(String name);
    
    /**
     * 查找所有已选中的属性池
     */
    List<AttributePool> findBySelectedTrue();
    
    /**
     * 查找所有未选中的属性池
     */
    List<AttributePool> findBySelectedFalse();
    
    /**
     * 分页查询已选中的属性池
     */
    Page<AttributePool> findBySelectedTrue(Pageable pageable);
    
    /**
     * 分页查询未选中的属性池
     */
    Page<AttributePool> findBySelectedFalse(Pageable pageable);
    
    /**
     * 根据名称和选中状态查找属性池
     */
    List<AttributePool> findByNameAndSelected(String name, Boolean selected);
    
    /**
     * 根据名称模糊查询和选中状态查找属性池
     */
    List<AttributePool> findByNameContainingIgnoreCaseAndSelected(String name, Boolean selected);
    
    /**
     * 统计已选中的属性池数量
     */
    long countBySelectedTrue();
    
    /**
     * 统计未选中的属性池数量
     */
    long countBySelectedFalse();
    
    /**
     * 统计总属性池数量
     */
    @Query("SELECT COUNT(ap) FROM AttributePool ap")
    long countAllAttributePools();
    
    /**
     * 批量更新属性池的选中状态
     */
    @Query("UPDATE AttributePool ap SET ap.selected = :selected WHERE ap.id IN :ids")
    int updateSelectedStatusByIds(@Param("selected") Boolean selected, @Param("ids") List<Long> ids);
    
    /**
     * 批量删除指定ID的属性池
     */
    void deleteByIdIn(List<Long> ids);
    
    /**
     * 根据名称列表查找属性池
     */
    List<AttributePool> findByNameIn(List<String> names);
    
    /**
     * 根据名称列表和选中状态查找属性池
     */
    List<AttributePool> findByNameInAndSelected(List<String> names, Boolean selected);
    
    /**
     * 检查属性池名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查指定名称的属性池是否被选中
     */
    @Query("SELECT ap.selected FROM AttributePool ap WHERE ap.name = :name")
    Optional<Boolean> findSelectedByName(@Param("name") String name);
    
    /**
     * 获取所有属性池名称
     */
    @Query("SELECT ap.name FROM AttributePool ap ORDER BY ap.name")
    List<String> findAllNames();
    
    /**
     * 获取已选中的属性池名称
     */
    @Query("SELECT ap.name FROM AttributePool ap WHERE ap.selected = true ORDER BY ap.name")
    List<String> findSelectedNames();
    
    /**
     * 获取未选中的属性池名称
     */
    @Query("SELECT ap.name FROM AttributePool ap WHERE ap.selected = false ORDER BY ap.name")
    List<String> findUnselectedNames();
    
    /**
     * 根据名称前缀查找属性池
     */
    List<AttributePool> findByNameStartingWithIgnoreCase(String prefix);
    
    /**
     * 根据名称后缀查找属性池
     */
    List<AttributePool> findByNameEndingWithIgnoreCase(String suffix);
    
    /**
     * 清空所有属性池
     */
    @Query("DELETE FROM AttributePool")
    void deleteAllAttributePools();
}
