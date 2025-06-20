package com.fileserver.service;

import com.fileserver.model.FileInfo;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {
    
   private final String UPLOAD_DIR = "/home/files/upload";

    
public List<FileInfo> getAllFiles() {
    List<FileInfo> files = new ArrayList<>();
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            return files;
        }
        
        List<Path> filePaths = Files.walk(uploadPath)
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
            
        for (Path filePath : filePaths) {
            File file = filePath.toFile();
            String fileName = file.getName();
            String fileType = getFileExtension(fileName);
            long fileSize = file.length();
            
            // Add debug output
            System.out.println("Processing file: " + fileName + ", type: " + fileType + ", size: " + fileSize);
            
            files.add(new FileInfo(fileName, fileType, fileSize, filePath.toString()));
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return files;
}
    
    public byte[] getFileContent(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return Files.readAllBytes(filePath);
    }
    
    public String getFileContentAsString(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        
        String fileType = getFileExtension(fileName).toLowerCase();
        if (fileType.equals("txt") || fileType.equals("loc")) {
            return new String(Files.readAllBytes(filePath));
        }
        
        throw new UnsupportedOperationException("Cannot read content of file type: " + fileType);
    }
   
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}
