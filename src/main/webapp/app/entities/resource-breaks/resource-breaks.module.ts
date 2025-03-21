import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ResourceBreaksComponent } from './list/resource-breaks.component';
import { ResourceBreaksDetailComponent } from './detail/resource-breaks-detail.component';
import { ResourceBreaksUpdateComponent } from './update/resource-breaks-update.component';
import { ResourceBreaksDeleteDialogComponent } from './delete/resource-breaks-delete-dialog.component';
import { ResourceBreaksRoutingModule } from './route/resource-breaks-routing.module';

@NgModule({
  imports: [SharedModule, ResourceBreaksRoutingModule],
  declarations: [
    ResourceBreaksComponent,
    ResourceBreaksDetailComponent,
    ResourceBreaksUpdateComponent,
    ResourceBreaksDeleteDialogComponent,
  ],
})
export class ResourceBreaksModule {}
