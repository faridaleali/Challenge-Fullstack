export interface User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface RegisterResponse {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  message: string;
}