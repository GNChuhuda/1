package org.example.abe_test.controller;

import org.example.abe_test.service.EqualityTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equality-test")
@CrossOrigin(origins = "*")
public class EqualityTestController {

    @Autowired
    private EqualityTestService equalityTestService;

    /**
     * 执行等值测试
     */
    @PostMapping("/perform")
    public ResponseEntity<Map<String, Object>> performEqualityTest(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> testFileIds = (List<Integer>) request.get("testFileIds");
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) request.get("userIds");
            
            // 转换Integer为Long
            List<Long> longTestFileIds = testFileIds.stream()
                    .map(Integer::longValue)
                    .toList();
            
            boolean result = equalityTestService.performEqualityTest(longTestFileIds, userIds);
            
            Map<String, Object> response = Map.of(
                    "result", result,
                    "message", result ? "等值测试通过" : "等值测试失败"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量授权用户并获取Token
     */
    @PostMapping("/authorize")
    public ResponseEntity<List<String>> authorizeUsers(@RequestBody List<String> userIds) {
        try {
            List<String> tokens = equalityTestService.authorizeUsers(userIds);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
