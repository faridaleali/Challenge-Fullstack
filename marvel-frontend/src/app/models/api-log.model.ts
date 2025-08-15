export interface ApiLog {
  id: number;
  endpoint: string;
  httpMethod: string;
  requestParams: string;
  responseTime: number;
  timestamp: Date;
  userName: string;
}

export interface ApiCallLogResponse {
  id: number;
  endpoint: string;
  httpMethod: string;
  requestParams: string;
  responseTime: number;
  timestamp: string;
  userName: string;
}