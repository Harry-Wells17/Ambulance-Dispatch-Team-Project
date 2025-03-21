import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ResourceFormService, ResourceFormGroup } from './resource-form.service';
import { IResource } from '../resource.model';
import { ResourceService } from '../service/resource.service';
import { IResourceBreaks } from 'app/entities/resource-breaks/resource-breaks.model';
import { ResourceBreaksService } from 'app/entities/resource-breaks/service/resource-breaks.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { ResourceType } from 'app/entities/enumerations/resource-type.model';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-resource-update',
  templateUrl: './resource-update.component.html',
})
export class ResourceUpdateComponent implements OnInit {
  isSaving = false;
  resource: IResource | null = null;
  resourceTypeValues = Object.keys(ResourceType);
  statusValues = Object.keys(Status);

  resourceBreaksSharedCollection: IResourceBreaks[] = [];
  eventsSharedCollection: IEvent[] = [];

  editForm: ResourceFormGroup = this.resourceFormService.createResourceFormGroup();

  constructor(
    protected resourceService: ResourceService,
    protected resourceFormService: ResourceFormService,
    protected resourceBreaksService: ResourceBreaksService,
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareResourceBreaks = (o1: IResourceBreaks | null, o2: IResourceBreaks | null): boolean =>
    this.resourceBreaksService.compareResourceBreaks(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resource }) => {
      this.resource = resource;
      if (resource) {
        this.updateForm(resource);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resource = this.resourceFormService.getResource(this.editForm);
    if (resource.id !== null) {
      this.subscribeToSaveResponse(this.resourceService.update(resource));
    } else {
      this.subscribeToSaveResponse(this.resourceService.create(resource));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResource>>): void {
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

  protected updateForm(resource: IResource): void {
    this.resource = resource;
    this.resourceFormService.resetForm(this.editForm, resource);

    this.resourceBreaksSharedCollection = this.resourceBreaksService.addResourceBreaksToCollectionIfMissing<IResourceBreaks>(
      this.resourceBreaksSharedCollection,
      resource.resourceBreak
    );
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(this.eventsSharedCollection, resource.event);
  }

  protected loadRelationshipsOptions(): void {
    this.resourceBreaksService
      .query()
      .pipe(map((res: HttpResponse<IResourceBreaks[]>) => res.body ?? []))
      .pipe(
        map((resourceBreaks: IResourceBreaks[]) =>
          this.resourceBreaksService.addResourceBreaksToCollectionIfMissing<IResourceBreaks>(resourceBreaks, this.resource?.resourceBreak)
        )
      )
      .subscribe((resourceBreaks: IResourceBreaks[]) => (this.resourceBreaksSharedCollection = resourceBreaks));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.resource?.event)))
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));
  }
}
