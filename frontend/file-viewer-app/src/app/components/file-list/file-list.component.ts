import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileService } from '../../services/file.service';
import { FileInfo } from '../../models/file-info.model';
import { OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';


@Component({
  selector: 'app-file-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css']
})
export class FileListComponent implements OnInit {
  files: FileInfo[] = [];
  loading = false;
  error = '';
  selectedFile: FileInfo | null = null;
  fileContent = '';
fileUrl: SafeResourceUrl = '';


  constructor(private fileService: FileService, private sanitizer: DomSanitizer) { }


  
ngOnInit(): void {
  console.log('ngOnInit called ✅');

  this.fileService.getAllFiles().subscribe({
    next: (data: FileInfo[]) => {
      console.log('Files received ✅:', data);
      this.files = data;
    },
    error: (error: any) => {
      console.error('Error fetching files ❌:', error);
    }
  });
}



  loadFiles(): void {
    this.loading = true;
    this.error = '';

    this.fileService.getAllFiles().subscribe({
      next: (files) => {
        console.log('Fetched files:', files); // ✅ Debug log added here
        this.files = files;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load files';
        this.loading = false;
        console.error('Error loading files:', error);
      }
    });
  }

  selectFile(file: FileInfo): void {
    this.selectedFile = file;
    this.fileContent = '';
    this.fileUrl = '';

    if (file.fileType.toLowerCase() === 'txt' || file.fileType.toLowerCase() === 'loc') {
      this.loadFileContent(file.fileName);
    } else {
      this.loadFileForViewing(file.fileName);
    }
  }

  loadFileContent(fileName: string): void {
    this.fileService.getFileContent(fileName).subscribe({
      next: (content) => {
        this.fileContent = content;
      },
      error: (error) => {
        this.error = 'Failed to load file content';
        console.error('Error loading file content:', error);
      }
    });
  }

  loadFileForViewing(fileName: string): void {
    this.fileService.viewFile(fileName).subscribe({next: (blob) => {
        const blobUrl = URL.createObjectURL(blob);
this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);

      },
      error: (error) => {
        this.error = 'Failed to load file for viewing';
        console.error('Error loading file for viewing:', error);
      }
    });
  }

  downloadFile(fileName?: string): void {
    if (!fileName) return;
    this.fileService.downloadFile(fileName).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        this.error = 'Failed to download file';
        console.error('Error downloading file:', error);
      }
    });
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  isImageFile(fileType: string): boolean {
    return ['png', 'jpg', 'jpeg'].includes(fileType.toLowerCase());
  }

  isPdfFile(fileType: string): boolean {
    return fileType.toLowerCase() === 'pdf';
  }

  isTextFile(fileType: string): boolean {
    return ['txt', 'loc'].includes(fileType.toLowerCase());
  }
}

