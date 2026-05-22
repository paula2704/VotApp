import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SurveyService } from '../../services/survey.service';
import { AuthService } from '../../services/auth.service';
import { SurveyResponse, SurveyRequest } from '../../models/survey.model';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { Chart, ArcElement, Tooltip, Legend, DoughnutController, BarController, CategoryScale, LinearScale, BarElement } from 'chart.js';
import { ThemeService } from '../../services/theme.service';

Chart.register(ArcElement, Tooltip, Legend, DoughnutController, BarController, CategoryScale, LinearScale, BarElement);

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
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

  get totalSurveys() { return this.surveys.length; }
  get activeSurveys() { return this.surveys.filter(s => s.active).length; }
  get totalVotes() { return this.surveys.reduce((sum, s) => sum + this.getTotalVotes(s), 0); }

  get donutData(): ChartData<'doughnut'> {
    return {
      labels: ['Activas', 'Inactivas'],
      datasets: [{
        data: [this.activeSurveys, this.totalSurveys - this.activeSurveys],
        backgroundColor: ['#B51A2B', '#384358'],
        borderColor: 'transparent',
        hoverOffset: 8
      }]
    };
  }

  donutOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { color: 'rgba(255,255,255,0.6)', padding: 16, font: { size: 12 } }
      }
    },
    cutout: '70%'
  };

  get barData(): ChartData<'bar'> {
    return {
      labels: this.surveys.map(s => s.question.length > 20 ? s.question.substring(0, 20) + '...' : s.question),
      datasets: [{
        label: 'Votos',
        data: this.surveys.map(s => this.getTotalVotes(s)),
        backgroundColor: this.surveys.map((_, i) =>
          i % 2 === 0 ? 'rgba(181,26,43,0.8)' : 'rgba(255,165,134,0.8)'
        ),
        borderRadius: 8,
        borderSkipped: false
      }]
    };
  }

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: {
      x: {
        ticks: { color: 'rgba(255,255,255,0.5)', font: { size: 11 } },
        grid: { color: 'rgba(255,255,255,0.05)' }
      },
      y: {
        ticks: { color: 'rgba(255,255,255,0.5)', font: { size: 11 } },
        grid: { color: 'rgba(255,255,255,0.05)' }
      }
    }
  };

 constructor(
  private surveyService: SurveyService,
  private authService: AuthService,
  private cdr: ChangeDetectorRef,
  public themeService: ThemeService
) {}

  ngOnInit() {
    this.loadSurveys();
    this.interval = setInterval(() => this.loadSurveys(), 5000);
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

  trackByIndex(index: number): number { return index; }

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