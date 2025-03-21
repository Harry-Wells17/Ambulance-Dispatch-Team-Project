import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { HomeCard } from './card/homeCard.component';
import { AmbulanceDoors } from 'app/manage/doors/ambulanceDoors.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), HomeCard, AmbulanceDoors],
  declarations: [HomeComponent],
})
export class HomeModule {}
