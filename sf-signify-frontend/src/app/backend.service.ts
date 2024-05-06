// backend.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  private backendUrl = 'http://52.207.231.75:8080/hello'; // Backend endpoint URL
//  private backendUrl = 'http://localhost:8080/hello'; 

  constructor(private http: HttpClient) { }

  fetchDataFromBackend() {
    return this.http.get(this.backendUrl);
  }
}

