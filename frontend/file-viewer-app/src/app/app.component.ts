import { Component } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FileListComponent } from './components/file-list/file-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, FileListComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {}

