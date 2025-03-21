import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ResourceBreaksComponent } from '../list/resource-breaks.component';
import { ResourceBreaksDetailComponent } from '../detail/resource-breaks-detail.component';
import { ResourceBreaksUpdateComponent } from '../update/resource-breaks-update.component';
import { ResourceBreaksRoutingResolveService } from './resource-breaks-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const resourceBreaksRoute: Routes = [
  {
    path: '',
    component: ResourceBreaksComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ResourceBreaksDetailComponent,
    resolve: {
      resourceBreaks: ResourceBreaksRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ResourceBreaksUpdateComponent,
    resolve: {
      resourceBreaks: ResourceBreaksRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ResourceBreaksUpdateComponent,
    resolve: {
      resourceBreaks: ResourceBreaksRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(resourceBreaksRoute)],
  exports: [RouterModule],
})
export class ResourceBreaksRoutingModule {}
