import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { CharacterResponse } from '../models/character.model';

@Injectable({
  providedIn: 'root'
})
export class CharacterService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getCharacters(nameStartsWith?: string, limit: number = 20, offset: number = 0): Observable<CharacterResponse[]> {
    let params = new HttpParams()
      .set('limit', limit.toString())
      .set('offset', offset.toString());

    if (nameStartsWith && nameStartsWith.trim()) {
      params = params.set('nameStartsWith', nameStartsWith.trim());
    }

    return this.http.get<any>(`${this.apiUrl}/api/characters`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  getCharacterById(id: number): Observable<CharacterResponse> {
    return this.http.get<CharacterResponse>(`${this.apiUrl}/api/characters/${id}`);
  }

  searchCharacters(query: string, limit: number = 20): Observable<CharacterResponse[]> {
    const params = new HttpParams()
      .set('nameStartsWith', query)
      .set('limit', limit.toString());

    return this.http.get<any>(`${this.apiUrl}/api/characters`, { params }).pipe(
      map(response => response.data || [])
    );
  }
}