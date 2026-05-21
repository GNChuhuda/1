package org.example.abe_test.service;

import org.example.abe_test.model.Ciphertext;
import org.example.abe_test.model.TestFile;
import org.example.abe_test.repository.CiphertextRepository;
import org.example.abe_test.repository.TestFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class FileService {

    @Autowired
    private CiphertextRepository ciphertextRepository;

    @Autowired
    private TestFileRepository testFileRepository;

    @Autowired
    private AbeService abeService;

    /**
     * 加密文件
     */
    public byte[] encryptFile(MultipartFile plainTextFile, String accessControlStructure) throws IOException {
        // 读取明文文件内容
        String plainText = new String(plainTextFile.getBytes());
        
        // 执行加密
        String encryptedResult = abeService.encrypt(plainText, accessControlStructure);
        
        // 保存到数据库
        Ciphertext ciphertext = new Ciphertext();
        ciphertext.setPlainTextFile(plainTextFile.getBytes());
        ciphertext.setAccessControlStructure(accessControlStructure);
        ciphertext.setEncryptedResult(encryptedResult);
        ciphertextRepository.save(ciphertext);
        
        // 返回密文文件
        return encryptedResult.getBytes();
    }

    /**
     * 解密文件
     */
    public String decryptFile(MultipartFile privateKeyFile, MultipartFile ciphertextFile) throws IOException {
        // 读取私钥和密文
        String privateKey = new String(privateKeyFile.getBytes());
        String cipherText = new String(ciphertextFile.getBytes());
        
        // 执行解密
        String decryptedResult = abeService.decrypt(cipherText, privateKey);
        
        // 保存到数据库
        Ciphertext ciphertext = new Ciphertext();
        ciphertext.setPrivateFile(privateKeyFile.getBytes());
        ciphertext.setCiphertextFile(ciphertextFile.getBytes());
        ciphertext.setDecryptedResult(decryptedResult);
        ciphertextRepository.save(ciphertext);
        
        return decryptedResult;
    }

    /**
     * 上传测试文件
     */
    public TestFile uploadTestFile(MultipartFile file, String name, String userId) throws IOException {
        TestFile testFile = new TestFile();
        testFile.setName(name);
        testFile.setFileContent(file.getBytes());
        testFile.setFileName(file.getOriginalFilename());
        testFile.setContentType(file.getContentType());
        testFile.setUserId(userId);
        
        return testFileRepository.save(testFile);
    }

    /**
     * 批量上传测试文件
     */
    public List<TestFile> uploadTestFiles(List<MultipartFile> files, List<String> names, String userId) throws IOException {
        List<TestFile> testFiles = new java.util.ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            TestFile testFile = new TestFile();
            testFile.setName(names.get(i));
            testFile.setFileContent(files.get(i).getBytes());
            testFile.setFileName(files.get(i).getOriginalFilename());
            testFile.setContentType(files.get(i).getContentType());
            testFile.setUserId(userId);
            
            testFiles.add(testFileRepository.save(testFile));
        }
        
        return testFiles;
    }

    /**
     * 根据用户ID获取测试文件
     */
    public List<TestFile> getTestFilesByUserId(String userId) {
        return testFileRepository.findByUserId(userId);
    }
}
