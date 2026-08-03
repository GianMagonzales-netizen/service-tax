import { Component } from '@angular/core';
import { SoaMonitor } from '../components/admin/soa-monitor/soa-monitor';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [SoaMonitor],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {}
