import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';

import { Auth } from '../../../../services/auth';

interface AuditEvent {
  id: number;
  eventType: string;
  entityType: string;
  entityId: number;
  userId: number;
  userEmail: string | null;
  userRole: string | null;
  oldValue: string | null;
  newValue: string | null;
  details: string | null;
  ipAddress: string | null;
  timestamp: string;
}

@Component({
  selector: 'app-soa-monitor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './soa-monitor.html',
  styleUrl: './soa-monitor.css',
})
export class SoaMonitor implements OnInit, OnDestroy {
  private readonly apiUrl = 'http://localhost:8080/api/audit';

  audits: AuditEvent[] = [];
  filteredAudits: AuditEvent[] = [];

  loading = false;
  errorMessage = '';
  lastUpdate: Date | null = null;

  eventTypeFilter = '';
  entityTypeFilter = '';
  searchText = '';

  private refreshSubscription?: Subscription;

  constructor(
    private readonly http: HttpClient,
    private readonly auth: Auth,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.loadAudits();

    this.refreshSubscription = interval(15000).subscribe(() => {
      this.loadAudits(false);
    });
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  loadAudits(showLoading = true): void {
    if (showLoading) {
      this.loading = true;
    }

    this.errorMessage = '';

    this.http.post<AuditEvent[]>(`${this.apiUrl}/filters`, {}).subscribe({
      next: (response: AuditEvent[]) => {
        this.audits = [...response].sort(
          (a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
        );

        this.applyFilters();

        this.lastUpdate = new Date();
        this.loading = false;
      },

      error: (error: unknown) => {
        console.error('Error cargando auditoría:', error);

        this.errorMessage = 'No se pudo conectar con Audit Service.';

        this.loading = false;
      },
    });
  }

  applyFilters(): void {
    const search = this.searchText.trim().toLowerCase();

    this.filteredAudits = this.audits.filter((audit) => {
      const matchesEvent = !this.eventTypeFilter || audit.eventType === this.eventTypeFilter;

      const matchesEntity = !this.entityTypeFilter || audit.entityType === this.entityTypeFilter;

      const searchableText = [
        audit.id,
        audit.eventType,
        audit.entityType,
        audit.entityId,
        audit.userId,
        audit.userEmail,
        audit.userRole,
        audit.details,
        audit.ipAddress,
      ]
        .filter((value) => value !== null && value !== undefined)
        .join(' ')
        .toLowerCase();

      return matchesEvent && matchesEntity && (!search || searchableText.includes(search));
    });
  }

  clearFilters(): void {
    this.eventTypeFilter = '';
    this.entityTypeFilter = '';
    this.searchText = '';

    this.applyFilters();
  }

  logout(): void {
    this.auth.clearSession();

    this.router.navigate(['/login']);
  }

  get totalEvents(): number {
    return this.audits.length;
  }

  get eventsToday(): number {
    const today = new Date();

    return this.audits.filter((audit) => {
      const date = new Date(audit.timestamp);

      return (
        date.getFullYear() === today.getFullYear() &&
        date.getMonth() === today.getMonth() &&
        date.getDate() === today.getDate()
      );
    }).length;
  }

  get uniqueUsers(): number {
    return new Set(
      this.audits
        .map((audit) => audit.userId)
        .filter((userId) => userId !== null && userId !== undefined),
    ).size;
  }

  get uniqueEntities(): number {
    return new Set(this.audits.map((audit) => audit.entityType).filter(Boolean)).size;
  }

  get eventTypes(): string[] {
    return [...new Set(this.audits.map((audit) => audit.eventType).filter(Boolean))].sort();
  }

  get entityTypes(): string[] {
    return [...new Set(this.audits.map((audit) => audit.entityType).filter(Boolean))].sort();
  }

  getEventClass(eventType: string): string {
    const event = eventType?.toUpperCase() ?? '';

    if (
      event.includes('DELETE') ||
      event.includes('FAILED') ||
      event.includes('ERROR') ||
      event.includes('CANCEL')
    ) {
      return 'danger';
    }

    if (
      event.includes('CREATE') ||
      event.includes('REGISTER') ||
      event.includes('COMPLETE') ||
      event.includes('SUCCESS')
    ) {
      return 'success';
    }

    if (event.includes('UPDATE') || event.includes('ASSIGN') || event.includes('PAYMENT')) {
      return 'warning';
    }

    return 'info';
  }

  formatDate(timestamp: string): string {
    if (!timestamp) {
      return 'Sin fecha';
    }

    return new Date(timestamp).toLocaleString('es-PE');
  }
}
