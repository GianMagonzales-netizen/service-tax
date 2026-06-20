import { Routes } from '@angular/router';

import { Login } from './views/login/login';
import { Register } from './views/register/register';
import { Dashboard } from './views/dashboard/dashboard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },

  {
    path: 'login',
    component: Login,
  },

  {
    path: 'register',
    component: Register,
  },

  {
    path: 'dashboard',
    component: Dashboard,
  },
];
