import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil, catchError, of } from 'rxjs';
import { ApiLogService } from '../../services/api-log.service';
import { AuthService } from '../../services/auth.service';
import { ApiCallLogResponse } from '../../models/api-log.model';

@Component({
  selector: 'app-api-logs',
  templateUrl: './api-logs.component.html',
  styleUrls: ['./api-logs.component.scss']
})
export class ApiLogsComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['timestamp', 'userName', 'httpMethod', 'endpoint', 'requestParams', 'responseTime'];
  dataSource = new MatTableDataSource<ApiCallLogResponse>();
  loading = false;
  totalElements = 0;
  currentPage = 0;
  pageSize = 10;
  pageSizeOptions = [5, 10, 25, 50];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private destroy$ = new Subject<void>();

  constructor(
    private apiLogService: ApiLogService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadApiLogs();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadApiLogs(page: number = 0, size: number = 10): void {
    this.loading = true;
    
    this.apiLogService.getApiLogs(page, size)
      .pipe(
        takeUntil(this.destroy$),
        catchError(error => {
          this.handleError('Error loading API logs', error);
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: size,
            number: page
          });
        })
      )
      .subscribe(response => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.loading = false;
      });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadApiLogs(this.currentPage, this.pageSize);
  }

  refresh(): void {
    this.loadApiLogs(this.currentPage, this.pageSize);
  }

  formatTimestamp(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleString();
  }

  formatResponseTime(responseTime: number): string {
    return `${responseTime}ms`;
  }

  getMethodClass(method: string): string {
    switch (method.toUpperCase()) {
      case 'GET':
        return 'method-get';
      case 'POST':
        return 'method-post';
      case 'PUT':
        return 'method-put';
      case 'DELETE':
        return 'method-delete';
      default:
        return 'method-default';
    }
  }

  formatEndpoint(endpoint: string): string {
    // Remove the base URL if present
    return endpoint.replace(/^https?:\/\/[^\/]+/, '');
  }

  formatRequestParams(params: string): string {
    if (!params || params.trim() === '' || params.trim() === '{}') {
      return 'None';
    }
    
    try {
      const parsed = JSON.parse(params);
      return JSON.stringify(parsed, null, 2);
    } catch {
      return params;
    }
  }

  private handleError(message: string, error: any): void {
    console.error(message, error);
    let errorMessage = message;
    
    if (error.status === 0) {
      errorMessage = 'Unable to connect to the server. Please check your connection.';
    } else if (error.status === 401) {
      errorMessage = 'Authentication failed. Please log in again.';
    } else if (error.error?.message) {
      errorMessage = error.error.message;
    }

    this.snackBar.open(errorMessage, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}