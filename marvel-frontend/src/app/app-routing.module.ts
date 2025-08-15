import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { CharacterListComponent } from './components/character-list/character-list.component';
import { ApiLogsComponent } from './components/api-logs/api-logs.component';

const routes: Routes = [
  { 
    path: 'login', 
    component: LoginComponent 
  },
  { 
    path: 'characters', 
    component: CharacterListComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'api-logs', 
    component: ApiLogsComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: '', 
    redirectTo: '/characters', 
    pathMatch: 'full' 
  },
  { 
    path: '**', 
    redirectTo: '/characters' 
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
