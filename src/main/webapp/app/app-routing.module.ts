import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/config/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NewPage } from './test.component';
import { EventPage } from './manage/events/events.component';
import { MainComponent } from './layouts/main/main.component';
import { MainEventsComponent } from './manage/main/main.component';
import { CrewPage } from './crew/crewPage.component';
import { GDPComponent } from './legal/gdpr/gdpr.component';
import { MapViewComponent } from './map-view/map-view.component';
import { Ambulance } from './manage/ambulance/ambulance.component';
import { AmbulanceDoors } from './manage/doors/ambulanceDoors.component';
import { UserLogsComponent } from './user-logs/userlogs.component';
import { LargeMapComponent } from './manage/largePages/largeMap/largeMap.component';
import { LargeResourceViewComponent } from './manage/largePages/resourceView/resourceView.component';
import { LargeCallsComponent } from './manage/largePages/largecalls/largeCalls.component';
import { MobileSim } from './app-sim/main.component';
import { LargeBreaksViewComponent } from './manage/largePages/largeBreaks/breaksView.component';
import { userManagement } from './user-management/userManagement.component';

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'testpage',
          component: NewPage,
        },
        {
          path: 'ambulance',
          component: Ambulance,
        },
        {
          path: 'ambulancetest',
          component: AmbulanceDoors,
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'login',
          loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
        },
        {
          path: 'events',
          component: EventPage,
        },
        {
          path: 'events/:id',
          component: MainEventsComponent,
        },
        {
          path: 'events/:id/large-map',
          component: LargeMapComponent,
        },
        {
          path: 'events/:id/large-resource-view',
          component: LargeResourceViewComponent,
        },
        {
          path: 'events/:id/large-calls-view',
          component: LargeCallsComponent,
        },
        {
          path: 'events/:id/large-breaks-view',
          component: LargeBreaksViewComponent,
        },
        {
          path: 'mobile-sim',
          component: MobileSim,
        },
        {
          path: 'user-management',
          component: userManagement,
        },
        {
          path: 'crew',
          component: CrewPage,
        },
        {
          path: 'gdpr',
          component: GDPComponent,
        },
        {
          path: 'map-view',
          component: MapViewComponent,
        },
        {
          path: 'logs-old',
          component: UserLogsComponent,
        },

        {
          path: '',
          loadChildren: () => import(`./entities/entity-routing.module`).then(m => m.EntityRoutingModule),
        },

        navbarRoute,
        ...errorRoute,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
