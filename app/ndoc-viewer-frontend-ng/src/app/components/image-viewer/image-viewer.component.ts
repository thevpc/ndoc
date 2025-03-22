import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { GitService } from '../../services/git.service';

@Component({
  selector: 'app-image-viewer',
  templateUrl: './image-viewer.component.html',
  styleUrls: ['./image-viewer.component.scss']
})
export class ImageViewerComponent implements OnChanges {
  @Input() filePath: string = '';
  @Input() pageNumber: number = 1;

  imageUrl: string = '';
  isPresentationMode: boolean = false;

  constructor(private gitService: GitService) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['filePath'] && this.filePath) {
      this.loadImage();
    }
  }

  private loadImage(): void {
    this.gitService.getDocumentImages(this.filePath, this.pageNumber).subscribe(
      (blob: Blob) => {
        const objectURL = URL.createObjectURL(blob);
        this.imageUrl = objectURL;
      },
      error => console.error('Error fetching image:', error)
    );
  }

  previousPage(): void {
    if (this.pageNumber > 1) {
      this.pageNumber--;
      this.loadImage();
    }
  }

  nextPage(): void {
    this.pageNumber++;
    this.loadImage();
  }

  togglePresentationMode(): void {
    this.isPresentationMode = !this.isPresentationMode;
  }
}
