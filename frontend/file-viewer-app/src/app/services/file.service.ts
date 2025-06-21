import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FileInfo } from '../models/file-info.model'; // Adjust the import path based on your project structure

@Injectable({
  providedIn: 'root'
})
export class FileService {

  // Base URL of the backend API
  private baseUrl = 'http://localhost:8080/api/web/files';

  constructor(private http: HttpClient) { }

  /**
   * Fetches metadata of all files from the backend.
   * @returns Observable emitting an array of FileInfo objects
   */
  getAllFiles(): Observable<FileInfo[]> {
    return this.http.get<FileInfo[]>(`${this.baseUrl}/list`);
  }

  /**
   * Downloads a file as binary (Blob).
   * Used for triggering file downloads like PDF, DOC, etc.
   * @param fileName Name of the file to download
   * @returns Observable emitting a Blob (binary content)
   */
  downloadFile(fileName: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/download/${fileName}`, {
      responseType: 'blob'
    });
  }

  /**
   * Gets the text content of a .txt or .loc file from the backend.
   * @param fileName Name of the text-based file
   * @returns Observable emitting file content as a string
   */
  getFileContent(fileName: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/content/${fileName}`, {
      responseType: 'text'
    });
  }

  /**
   * Fetches a file (like image, PDF) to be viewed directly in the browser.
   * @param fileName Name of the file
   * @returns Observable emitting the file as a Blob
   */
  viewFile(fileName: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/view/${fileName}`, {
      responseType: 'blob'
    });
  }
}
