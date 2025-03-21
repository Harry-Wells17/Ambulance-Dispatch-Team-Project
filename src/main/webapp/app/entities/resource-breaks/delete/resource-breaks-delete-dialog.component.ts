import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IResourceBreaks } from '../resource-breaks.model';
import { ResourceBreaksService } from '../service/resource-breaks.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './resource-breaks-delete-dialog.component.html',
})
export class ResourceBreaksDeleteDialogComponent {
  resourceBreaks?: IResourceBreaks;

  constructor(protected resourceBreaksService: ResourceBreaksService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.resourceBreaksService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
