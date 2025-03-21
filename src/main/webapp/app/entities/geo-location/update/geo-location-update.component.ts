import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { GeoLocationFormService, GeoLocationFormGroup } from './geo-location-form.service';
import { IGeoLocation } from '../geo-location.model';
import { GeoLocationService } from '../service/geo-location.service';

@Component({
  selector: 'jhi-geo-location-update',
  templateUrl: './geo-location-update.component.html',
})
export class GeoLocationUpdateComponent implements OnInit {
  isSaving = false;
  geoLocation: IGeoLocation | null = null;

  editForm: GeoLocationFormGroup = this.geoLocationFormService.createGeoLocationFormGroup();

  constructor(
    protected geoLocationService: GeoLocationService,
    protected geoLocationFormService: GeoLocationFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ geoLocation }) => {
      this.geoLocation = geoLocation;
      if (geoLocation) {
        this.updateForm(geoLocation);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const geoLocation = this.geoLocationFormService.getGeoLocation(this.editForm);
    if (geoLocation.id !== null) {
      this.subscribeToSaveResponse(this.geoLocationService.update(geoLocation));
    } else {
      this.subscribeToSaveResponse(this.geoLocationService.create(geoLocation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGeoLocation>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(geoLocation: IGeoLocation): void {
    this.geoLocation = geoLocation;
    this.geoLocationFormService.resetForm(this.editForm, geoLocation);
  }
}
