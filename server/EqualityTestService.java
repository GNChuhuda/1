package org.example.abe_test.service;

import org.example.abe_test.model.TestFile;
import org.example.abe_test.repository.TestFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EqualityTestService {

    @Autowired
    private TestFileRepository testFileRepository;

    @Autowired
    private AbeService abeService;

    @Autowired
    private UserService userService;

    /**
     * 执行等值测试
     */
    public boolean performEqualityTest(List<Long> testFileIds, List<String> userIds) {
        // 获取测试文件
        List<TestFile> testFiles = testFileRepository.findAllById(testFileIds);
        
        // 获取密文内容
        List<String> cipherTexts = testFiles.stream()
                .map(testFile -> new String(testFile.getFileContent()))
                .collect(Collectors.toList());
        
        // 获取用户Token
        List<String> tokens = userIds.stream()
                .map(userService::getUserToken)
                .collect(Collectors.toList());
        
        // 执行等值测试
        boolean result = abeService.equalityTest(cipherTexts, tokens);
        
        // 更新测试文件的Token
        for (int i = 0; i < testFiles.size() && i < tokens.size(); i++) {
            TestFile testFile = testFiles.get(i);
            testFile.setToken(tokens.get(i));
            testFileRepository.save(testFile);
        }
        
        return result;
    }

    /**
     * 批量授权用户并获取Token
     */
    public List<String> authorizeUsers(List<String> userIds) {
        return userIds.stream()
                .map(userService::getUserToken)
                .collect(Collectors.toList());
    }
}
