import { Component } from '@angular/core';
import { BackendService } from './backend.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Angular Frontend'; // Initialize the title property
  errorMessage: string = '';

  constructor(private backendService: BackendService) { }

  fetchDataFromBackend() {
    this.errorMessage = ''; // Clear any previous error message
    this.backendService.fetchDataFromBackend().subscribe({
      next: (data: any) => {
        console.log('Data from backend:', data);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 0) {
          this.errorMessage = 'CORS error: The request was blocked due to CORS policy.';
        } else {
          this.errorMessage = `HTTP error: ${error.message}`;
        }
      }
    });
  }
}

