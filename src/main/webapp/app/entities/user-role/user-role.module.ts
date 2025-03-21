import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserRoleComponent } from './list/user-role.component';
import { UserRoleDetailComponent } from './detail/user-role-detail.component';
import { UserRoleUpdateComponent } from './update/user-role-update.component';
import { UserRoleDeleteDialogComponent } from './delete/user-role-delete-dialog.component';
import { UserRoleRoutingModule } from './route/user-role-routing.module';

@NgModule({
  imports: [SharedModule, UserRoleRoutingModule],
  declarations: [UserRoleComponent, UserRoleDetailComponent, UserRoleUpdateComponent, UserRoleDeleteDialogComponent],
})
export class UserRoleModule {}
