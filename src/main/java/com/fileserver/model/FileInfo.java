package com.fileserver.model;

/**
 * A simple model class to store metadata about a file.
 */
public class FileInfo {
    
    // Name of the file (e.g., "document.txt")
    private String fileName;
    
    // Type or extension of the file (e.g., "txt", "pdf", "png")
    private String fileType;
    
    // Size of the file in bytes
    private long fileSize;
    
    // Absolute file path on the server (e.g., "/home/files/upload/document.txt")
    private String filePath;

    /**
     * Default constructor (required for frameworks like Spring and Jackson)
     */
    public FileInfo() {}

    /**
     * Parameterized constructor to initialize all fields
     *
     * @param fileName Name of the file
     * @param fileType File extension/type
     * @param fileSize Size of the file in bytes
     * @param filePath Full path to the file on disk
     */
    public FileInfo(String fileName, String fileType, long fileSize, String filePath) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    // ---------- Getters and Setters ----------

    /**
     * Gets the name of the file.
     *
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file.
     *
     * @param fileName Name of the file
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file extension/type.
     *
     * @return fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Sets the file extension/type.
     *
     * @param fileType Extension of the file (e.g., "jpg", "pdf")
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Gets the size of the file in bytes.
     *
     * @return fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the size of the file in bytes.
     *
     * @param fileSize File size in bytes
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Gets the full file path on the server.
     *
     * @return filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the full file path on the server.
     *
     * @param filePath Absolute path to the file
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
