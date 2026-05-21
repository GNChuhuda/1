package org.example.abe_test.controller;

import org.example.abe_test.dto.AttributeDto;
import org.example.abe_test.dto.AttributePoolDto;
import org.example.abe_test.dto.UserDto;
import org.example.abe_test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 上传属性池文件
     */
    @PostMapping("/attribute-pool")
    public ResponseEntity<List<AttributePoolDto>> uploadAttributePool(@RequestBody List<AttributePoolDto> attributePools) {
        try {
            List<AttributePoolDto> result = userService.uploadAttributePool(attributePools);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取所有属性池
     */
    @GetMapping("/attribute-pool")
    public ResponseEntity<List<AttributePoolDto>> getAllAttributePools() {
        try {
            List<AttributePoolDto> result = userService.getAllAttributePools();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 创建用户
     */
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestParam String userId, @RequestBody List<AttributeDto> attributes) {
        try {
            UserDto result = userService.createUser(userId, attributes);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        try {
            UserDto result = userService.getUserById(userId);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户Token
     */
    @GetMapping("/{userId}/token")
    public ResponseEntity<String> getUserToken(@PathVariable String userId) {
        try {
            String token = userService.getUserToken(userId);
            if (token != null) {
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
