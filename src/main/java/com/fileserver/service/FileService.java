package com.fileserver.service;

import com.fileserver.model.FileInfo;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    // Directory where files are stored
    private final String UPLOAD_DIR = "/home/files/upload";

    /**
     * Retrieves a list of all files in the upload directory.
     * 
     * @return List of FileInfo objects representing each file
     */
    public List<FileInfo> getAllFiles() {
        List<FileInfo> files = new ArrayList<>();
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR); // Convert string path to Path object
            
            // If upload directory doesn't exist, create it and return empty list
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                return files;
            }

            // Recursively find all regular files in the upload directory
            List<Path> filePaths = Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            // For each file, extract name, type, size, and path and add to the list
            for (Path filePath : filePaths) {
                File file = filePath.toFile();
                String fileName = file.getName();                      // File name (e.g., report.txt)
                String fileType = getFileExtension(fileName);         // File extension (e.g., txt)
                long fileSize = file.length();                        // File size in bytes

                // Debug output to confirm file info being processed
                System.out.println("Processing file: " + fileName + ", type: " + fileType + ", size: " + fileSize);

                // Create a FileInfo object and add it to the list
                files.add(new FileInfo(fileName, fileType, fileSize, filePath.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    /**
     * Retrieves the raw byte content of a given file.
     *
     * @param fileName Name of the file to read
     * @return Byte array containing the file's content
     * @throws IOException If file does not exist or can't be read
     */
    public byte[] getFileContent(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, fileName); // Resolve full path to file
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return Files.readAllBytes(filePath); // Return file content as bytes
    }

    /**
     * Retrieves the content of a .txt or .loc file as a string.
     *
     * @param fileName Name of the file to read
     * @return String content of the file
     * @throws IOException If file doesn't exist or can't be read
     * @throws UnsupportedOperationException If file type is unsupported
     */
    public String getFileContentAsString(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        String fileType = getFileExtension(fileName).toLowerCase(); // Extract file extension
        if (fileType.equals("txt") || fileType.equals("loc")) {
            return new String(Files.readAllBytes(filePath)); // Convert file bytes to string
        }

        throw new UnsupportedOperationException("Cannot read content of file type: " + fileType);
    }

    /**
     * Helper method to extract file extension from file name.
     *
     * @param fileName Name of the file
     * @return File extension (without the dot), or empty string if none
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1); // Return substring after last dot
        }
        return ""; // No extension found
    }
}
