package org.example.abe_test.controller;

import org.example.abe_test.model.TestFile;
import org.example.abe_test.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 加密文件
     */
    @PostMapping("/encrypt")
    public ResponseEntity<byte[]> encryptFile(
            @RequestParam("file") MultipartFile plainTextFile,
            @RequestParam("accessControlStructure") String accessControlStructure) {
        try {
            byte[] encryptedFile = fileService.encryptFile(plainTextFile, accessControlStructure);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "encrypted_file.txt");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(encryptedFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 解密文件
     */
    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptFile(
            @RequestParam("privateKey") MultipartFile privateKeyFile,
            @RequestParam("ciphertext") MultipartFile ciphertextFile) {
        try {
            String decryptedResult = fileService.decryptFile(privateKeyFile, ciphertextFile);
            return ResponseEntity.ok(decryptedResult);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 上传测试文件
     */
    @PostMapping("/test/upload")
    public ResponseEntity<TestFile> uploadTestFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("userId") String userId) {
        try {
            TestFile testFile = fileService.uploadTestFile(file, name, userId);
            return ResponseEntity.ok(testFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量上传测试文件
     */
    @PostMapping("/test/upload-batch")
    public ResponseEntity<List<TestFile>> uploadTestFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("names") List<String> names,
            @RequestParam("userId") String userId) {
        try {
            List<TestFile> testFiles = fileService.uploadTestFiles(files, names, userId);
            return ResponseEntity.ok(testFiles);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据用户ID获取测试文件
     */
    @GetMapping("/test/user/{userId}")
    public ResponseEntity<List<TestFile>> getTestFilesByUserId(@PathVariable String userId) {
        try {
            List<TestFile> testFiles = fileService.getTestFilesByUserId(userId);
            return ResponseEntity.ok(testFiles);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
