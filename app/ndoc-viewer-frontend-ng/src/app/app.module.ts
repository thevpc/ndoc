import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent2} from './app.component';
import { RepositoryContentComponent } from './components/repository-content/repository-content.component';
import { ImageViewerComponent } from './components/image-viewer/image-viewer.component';
import { FileViewerComponent } from './components/file-viewer/file-viewer.component';

@NgModule({
  declarations: [
    AppComponent2,
    RepositoryContentComponent,
    ImageViewerComponent,
    FileViewerComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent2]
})
export class AppModule { }
