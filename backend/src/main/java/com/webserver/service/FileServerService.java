package com.webserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webserver.model.FileInfo;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Service class that communicates with the File Server (hosted on another VM or container)
 * to fetch file metadata, download files, retrieve content, and view files.
 */
@Service
public class FileServerService {

    // Base URL of the file server (change as per your File Server IP/port)
    private final String FILE_SERVER_BASE_URL = "http://192.168.29.162:8081/api/files";

    // ObjectMapper is used to convert JSON responses into Java objects
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Calls the file server to fetch a list of all files and their metadata.
     *
     * @return List of FileInfo objects
     * @throws Exception If the request fails or response parsing fails
     */
    public List<FileInfo> getAllFiles() throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response body into a string
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response into a List<FileInfo>
            return objectMapper.readValue(response.toString(), new TypeReference<List<FileInfo>>() {});
        } else {
            throw new RuntimeException("Failed to fetch files from file server");
        }
    }

    /**
     * Downloads the file content as raw bytes from the file server.
     *
     * @param fileName Name of the file to download
     * @return Byte array representing the file
     * @throws Exception If download fails
     */
    public byte[] downloadFile(String fileName) throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/download/" + fileName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the binary content into a byte array
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

    /**
     * Gets the content of a .txt or .loc file from the file server as a plain string.
     *
     * @param fileName Name of the file
     * @return Content of the file as text
     * @throws Exception If the content can't be retrieved
     */
    public String getFileContent(String fileName) throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/content/" + fileName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read and return the full file content line-by-line
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

    /**
     * Fetches the binary content of a file (image/pdf) to be rendered in the browser.
     *
     * @param fileName Name of the file
     * @return Byte array of the file
     * @throws Exception If file cannot be fetched
     */
    public byte[] viewFile(String fileName) throws Exception {
        URL url = new URL(FILE_SERVER_BASE_URL + "/view/" + fileName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read file content into a byte array
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
