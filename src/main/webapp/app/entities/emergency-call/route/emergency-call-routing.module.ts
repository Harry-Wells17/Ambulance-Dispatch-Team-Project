import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EmergencyCallComponent } from '../list/emergency-call.component';
import { EmergencyCallDetailComponent } from '../detail/emergency-call-detail.component';
import { EmergencyCallUpdateComponent } from '../update/emergency-call-update.component';
import { EmergencyCallRoutingResolveService } from './emergency-call-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const emergencyCallRoute: Routes = [
  {
    path: '',
    component: EmergencyCallComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EmergencyCallDetailComponent,
    resolve: {
      emergencyCall: EmergencyCallRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EmergencyCallUpdateComponent,
    resolve: {
      emergencyCall: EmergencyCallRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EmergencyCallUpdateComponent,
    resolve: {
      emergencyCall: EmergencyCallRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(emergencyCallRoute)],
  exports: [RouterModule],
})
export class EmergencyCallRoutingModule {}
