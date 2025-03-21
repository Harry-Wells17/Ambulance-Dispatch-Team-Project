import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SystemLogComponent } from '../list/system-log.component';
import { SystemLogDetailComponent } from '../detail/system-log-detail.component';
import { SystemLogUpdateComponent } from '../update/system-log-update.component';
import { SystemLogRoutingResolveService } from './system-log-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const systemLogRoute: Routes = [
  {
    path: '',
    component: SystemLogComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SystemLogDetailComponent,
    resolve: {
      systemLog: SystemLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SystemLogUpdateComponent,
    resolve: {
      systemLog: SystemLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SystemLogUpdateComponent,
    resolve: {
      systemLog: SystemLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(systemLogRoute)],
  exports: [RouterModule],
})
export class SystemLogRoutingModule {}
