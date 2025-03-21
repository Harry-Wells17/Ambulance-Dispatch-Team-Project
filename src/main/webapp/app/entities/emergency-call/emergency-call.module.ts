import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EmergencyCallComponent } from './list/emergency-call.component';
import { EmergencyCallDetailComponent } from './detail/emergency-call-detail.component';
import { EmergencyCallUpdateComponent } from './update/emergency-call-update.component';
import { EmergencyCallDeleteDialogComponent } from './delete/emergency-call-delete-dialog.component';
import { EmergencyCallRoutingModule } from './route/emergency-call-routing.module';

@NgModule({
  imports: [SharedModule, EmergencyCallRoutingModule],
  declarations: [EmergencyCallComponent, EmergencyCallDetailComponent, EmergencyCallUpdateComponent, EmergencyCallDeleteDialogComponent],
})
export class EmergencyCallModule {}
