import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GitService {

  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  cloneRepository(url: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/repo?url=${encodeURIComponent(url)}`);
  }

  getFileContent(localPath: string): Observable<Blob> {
    console.log(`Requesting file content from: ${this.apiUrl}/file/content?localPath=${encodeURIComponent(localPath)}`);
    return this.http.get(`${this.apiUrl}/file/content?localPath=/home/mohamed/test02/${encodeURIComponent(localPath)}`, { responseType: 'blob' });
  }

  getDocumentImages(filePath: string, pageNumber: number = 1): Observable<Blob> {
    const url = `${this.apiUrl}/images?filePath=/home/mohamed/test02/${encodeURIComponent(filePath)}&pageNumber=${pageNumber}`;
    return this.http.get(url, { responseType: 'blob' });
  }
}
