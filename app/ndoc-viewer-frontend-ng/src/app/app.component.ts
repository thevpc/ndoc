import { Component } from '@angular/core';
import { GitService } from './services/git.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent2 {
  repoUrl: string = '';
  selectedFilePath: string = '';
  fileList: string[] = [];
  pageNumber: number = 1;

  constructor(private gitService: GitService) { }

  cloneRepo(): void {
    this.gitService.cloneRepository(this.repoUrl).subscribe(
      (files: string[]) => {
        this.fileList = files;
      },
      error => console.error('Error cloning repository:', error)
    );
  }

  onFileSelect(file: string): void {
    this.selectedFilePath = file;
    this.pageNumber = 1;
  }
}
