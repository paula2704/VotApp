import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  username = '';
  password = '';
  confirmPassword = '';
  role: 'USER' | 'ADMIN' = 'USER';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.username || !this.password || !this.confirmPassword) {
      this.error = 'Completa todos los campos';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }

    if (this.password.length < 6) {
      this.error = 'La contraseña debe tener mínimo 6 caracteres';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.register({ username: this.username, password: this.password }, this.role)
      .subscribe({
        next: (response) => {
          if (response.role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/surveys']);
          }
        },
        error: () => {
          this.error = 'El usuario ya existe';
          this.loading = false;
        }
      });
  }
}