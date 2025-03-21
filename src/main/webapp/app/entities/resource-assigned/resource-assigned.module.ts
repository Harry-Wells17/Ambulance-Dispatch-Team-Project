import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ResourceAssignedComponent } from './list/resource-assigned.component';
import { ResourceAssignedDetailComponent } from './detail/resource-assigned-detail.component';
import { ResourceAssignedUpdateComponent } from './update/resource-assigned-update.component';
import { ResourceAssignedDeleteDialogComponent } from './delete/resource-assigned-delete-dialog.component';
import { ResourceAssignedRoutingModule } from './route/resource-assigned-routing.module';

@NgModule({
  imports: [SharedModule, ResourceAssignedRoutingModule],
  declarations: [
    ResourceAssignedComponent,
    ResourceAssignedDetailComponent,
    ResourceAssignedUpdateComponent,
    ResourceAssignedDeleteDialogComponent,
  ],
})
export class ResourceAssignedModule {}
