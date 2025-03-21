import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserRoleComponent } from '../list/user-role.component';
import { UserRoleDetailComponent } from '../detail/user-role-detail.component';
import { UserRoleUpdateComponent } from '../update/user-role-update.component';
import { UserRoleRoutingResolveService } from './user-role-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userRoleRoute: Routes = [
  {
    path: '',
    component: UserRoleComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserRoleDetailComponent,
    resolve: {
      userRole: UserRoleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserRoleUpdateComponent,
    resolve: {
      userRole: UserRoleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserRoleUpdateComponent,
    resolve: {
      userRole: UserRoleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userRoleRoute)],
  exports: [RouterModule],
})
export class UserRoleRoutingModule {}
