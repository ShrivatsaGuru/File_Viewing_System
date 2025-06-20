package com.webserver.controller;

import com.webserver.model.FileInfo;
import com.webserver.service.FileServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.*;

@RestController
@RequestMapping("/api/web/files")
@CrossOrigin(origins = "http://localhost:4200")
public class WebFileController {
    
    @Autowired
    private FileServerService fileServerService;
    @Autowired
     private RestTemplate restTemplate;

@GetMapping("/list")
public ResponseEntity<List<FileInfo>> getAllFiles() {
    try {
        // Forward the call to the actual file server
        String url = "http://file-server:8081/api/files/list";
        ResponseEntity<FileInfo[]> response = restTemplate.getForEntity(url, FileInfo[].class);
        List<FileInfo> files = Arrays.asList(response.getBody());
        return ResponseEntity.ok(files);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    @GetMapping("/proxy/files")
public ResponseEntity<List<FileInfo>> getFilesFromFileServer() {
    String url = "http://file-server:8081/api/files/list";
    ResponseEntity<FileInfo[]> response = restTemplate.getForEntity(url, FileInfo[].class);
    List<FileInfo> files = Arrays.asList(response.getBody());
    return ResponseEntity.ok(files);
}

    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileServerService.downloadFile(fileName);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/content/{fileName}")
    public ResponseEntity<String> getFileContent(@PathVariable String fileName) {
        try {
            String content = fileServerService.getFileContent(fileName);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get file content");
        }
    }
    
    @GetMapping("/view/{fileName}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileServerService.viewFile(fileName);
            
            String fileType = getFileExtension(fileName).toLowerCase();
            MediaType mediaType = getMediaType(fileType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(fileContent);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
