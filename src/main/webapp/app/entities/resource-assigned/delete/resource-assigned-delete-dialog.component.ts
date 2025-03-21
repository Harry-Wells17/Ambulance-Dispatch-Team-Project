import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IResourceAssigned } from '../resource-assigned.model';
import { ResourceAssignedService } from '../service/resource-assigned.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './resource-assigned-delete-dialog.component.html',
})
export class ResourceAssignedDeleteDialogComponent {
  resourceAssigned?: IResourceAssigned;

  constructor(protected resourceAssignedService: ResourceAssignedService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.resourceAssignedService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
