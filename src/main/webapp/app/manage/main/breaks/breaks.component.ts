import { CommonModule, NgForOf, NgIf } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ASC, DESC } from 'app/config/navigation.constants';
import { AccountService } from 'app/core/auth/account.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { Status } from 'app/entities/enumerations/status.model';
import { IResourceBreaks } from 'app/entities/resource-breaks/resource-breaks.model';
import { EntityArrayResponseType, ResourceBreaksService } from 'app/entities/resource-breaks/service/resource-breaks.service';
import { ResourceBreaksFormGroup, ResourceBreaksFormService } from 'app/entities/resource-breaks/update/resource-breaks-form.service';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { TimeSince } from 'app/manage/events/card/eventCard.component';
import { AddLogs } from 'app/shared/logs';
import { Dayjs } from 'dayjs/esm';
import dayjs from 'dayjs/esm';
import { Observable, finalize, tap } from 'rxjs';

@Component({
  standalone: true,
  selector: 'breaks-component',
  templateUrl: './breaks.component.html',
  imports: [NgForOf, CommonModule, NgIf],
})
export class BreaksComponent implements OnInit, OnDestroy {
  @Input() dataIn!: IResourceBreaks;
  data: IResourceBreaks = {} as any;
  resourceBreaks?: IResourceBreaks[];
  breaksRequested?: IResourceBreaks[];
  breaksNotRequested?: IResourceBreaks[];
  resourceBreak: IResourceBreaks | null = null;
  editForm: ResourceBreaksFormGroup = this.resourceBreaksFormService.createResourceBreaksFormGroup();
  constructor(
    protected resourceBreaksService: ResourceBreaksService,
    protected resourceBreaksFormService: ResourceBreaksFormService,
    protected resources: ResourceService,
    protected accountService: AccountService,
    protected logService: SystemLogService,
    private activated: ActivatedRoute
  ) {
    setInterval(() => {
      this.Update();
    }, 3000);
  }
  isLoading = false;
  predicate = 'id';
  ascending = true;
  isSaving = false;
  callSignLookup: { [key: string]: IResource } = {};
  private timeOutNumber: number | null = null;

  create() {
    const resourceBreak = this.resourceBreaksFormService.getResourceBreaks(this.editForm);
    resourceBreak.id = null;
    resourceBreak.breakRequested = true;
    resourceBreak.lastBreak = dayjs();

    if (resourceBreak.id == null) {
      this.resourceBreaksService.create(resourceBreak).subscribe(x => {
        this.Update();
      });
    }
  }

  delete(resourceBreak: IResourceBreaks): void {
    this.resourceBreaksService.delete(resourceBreak.id).subscribe(x => {
      this.Update();
    });
  }

  ngOnInit(): void {
    this.Update();
  }

  ngOnDestroy(): void {
    if (this.timeOutNumber) {
      //clearTimeout(this.timeOutNumber);
    }
  }

  protected queryBackend(predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
      'eventId.equals': parseInt(this.activated.snapshot.params.id, 10),
    };
    return this.resources.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  async moveToOnBreak(resourceBreak: IResourceBreaks) {
    resourceBreak.breakRequested = false;
    resourceBreak.startedBreak = dayjs();
    resourceBreak.lastBreak = dayjs(resourceBreak.lastBreak);
    resourceBreak.onBreak = true;

    await this.save(resourceBreak);
    this.Update();
    this.resources.find(this.callSignLookup[resourceBreak.id].id).subscribe(x => {
      if (x.body === null) return;

      x.body.status = Status.BREAK;

      this.resources.update(x.body).subscribe(() => {
        this.Update();
      });
    });

    AddLogs({
      accountsService: this.accountService,
      eventId: this.activated.snapshot.params.id,
      logService: this.logService,
      logType: 'BREAKLOG',
      messageContent: `Move the resource "${this.callSignLookup[resourceBreak.id].callSign}" to on break!`,
    });
  }

  async moveToOffBreak(resourceBreak: IResourceBreaks) {
    resourceBreak.breakRequested = false;
    resourceBreak.onBreak = false;
    resourceBreak.lastBreak = dayjs();
    resourceBreak.startedBreak = dayjs(resourceBreak.startedBreak);
    await this.save(resourceBreak);
    this.Update();
    this.resources.find(this.callSignLookup[resourceBreak.id].id).subscribe(x => {
      if (x.body === null) return;

      x.body.status = Status.GREEN;

      this.resources.update(x.body).subscribe(() => {
        this.Update();
      });
    });

    AddLogs({
      accountsService: this.accountService,
      eventId: this.activated.snapshot.params.id,
      logService: this.logService,
      logType: 'BREAKLOG',
      messageContent: `Move the resource "${this.callSignLookup[resourceBreak.id].callSign}" to off break!`,
    });
  }

  save(resourceBreaks: IResourceBreaks): Promise<void> {
    return new Promise((resolve, reject) => {
      this.isSaving = true;

      if (resourceBreaks.id !== null) {
        let req = this.resourceBreaksService.update(resourceBreaks);
        req.subscribe(x => {
          resolve();
        });
      } else {
        this.subscribeToSaveResponse(this.resourceBreaksService.create(resourceBreaks as any));
      }
    });
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResourceBreaks>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.Update();
  }

  public howLongAgo(time: dayjs.Dayjs | null | undefined) {
    return TimeSince({ time: new Date(`${time}`) }) + ' ago';
  }

  public Update() {
    this.queryBackend().subscribe(x => {
      this.resourceBreaks = (x.body as IResource[])
        .filter(x => x.status !== Status.SHIFT_END)
        .map(x => x.resourceBreak) as IResourceBreaks[];

      (x.body as IResource[]).forEach(k => {
        this.callSignLookup[(k.resourceBreak as any).id] = k as any;
      });

      this.resourceBreaks = this.resourceBreaks.map(x => {
        return {
          ...x,
        } as any;
      }) as any as IResourceBreaks[];

      this.breaksNotRequested = this.resourceBreaks.filter(b => b.onBreak);
      this.breaksRequested = this.resourceBreaks.filter(b => !b.onBreak || b.onBreak === null);
    });
  }

  public event(e: MouseEvent) {
    e.preventDefault();
  }

  protected onSaveError(): void {
    console.log('ERRROR');
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    console.log('SAVEDS');
    this.isSaving = false;
  }
}
