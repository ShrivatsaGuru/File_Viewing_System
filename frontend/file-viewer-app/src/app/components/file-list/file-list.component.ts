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

  // Stores the list of all files fetched from backend
  files: FileInfo[] = [];

  // Loading state indicator
  loading = false;

  // Error message holder
  error = '';

  // Currently selected file from the list
  selectedFile: FileInfo | null = null;

  // Holds text content of selected text-based file
  fileContent = '';

  // Holds URL for viewing images or PDFs safely
  fileUrl: SafeResourceUrl = '';

  constructor(
    private fileService: FileService,
    private sanitizer: DomSanitizer // Used to securely load blob URLs
  ) {}

  /**
   * Called when the component is initialized.
   * Fetches the list of files from the server.
   */
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

  /**
   * Reloads the list of files manually.
   */
  loadFiles(): void {
    this.loading = true;
    this.error = '';

    this.fileService.getAllFiles().subscribe({
      next: (files) => {
        console.log('Fetched files:', files);
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

  /**
   * Handles file selection and determines how to display the file.
   * @param file The selected file from the list
   */
  selectFile(file: FileInfo): void {
    this.selectedFile = file;
    this.fileContent = '';
    this.fileUrl = '';

    if (this.isTextFile(file.fileType)) {
      this.loadFileContent(file.fileName);
    } else {
      this.loadFileForViewing(file.fileName);
    }
  }

  /**
   * Loads text content of a .txt or .loc file.
   * @param fileName Name of the file to load
   */
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

  /**
   * Prepares a file to be viewed inline (e.g. image or PDF).
   * Converts blob to a safe resource URL.
   * @param fileName Name of the file
   */
  loadFileForViewing(fileName: string): void {
    this.fileService.viewFile(fileName).subscribe({
      next: (blob) => {
        const blobUrl = URL.createObjectURL(blob);
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
      },
      error: (error) => {
        this.error = 'Failed to load file for viewing';
        console.error('Error loading file for viewing:', error);
      }
    });
  }

  /**
   * Downloads the specified file from the server.
   * @param fileName Name of the file to download
   */
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
        window.URL.revokeObjectURL(url); // Free up memory
      },
      error: (error) => {
        this.error = 'Failed to download file';
        console.error('Error downloading file:', error);
      }
    });
  }

  /**
   * Converts raw byte size to a human-readable string (KB, MB, etc.).
   * @param bytes File size in bytes
   * @returns Formatted file size string
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Checks if the file is an image.
   * @param fileType File extension
   * @returns true if image
   */
  isImageFile(fileType: string): boolean {
    return ['png', 'jpg', 'jpeg'].includes(fileType.toLowerCase());
  }

  /**
   * Checks if the file is a PDF.
   * @param fileType File extension
   * @returns true if PDF
   */
  isPdfFile(fileType: string): boolean {
    return fileType.toLowerCase() === 'pdf';
  }

  /**
   * Checks if the file is a text file (.txt or .loc).
   * @param fileType File extension
   * @returns true if text file
   */
  isTextFile(fileType: string): boolean {
    return ['txt', 'loc'].includes(fileType.toLowerCase());
  }
}
