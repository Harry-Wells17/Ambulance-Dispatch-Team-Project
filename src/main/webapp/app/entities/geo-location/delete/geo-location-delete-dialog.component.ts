import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGeoLocation } from '../geo-location.model';
import { GeoLocationService } from '../service/geo-location.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './geo-location-delete-dialog.component.html',
})
export class GeoLocationDeleteDialogComponent {
  geoLocation?: IGeoLocation;

  constructor(protected geoLocationService: GeoLocationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.geoLocationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
