import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SystemLogFormService, SystemLogFormGroup } from './system-log-form.service';
import { ISystemLog } from '../system-log.model';
import { SystemLogService } from '../service/system-log.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { LogType } from 'app/entities/enumerations/log-type.model';

@Component({
  selector: 'jhi-system-log-update',
  templateUrl: './system-log-update.component.html',
})
export class SystemLogUpdateComponent implements OnInit {
  isSaving = false;
  systemLog: ISystemLog | null = null;
  logTypeValues = Object.keys(LogType);

  usersSharedCollection: IUser[] = [];
  emergencyCallsSharedCollection: IEmergencyCall[] = [];
  eventsSharedCollection: IEvent[] = [];

  editForm: SystemLogFormGroup = this.systemLogFormService.createSystemLogFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected systemLogService: SystemLogService,
    protected systemLogFormService: SystemLogFormService,
    protected userService: UserService,
    protected emergencyCallService: EmergencyCallService,
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEmergencyCall = (o1: IEmergencyCall | null, o2: IEmergencyCall | null): boolean =>
    this.emergencyCallService.compareEmergencyCall(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemLog }) => {
      this.systemLog = systemLog;
      if (systemLog) {
        this.updateForm(systemLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamprojectApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const systemLog = this.systemLogFormService.getSystemLog(this.editForm);
    if (systemLog.id !== null) {
      this.subscribeToSaveResponse(this.systemLogService.update(systemLog));
    } else {
      this.subscribeToSaveResponse(this.systemLogService.create(systemLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISystemLog>>): void {
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

  protected updateForm(systemLog: ISystemLog): void {
    this.systemLog = systemLog;
    this.systemLogFormService.resetForm(this.editForm, systemLog);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, systemLog.createdBy);
    this.emergencyCallsSharedCollection = this.emergencyCallService.addEmergencyCallToCollectionIfMissing<IEmergencyCall>(
      this.emergencyCallsSharedCollection,
      systemLog.emergencyCall
    );
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(this.eventsSharedCollection, systemLog.event);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.systemLog?.createdBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.emergencyCallService
      .query()
      .pipe(map((res: HttpResponse<IEmergencyCall[]>) => res.body ?? []))
      .pipe(
        map((emergencyCalls: IEmergencyCall[]) =>
          this.emergencyCallService.addEmergencyCallToCollectionIfMissing<IEmergencyCall>(emergencyCalls, this.systemLog?.emergencyCall)
        )
      )
      .subscribe((emergencyCalls: IEmergencyCall[]) => (this.emergencyCallsSharedCollection = emergencyCalls));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.systemLog?.event)))
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));
  }
}
