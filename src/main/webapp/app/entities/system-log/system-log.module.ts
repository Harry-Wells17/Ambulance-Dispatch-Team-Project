import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SystemLogComponent } from './list/system-log.component';
import { SystemLogDetailComponent } from './detail/system-log-detail.component';
import { SystemLogUpdateComponent } from './update/system-log-update.component';
import { SystemLogDeleteDialogComponent } from './delete/system-log-delete-dialog.component';
import { SystemLogRoutingModule } from './route/system-log-routing.module';

@NgModule({
  imports: [SharedModule, SystemLogRoutingModule],
  declarations: [SystemLogComponent, SystemLogDetailComponent, SystemLogUpdateComponent, SystemLogDeleteDialogComponent],
})
export class SystemLogModule {}
