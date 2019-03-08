import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BookHttpService {

  readonly URL_HOMEWORK_API = 'http://localhost:8080/api/';

  constructor(private http: HttpClient) { }

  getBooksByCategory(category: string): Observable<any> {
    return this.http.get(this.URL_HOMEWORK_API + 'category/' + category + '/books');
  }
}
