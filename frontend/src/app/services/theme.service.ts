import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {

  private isDark = true;

  constructor() {
    // lee el tema guardado en localStorage
    const saved = localStorage.getItem('theme');
    if (saved) {
      this.isDark = saved === 'dark';
      this.applyTheme();
    }
  }

  toggle() {
    this.isDark = !this.isDark;
    localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
    this.applyTheme();
  }

  get darkMode() { return this.isDark; }

  private applyTheme() {
    document.documentElement.setAttribute(
      'data-theme',
      this.isDark ? 'dark' : 'light'
    );
  }
}