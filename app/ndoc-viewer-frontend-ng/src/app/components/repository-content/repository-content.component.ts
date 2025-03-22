import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { GitService } from '../../services/git.service';

@Component({
  selector: 'app-repository-content',
  templateUrl: './repository-content.component.html',
  styleUrls: ['./repository-content.component.scss']
})
export class RepositoryContentComponent implements OnInit {
  @Output() fileSelected = new EventEmitter<string>();
  contents: string[] = [];
  repoUrl: string = '';

  constructor(private gitService: GitService) { }

  ngOnInit(): void {
  }

  cloneRepository(): void {
    this.gitService.cloneRepository(this.repoUrl).subscribe(contents => {
      console.log(contents);
      this.contents = contents;
    }, error => {
      console.error('Error cloning repository:', error);
    });
  }

  selectFile(file: string): void {
    this.fileSelected.emit(file);
  }
}
