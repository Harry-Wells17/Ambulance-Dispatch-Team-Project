import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserManagementComponent } from '../list/user-management.component';
import { UserManagementDetailComponent } from '../detail/user-management-detail.component';
import { UserManagementUpdateComponent } from '../update/user-management-update.component';
import { UserManagementRoutingResolveService } from './user-management-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userManagementRoute: Routes = [
  {
    path: '',
    component: UserManagementComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserManagementDetailComponent,
    resolve: {
      userManagement: UserManagementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserManagementUpdateComponent,
    resolve: {
      userManagement: UserManagementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserManagementUpdateComponent,
    resolve: {
      userManagement: UserManagementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userManagementRoute)],
  exports: [RouterModule],
})
export class UserManagementRoutingModule {}
