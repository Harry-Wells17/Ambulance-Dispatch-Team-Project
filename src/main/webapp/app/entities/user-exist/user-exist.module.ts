import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserExistComponent } from './list/user-exist.component';
import { UserExistDetailComponent } from './detail/user-exist-detail.component';
import { UserExistUpdateComponent } from './update/user-exist-update.component';
import { UserExistDeleteDialogComponent } from './delete/user-exist-delete-dialog.component';
import { UserExistRoutingModule } from './route/user-exist-routing.module';

@NgModule({
  imports: [SharedModule, UserExistRoutingModule],
  declarations: [UserExistComponent, UserExistDetailComponent, UserExistUpdateComponent, UserExistDeleteDialogComponent],
})
export class UserExistModule {}
