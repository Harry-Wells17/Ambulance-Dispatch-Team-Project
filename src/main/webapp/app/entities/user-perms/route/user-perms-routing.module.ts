import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserPermsComponent } from '../list/user-perms.component';
import { UserPermsDetailComponent } from '../detail/user-perms-detail.component';
import { UserPermsUpdateComponent } from '../update/user-perms-update.component';
import { UserPermsRoutingResolveService } from './user-perms-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userPermsRoute: Routes = [
  {
    path: '',
    component: UserPermsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserPermsDetailComponent,
    resolve: {
      userPerms: UserPermsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserPermsUpdateComponent,
    resolve: {
      userPerms: UserPermsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserPermsUpdateComponent,
    resolve: {
      userPerms: UserPermsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userPermsRoute)],
  exports: [RouterModule],
})
export class UserPermsRoutingModule {}
