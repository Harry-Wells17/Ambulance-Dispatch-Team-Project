import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ResourceAssignedFormService, ResourceAssignedFormGroup } from './resource-assigned-form.service';
import { IResourceAssigned } from '../resource-assigned.model';
import { ResourceAssignedService } from '../service/resource-assigned.service';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';

@Component({
  selector: 'jhi-resource-assigned-update',
  templateUrl: './resource-assigned-update.component.html',
})
export class ResourceAssignedUpdateComponent implements OnInit {
  isSaving = false;
  resourceAssigned: IResourceAssigned | null = null;

  resourcesSharedCollection: IResource[] = [];
  emergencyCallsSharedCollection: IEmergencyCall[] = [];

  editForm: ResourceAssignedFormGroup = this.resourceAssignedFormService.createResourceAssignedFormGroup();

  constructor(
    protected resourceAssignedService: ResourceAssignedService,
    protected resourceAssignedFormService: ResourceAssignedFormService,
    protected resourceService: ResourceService,
    protected emergencyCallService: EmergencyCallService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareResource = (o1: IResource | null, o2: IResource | null): boolean => this.resourceService.compareResource(o1, o2);

  compareEmergencyCall = (o1: IEmergencyCall | null, o2: IEmergencyCall | null): boolean =>
    this.emergencyCallService.compareEmergencyCall(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceAssigned }) => {
      this.resourceAssigned = resourceAssigned;
      if (resourceAssigned) {
        this.updateForm(resourceAssigned);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resourceAssigned = this.resourceAssignedFormService.getResourceAssigned(this.editForm);
    if (resourceAssigned.id !== null) {
      this.subscribeToSaveResponse(this.resourceAssignedService.update(resourceAssigned));
    } else {
      this.subscribeToSaveResponse(this.resourceAssignedService.create(resourceAssigned));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResourceAssigned>>): void {
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

  protected updateForm(resourceAssigned: IResourceAssigned): void {
    this.resourceAssigned = resourceAssigned;
    this.resourceAssignedFormService.resetForm(this.editForm, resourceAssigned);

    this.resourcesSharedCollection = this.resourceService.addResourceToCollectionIfMissing<IResource>(
      this.resourcesSharedCollection,
      resourceAssigned.resource
    );
    this.emergencyCallsSharedCollection = this.emergencyCallService.addEmergencyCallToCollectionIfMissing<IEmergencyCall>(
      this.emergencyCallsSharedCollection,
      resourceAssigned.emergencyCall
    );
  }

  protected loadRelationshipsOptions(): void {
    this.resourceService
      .query()
      .pipe(map((res: HttpResponse<IResource[]>) => res.body ?? []))
      .pipe(
        map((resources: IResource[]) =>
          this.resourceService.addResourceToCollectionIfMissing<IResource>(resources, this.resourceAssigned?.resource)
        )
      )
      .subscribe((resources: IResource[]) => (this.resourcesSharedCollection = resources));

    this.emergencyCallService
      .query()
      .pipe(map((res: HttpResponse<IEmergencyCall[]>) => res.body ?? []))
      .pipe(
        map((emergencyCalls: IEmergencyCall[]) =>
          this.emergencyCallService.addEmergencyCallToCollectionIfMissing<IEmergencyCall>(
            emergencyCalls,
            this.resourceAssigned?.emergencyCall
          )
        )
      )
      .subscribe((emergencyCalls: IEmergencyCall[]) => (this.emergencyCallsSharedCollection = emergencyCalls));
  }
}
