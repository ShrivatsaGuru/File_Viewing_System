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
@CrossOrigin(origins = "*") // Allow cross-origin requests from any origin
public class FileController {

    @Autowired
    private FileService fileService; // Injecting the FileService to handle business logic

    /**
     * Endpoint to list all files available on the server.
     * @return List of FileInfo objects wrapped in ResponseEntity
     */
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> getAllFiles() {
        try {
            List<FileInfo> files = fileService.getAllFiles();
            return ResponseEntity.ok(files); // 200 OK with list of files
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 error
        }
    }

    /**
     * Simple test endpoint to verify backend is up and reachable.
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("Test endpoint called!");
        return ResponseEntity.ok("Test successful");
    }

    /**
     * Endpoint to download a file by its name.
     * @param fileName Name of the file to download
     * @return File content as a downloadable response
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileService.getFileContent(fileName); // Load file content

            // Set response headers to trigger download in browser
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            // Determine the MIME type based on file extension
            String fileType = getFileExtension(fileName).toLowerCase();
            MediaType mediaType = getMediaType(fileType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.notFound().build(); // 404 if file doesn't exist
        }
    }

    /**
     * Endpoint to get readable content of .txt or .loc files.
     * @param fileName Name of the file
     * @return File content as plain text
     */
    @GetMapping("/content/{fileName}")
    public ResponseEntity<String> getFileContent(@PathVariable String fileName) {
        try {
            String content = fileService.getFileContentAsString(fileName);
            return ResponseEntity.ok(content); // 200 OK with file content
        } catch (IOException e) {
            return ResponseEntity.notFound().build(); // 404 if file not found
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body("Cannot read content of this file type");
        }
    }

    /**
     * Endpoint to display a file inline in the browser (PDF, image, etc.).
     * @param fileName Name of the file to view
     * @return File content with inline display headers
     */
    @GetMapping("/view/{fileName}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileService.getFileContent(fileName);

            String fileType = getFileExtension(fileName).toLowerCase();
            MediaType mediaType = getMediaType(fileType);

            // Inline disposition tells browser to try to render the file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    /**
     * Utility method to extract file extension from file name.
     * @param fileName Name of the file
     * @return File extension (without dot)
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return ""; // No extension found
    }

    /**
     * Utility method to return proper MediaType based on file extension.
     * @param fileType File extension (e.g., pdf, txt)
     * @return Corresponding MediaType
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
                return MediaType.APPLICATION_OCTET_STREAM; // Generic binary for unknown types
        }
    }
}
