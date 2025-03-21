import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GeoLocationComponent } from '../list/geo-location.component';
import { GeoLocationDetailComponent } from '../detail/geo-location-detail.component';
import { GeoLocationUpdateComponent } from '../update/geo-location-update.component';
import { GeoLocationRoutingResolveService } from './geo-location-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const geoLocationRoute: Routes = [
  {
    path: '',
    component: GeoLocationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GeoLocationDetailComponent,
    resolve: {
      geoLocation: GeoLocationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GeoLocationUpdateComponent,
    resolve: {
      geoLocation: GeoLocationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GeoLocationUpdateComponent,
    resolve: {
      geoLocation: GeoLocationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(geoLocationRoute)],
  exports: [RouterModule],
})
export class GeoLocationRoutingModule {}
