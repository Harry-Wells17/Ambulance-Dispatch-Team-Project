import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EmergencyCallFormService, EmergencyCallFormGroup } from './emergency-call-form.service';
import { IEmergencyCall } from '../emergency-call.model';
import { EmergencyCallService } from '../service/emergency-call.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { CallCategory } from 'app/entities/enumerations/call-category.model';
import { Sex } from 'app/entities/enumerations/sex.model';

@Component({
  selector: 'jhi-emergency-call-update',
  templateUrl: './emergency-call-update.component.html',
})
export class EmergencyCallUpdateComponent implements OnInit {
  isSaving = false;
  emergencyCall: IEmergencyCall | null = null;
  callCategoryValues = Object.keys(CallCategory);
  sexValues = Object.keys(Sex);

  usersSharedCollection: IUser[] = [];
  eventsSharedCollection: IEvent[] = [];

  editForm: EmergencyCallFormGroup = this.emergencyCallFormService.createEmergencyCallFormGroup();

  constructor(
    protected emergencyCallService: EmergencyCallService,
    protected emergencyCallFormService: EmergencyCallFormService,
    protected userService: UserService,
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ emergencyCall }) => {
      this.emergencyCall = emergencyCall;
      if (emergencyCall) {
        this.updateForm(emergencyCall);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const emergencyCall = this.emergencyCallFormService.getEmergencyCall(this.editForm);
    if (emergencyCall.id !== null) {
      this.subscribeToSaveResponse(this.emergencyCallService.update(emergencyCall));
    } else {
      this.subscribeToSaveResponse(this.emergencyCallService.create(emergencyCall));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmergencyCall>>): void {
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

  protected updateForm(emergencyCall: IEmergencyCall): void {
    this.emergencyCall = emergencyCall;
    this.emergencyCallFormService.resetForm(this.editForm, emergencyCall);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, emergencyCall.createdBy);
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(this.eventsSharedCollection, emergencyCall.event);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.emergencyCall?.createdBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.emergencyCall?.event)))
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));
  }
}
