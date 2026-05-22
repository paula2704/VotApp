import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SurveyService } from '../../services/survey.service';
import { AuthService } from '../../services/auth.service';
import { SurveyResponse } from '../../models/survey.model';
 import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-surveys',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './surveys.component.html'
})
export class SurveysComponent implements OnInit, OnDestroy {
  surveys: SurveyResponse[] = [];
  loading = true;
  error = '';
  votingId: number | null = null;
  successMessage = '';
  private interval: any;


 constructor(
    private surveyService: SurveyService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadSurveys();
    // polling cada 5 segundos — actualiza los resultados automáticamente
    // esto es lo que el enunciado pide: dashboard que se actualiza solo
    this.interval = setInterval(() => this.loadSurveys(), 5000);
  }

  ngOnDestroy() {
    // limpia el interval cuando el componente se destruye
    // evita memory leaks
    if (this.interval) clearInterval(this.interval);
  }

  loadSurveys() {
  this.surveyService.getActive().subscribe({
    next: (data) => {
      this.surveys = data;
      this.loading = false;
      this.cdr.detectChanges(); // fuerza la detección de cambios
    },
    error: () => {
      this.error = 'Error al cargar las encuestas';
      this.loading = false;
      this.cdr.detectChanges();
    }
  });
}

  vote(surveyId: number, optionId: number) {
    this.votingId = surveyId;
    this.surveyService.vote(surveyId, { optionId }).subscribe({
      next: () => {
        this.successMessage = '¡Voto registrado!';
        this.votingId = null;
        this.loadSurveys(); // recarga inmediata después de votar
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.error = 'Ya votaste en esta encuesta o hubo un error';
        this.votingId = null;
        setTimeout(() => this.error = '', 3000);
      }
    });
  }

  // calcula el porcentaje de votos de una opción
  getPercentage(votes: number, survey: SurveyResponse): number {
    const total = survey.options.reduce((sum, opt) => sum + opt.votes, 0);
    if (total === 0) return 0;
    return Math.round((votes / total) * 100);
  }

  getTotalVotes(survey: SurveyResponse): number {
    return survey.options.reduce((sum, opt) => sum + opt.votes, 0);
  }

  logout() {
    this.authService.logout();
  }
}