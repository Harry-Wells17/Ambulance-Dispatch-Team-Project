import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ResourceAssignedComponent } from '../list/resource-assigned.component';
import { ResourceAssignedDetailComponent } from '../detail/resource-assigned-detail.component';
import { ResourceAssignedUpdateComponent } from '../update/resource-assigned-update.component';
import { ResourceAssignedRoutingResolveService } from './resource-assigned-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const resourceAssignedRoute: Routes = [
  {
    path: '',
    component: ResourceAssignedComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ResourceAssignedDetailComponent,
    resolve: {
      resourceAssigned: ResourceAssignedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ResourceAssignedUpdateComponent,
    resolve: {
      resourceAssigned: ResourceAssignedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ResourceAssignedUpdateComponent,
    resolve: {
      resourceAssigned: ResourceAssignedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(resourceAssignedRoute)],
  exports: [RouterModule],
})
export class ResourceAssignedRoutingModule {}
