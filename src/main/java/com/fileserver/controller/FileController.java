package com.fileserver.controller;

import com.fileserver.model.FileInfo;
import com.fileserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> getAllFiles() {
        try {
            List<FileInfo> files = fileService.getAllFiles();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/test")
public ResponseEntity<String> test() {
    System.out.println("Test endpoint called!");
    return ResponseEntity.ok("Test successful");
}
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileService.getFileContent(fileName);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            
            String fileType = getFileExtension(fileName).toLowerCase();
            MediaType mediaType = getMediaType(fileType);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(fileContent);
                    
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/content/{fileName}")
    public ResponseEntity<String> getFileContent(@PathVariable String fileName) {
        try {
            String content = fileService.getFileContentAsString(fileName);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body("Cannot read content of this file type");
        }
    }
    
    @GetMapping("/view/{fileName}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileService.getFileContent(fileName);
            
            String fileType = getFileExtension(fileName).toLowerCase();
            MediaType mediaType = getMediaType(fileType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(fileContent);
                    
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    private MediaType getMediaType(String fileType) {
        switch (fileType.toLowerCase()) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "txt":
            case "loc":
                return MediaType.TEXT_PLAIN;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
