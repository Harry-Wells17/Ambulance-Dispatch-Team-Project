import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'system-log',
        data: { pageTitle: 'SystemLogs' },
        loadChildren: () => import('./system-log/system-log.module').then(m => m.SystemLogModule),
      },
      {
        path: 'user-perms',
        data: { pageTitle: 'UserPerms' },
        loadChildren: () => import('./user-perms/user-perms.module').then(m => m.UserPermsModule),
      },
      {
        path: 'resource-breaks',
        data: { pageTitle: 'ResourceBreaks' },
        loadChildren: () => import('./resource-breaks/resource-breaks.module').then(m => m.ResourceBreaksModule),
      },
      {
        path: 'geo-location',
        data: { pageTitle: 'GeoLocations' },
        loadChildren: () => import('./geo-location/geo-location.module').then(m => m.GeoLocationModule),
      },
      {
        path: 'resource',
        data: { pageTitle: 'Resources' },
        loadChildren: () => import('./resource/resource.module').then(m => m.ResourceModule),
      },
      {
        path: 'resource-assigned',
        data: { pageTitle: 'ResourceAssigneds' },
        loadChildren: () => import('./resource-assigned/resource-assigned.module').then(m => m.ResourceAssignedModule),
      },
      {
        path: 'emergency-call',
        data: { pageTitle: 'EmergencyCalls' },
        loadChildren: () => import('./emergency-call/emergency-call.module').then(m => m.EmergencyCallModule),
      },
      {
        path: 'event',
        data: { pageTitle: 'Events' },
        loadChildren: () => import('./event/event.module').then(m => m.EventModule),
      },
      {
        path: 'user-role',
        data: { pageTitle: 'UserRoles' },
        loadChildren: () => import('./user-role/user-role.module').then(m => m.UserRoleModule),
      },
      {
        path: 'user-management',
        data: { pageTitle: 'UserManagements' },
        loadChildren: () => import('./user-management/user-management.module').then(m => m.UserManagementModule),
      },
      {
        path: 'user-exist',
        data: { pageTitle: 'UserExists' },
        loadChildren: () => import('./user-exist/user-exist.module').then(m => m.UserExistModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
