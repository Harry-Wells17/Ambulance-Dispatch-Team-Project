import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserExistComponent } from '../list/user-exist.component';
import { UserExistDetailComponent } from '../detail/user-exist-detail.component';
import { UserExistUpdateComponent } from '../update/user-exist-update.component';
import { UserExistRoutingResolveService } from './user-exist-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userExistRoute: Routes = [
  {
    path: '',
    component: UserExistComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserExistDetailComponent,
    resolve: {
      userExist: UserExistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserExistUpdateComponent,
    resolve: {
      userExist: UserExistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserExistUpdateComponent,
    resolve: {
      userExist: UserExistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userExistRoute)],
  exports: [RouterModule],
})
export class UserExistRoutingModule {}
