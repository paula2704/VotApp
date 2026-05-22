import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SurveyService } from '../../services/survey.service';
import { AuthService } from '../../services/auth.service';
import { SurveyResponse, SurveyRequest } from '../../models/survey.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit, OnDestroy {
  surveys: SurveyResponse[] = [];
  loading = true;
  error = '';
  successMessage = '';
  private interval: any;

  showForm = false;
  editingId: number | null = null;
  question = '';
  options: string[] = ['', ''];

  constructor(
    private surveyService: SurveyService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadSurveys();
    this.interval = setInterval(() => this.loadSurveys(), 5000);
  }

  trackByIndex(index: number): number {
  return index;
}
  ngOnDestroy() {
    if (this.interval) clearInterval(this.interval);
  }

  loadSurveys() {
    this.surveyService.getAll().subscribe({
      next: (data) => {
        this.surveys = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Error al cargar las encuestas';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openCreate() {
    this.editingId = null;
    this.question = '';
    this.options = ['', ''];
    this.showForm = true;
  }

  openEdit(survey: SurveyResponse) {
    this.editingId = survey.id;
    this.question = survey.question;
    this.options = survey.options.map(o => o.text);
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingId = null;
    this.question = '';
    this.options = ['', ''];
  }

  addOption() {
    if (this.options.length < 6) this.options.push('');
  }

  removeOption(index: number) {
    if (this.options.length > 2) this.options.splice(index, 1);
  }

  onSubmit() {
    const validOptions = this.options.filter(o => o.trim() !== '');
    if (!this.question.trim()) { this.error = 'La pregunta es obligatoria'; return; }
    if (validOptions.length < 2) { this.error = 'Agrega mínimo 2 opciones'; return; }

    const request: SurveyRequest = { question: this.question.trim(), options: validOptions };

    if (this.editingId) {
      this.surveyService.update(this.editingId, request).subscribe({
        next: () => { this.successMessage = 'Encuesta actualizada'; this.closeForm(); this.loadSurveys(); setTimeout(() => this.successMessage = '', 3000); },
        error: () => this.error = 'Error al actualizar'
      });
    } else {
      this.surveyService.create(request).subscribe({
        next: () => { this.successMessage = 'Encuesta creada'; this.closeForm(); this.loadSurveys(); setTimeout(() => this.successMessage = '', 3000); },
        error: () => this.error = 'Error al crear'
      });
    }
  }

  delete(id: number) {
    if (confirm('¿Eliminar esta encuesta?')) {
      this.surveyService.delete(id).subscribe({
        next: () => { this.successMessage = 'Encuesta eliminada'; this.loadSurveys(); setTimeout(() => this.successMessage = '', 3000); },
        error: () => this.error = 'Error al eliminar'
      });
    }
  }

  toggle(id: number) {
    this.surveyService.toggle(id).subscribe({
      next: () => this.loadSurveys(),
      error: () => this.error = 'Error al cambiar estado'
    });
  }

  getTotalVotes(survey: SurveyResponse): number {
    return survey.options.reduce((sum, opt) => sum + opt.votes, 0);
  }

  getPercentage(votes: number, survey: SurveyResponse): number {
    const total = this.getTotalVotes(survey);
    if (total === 0) return 0;
    return Math.round((votes / total) * 100);
  }

  logout() { this.authService.logout(); }
}