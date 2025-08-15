import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiCallLogResponse } from '../models/api-log.model';

@Injectable({
  providedIn: 'root'
})
export class ApiLogService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getApiLogs(page: number = 0, size: number = 10): Observable<{
    content: ApiCallLogResponse[],
    totalElements: number,
    totalPages: number,
    size: number,
    number: number
  }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'timestamp,desc');

    return this.http.get<{
      content: ApiCallLogResponse[],
      totalElements: number,
      totalPages: number,
      size: number,
      number: number
    }>(`${this.apiUrl}/api/logs`, { params });
  }

  getApiLogsByUser(username: string, page: number = 0, size: number = 10): Observable<{
    content: ApiCallLogResponse[],
    totalElements: number,
    totalPages: number,
    size: number,
    number: number
  }> {
    const params = new HttpParams()
      .set('username', username)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'timestamp,desc');

    return this.http.get<{
      content: ApiCallLogResponse[],
      totalElements: number,
      totalPages: number,
      size: number,
      number: number
    }>(`${this.apiUrl}/api/logs/user`, { params });
  }
}