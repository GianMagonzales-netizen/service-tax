import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-overlay.html',
  styleUrl: './loading-overlay.css',
})
export class LoadingOverlayComponent {
  steps = [
    'Creando solicitud',
    'Buscando mejor conductor',
    'Asignando vehículo',
    'Calculando distancia y tarifa',
    'Procesando pago',
    'Finalizando operación',
  ];
}
