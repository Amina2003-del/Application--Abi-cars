package ma.abisoft.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file) throws IOException;
    void deleteFile(String fileUrl) throws IOException;
    
    
}