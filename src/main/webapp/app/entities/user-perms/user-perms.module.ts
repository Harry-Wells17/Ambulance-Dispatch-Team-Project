import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserPermsComponent } from './list/user-perms.component';
import { UserPermsDetailComponent } from './detail/user-perms-detail.component';
import { UserPermsUpdateComponent } from './update/user-perms-update.component';
import { UserPermsDeleteDialogComponent } from './delete/user-perms-delete-dialog.component';
import { UserPermsRoutingModule } from './route/user-perms-routing.module';

@NgModule({
  imports: [SharedModule, UserPermsRoutingModule],
  declarations: [UserPermsComponent, UserPermsDetailComponent, UserPermsUpdateComponent, UserPermsDeleteDialogComponent],
})
export class UserPermsModule {}
