package org.example.abe_test.service;

import org.example.abe_test.dto.AttributeDto;
import org.example.abe_test.dto.AttributePoolDto;
import org.example.abe_test.dto.UserDto;
import org.example.abe_test.model.AttributePool;
import org.example.abe_test.model.User;
import org.example.abe_test.model.UserAttribute;
import org.example.abe_test.repository.AttributePoolRepository;
import org.example.abe_test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttributePoolRepository attributePoolRepository;

    @Autowired
    private AbeService abeService;

    /**
     * 上传属性池文件，解析并保存到数据库
     */
    public List<AttributePoolDto> uploadAttributePool(List<AttributePoolDto> attributePools) {
        // 清空现有属性池
        attributePoolRepository.deleteAll();
        
        // 保存新的属性池
        List<AttributePool> entities = attributePools.stream()
                .map(dto -> {
                    AttributePool entity = new AttributePool();
                    entity.setName(dto.getName());
                    entity.setSelected(dto.getSelected());
                    return entity;
                })
                .collect(Collectors.toList());
        
        List<AttributePool> saved = attributePoolRepository.saveAll(entities);
        
        return saved.stream()
                .map(entity -> new AttributePoolDto(entity.getId(), entity.getName(), entity.getSelected()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有属性池
     */
    public List<AttributePoolDto> getAllAttributePools() {
        return attributePoolRepository.findAll().stream()
                .map(entity -> new AttributePoolDto(entity.getId(), entity.getName(), entity.getSelected()))
                .collect(Collectors.toList());
    }

    /**
     * 创建用户并生成私钥
     */
    public UserDto createUser(String userId, List<AttributeDto> attributes) {
        // 创建用户实体
        User user = new User();
        user.setId(userId);
        
        // 生成私钥
        List<String> attributeNames = attributes.stream()
                .map(AttributeDto::getName)
                .collect(Collectors.toList());
        String privateKey = abeService.generatePrivateKey(attributeNames);
        user.setPrivateKeyBase64(privateKey);
        
        // 保存用户属性
        List<UserAttribute> userAttributes = attributes.stream()
                .map(dto -> {
                    UserAttribute userAttr = new UserAttribute();
                    userAttr.setName(dto.getName());
                    userAttr.setUser(user);
                    return userAttr;
                })
                .collect(Collectors.toList());
        user.setAttributes(userAttributes);
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 返回DTO
        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setPrivateKey(savedUser.getPrivateKeyBase64());
        userDto.setAttributes(attributes);
        
        return userDto;
    }

    /**
     * 根据用户ID获取用户
     */
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setPrivateKey(user.getPrivateKeyBase64());
        userDto.setAttributes(user.getAttributes().stream()
                .map(attr -> new AttributeDto(attr.getName()))
                .collect(Collectors.toList()));
        
        return userDto;
    }

    /**
     * 获取用户Token
     */
    public String getUserToken(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        return abeService.extractToken(user.getPrivateKeyBase64());
    }
}
