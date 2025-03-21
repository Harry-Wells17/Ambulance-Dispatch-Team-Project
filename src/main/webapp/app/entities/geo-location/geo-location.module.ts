import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GeoLocationComponent } from './list/geo-location.component';
import { GeoLocationDetailComponent } from './detail/geo-location-detail.component';
import { GeoLocationUpdateComponent } from './update/geo-location-update.component';
import { GeoLocationDeleteDialogComponent } from './delete/geo-location-delete-dialog.component';
import { GeoLocationRoutingModule } from './route/geo-location-routing.module';

@NgModule({
  imports: [SharedModule, GeoLocationRoutingModule],
  declarations: [GeoLocationComponent, GeoLocationDetailComponent, GeoLocationUpdateComponent, GeoLocationDeleteDialogComponent],
})
export class GeoLocationModule {}
