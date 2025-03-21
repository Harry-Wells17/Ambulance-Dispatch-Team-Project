import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEmergencyCall } from '../emergency-call.model';
import { EmergencyCallService } from '../service/emergency-call.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './emergency-call-delete-dialog.component.html',
})
export class EmergencyCallDeleteDialogComponent {
  emergencyCall?: IEmergencyCall;

  constructor(protected emergencyCallService: EmergencyCallService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.emergencyCallService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
