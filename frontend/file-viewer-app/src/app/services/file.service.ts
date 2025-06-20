import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FileInfo } from '../models/file-info.model'; // adjust this path if needed


@Injectable({
  providedIn: 'root'
})
export class FileService {
private baseUrl = 'http://localhost:8080/api/web/files';



  constructor(private http: HttpClient) { }

getAllFiles(): Observable<FileInfo[]> {
  return this.http.get<FileInfo[]>('http://localhost:8080/api/web/files/list');
}


downloadFile(fileName: string): Observable<Blob> {
  return this.http.get(`${this.baseUrl}/download/${fileName}`, {
    responseType: 'blob'
  });
}

getFileContent(fileName: string): Observable<string> {
  return this.http.get(`${this.baseUrl}/content/${fileName}`, {
    responseType: 'text'
  });
}

viewFile(fileName: string): Observable<Blob> {
  return this.http.get(`${this.baseUrl}/view/${fileName}`, {
    responseType: 'blob'
  });
}
}
