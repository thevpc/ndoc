import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { GitService } from '../../services/git.service';

@Component({
  selector: 'app-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnChanges {
  @Input() localPath: string = '';
  fileContent: string = '';

  constructor(private gitService: GitService) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['localPath'] && this.localPath) {
      this.loadFileContent();
    }
  }

  private loadFileContent(): void {
    this.gitService.getFileContent(this.localPath).subscribe(
      (blob: Blob) => {
        const reader = new FileReader();
        reader.onload = () => {
          this.fileContent = reader.result as string;
        };
        reader.readAsText(blob);
      },
      error => console.error('Error fetching file content:', error)
    );
  }
}
