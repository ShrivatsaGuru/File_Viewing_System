package com.webserver.controller;

import com.webserver.model.FileInfo;
import com.webserver.service.FileServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * This controller acts as a proxy between the frontend (Angular)
 * and the actual file server (running in another VM or Docker container).
 */
@RestController
@RequestMapping("/api/web/files")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular app
public class WebFileController {

    @Autowired
    private FileServerService fileServerService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Preferred endpoint: Fetches file list using RestTemplate (for flexibility + clarity).
     */
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> getAllFiles() {
        try {
            // Forward the call to the file server via service (for easier logic control & future enhancements)
            String url = "http://file-server:8081/api/files/list"; // 'file-server' should be the Docker hostname
            ResponseEntity<FileInfo[]> response = restTemplate.getForEntity(url, FileInfo[].class);

            List<FileInfo> files = Arrays.asList(response.getBody());
            return ResponseEntity.ok(files); // 200 OK with list of files
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 error
        }
    }

    /**
     * Alternate proxy endpoint (can be removed if unused).
     * Demonstrates raw proxying using RestTemplate.
     */
    @GetMapping("/proxy/files")
    public ResponseEntity<List<FileInfo>> getFilesFromFileServer() {
        String url = "http://file-server:8081/api/files/list";
        ResponseEntity<FileInfo[]> response = restTemplate.getForEntity(url, FileInfo[].class);
        List<FileInfo> files = Arrays.asList(response.getBody());
        return ResponseEntity.ok(files);
    }

    /**
     * Downloads a file from the file server and sends it to the frontend as an attachment.
     */
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

    /**
     * Gets plain text content of `.txt` or `.loc` files from the file server.
     */
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

    /**
     * Views a file (PDF/image) inline in the browser.
     */
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

    /**
     * Utility method to extract file extension from file name.
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * Maps known file extensions to appropriate media types.
     */
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
