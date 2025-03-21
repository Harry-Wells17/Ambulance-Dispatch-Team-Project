import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserPerms } from '../user-perms.model';
import { UserPermsService } from '../service/user-perms.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './user-perms-delete-dialog.component.html',
})
export class UserPermsDeleteDialogComponent {
  userPerms?: IUserPerms;

  constructor(protected userPermsService: UserPermsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userPermsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
