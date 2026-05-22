import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SurveyRequest, SurveyResponse, VoteRequest } from '../models/survey.model';

@Injectable({ providedIn: 'root' })
export class SurveyService {

  private apiUrl = 'http://localhost:8080/api/surveys';

  constructor(private http: HttpClient) {}

  // trae todas las encuestas (admin)
  getAll(): Observable<SurveyResponse[]> {
    return this.http.get<SurveyResponse[]>(this.apiUrl);
  }

  // trae solo encuestas activas (usuario)
  getActive(): Observable<SurveyResponse[]> {
    return this.http.get<SurveyResponse[]>(`${this.apiUrl}/active`);
  }

  // trae una encuesta por id
  getById(id: number): Observable<SurveyResponse> {
    return this.http.get<SurveyResponse>(`${this.apiUrl}/${id}`);
  }

  // crea una encuesta (solo admin)
  create(request: SurveyRequest): Observable<SurveyResponse> {
    return this.http.post<SurveyResponse>(this.apiUrl, request);
  }

  // edita una encuesta (solo admin)
  update(id: number, request: SurveyRequest): Observable<SurveyResponse> {
    return this.http.put<SurveyResponse>(`${this.apiUrl}/${id}`, request);
  }

  // elimina una encuesta (solo admin)
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // activa o desactiva una encuesta (solo admin)
  toggle(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/toggle`, {});
  }

  // vota en una encuesta
  vote(surveyId: number, request: VoteRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${surveyId}/vote`, request);
  }
}