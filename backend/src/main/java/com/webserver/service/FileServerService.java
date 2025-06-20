package com.webserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webserver.model.FileInfo;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class FileServerService {
    
    private final String FILE_SERVER_BASE_URL = "http://192.168.29.162:8081/api/files";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<FileInfo> getAllFiles() throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return objectMapper.readValue(response.toString(), new TypeReference<List<FileInfo>>(){});
        } else {
            throw new RuntimeException("Failed to fetch files from file server");
        }
    }
    
    public byte[] downloadFile(String fileName) throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/download/" + fileName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            return outputStream.toByteArray();
        } else {
            throw new RuntimeException("Failed to download file from file server");
        }
    }
    
    public String getFileContent(String fileName) throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/content/" + fileName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();
            
            return response.toString();
        } else {
            throw new RuntimeException("Failed to get file content from file server");
        }
    }
    
    public byte[] viewFile(String fileName) throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/view/" + fileName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            return outputStream.toByteArray();
        } else {
            throw new RuntimeException("Failed to view file from file server");
        }
    }
}
