import { Component, OnDestroy } from '@angular/core';
import { CreateCallsComponent } from './createNew/createCall.component';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { Observable, tap } from 'rxjs';
import { EmergencyCallService, EntityArrayResponseType } from 'app/entities/emergency-call/service/emergency-call.service';
import { ASC } from 'app/config/navigation.constants';
import { NgForOf, NgIf } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CallCardComponent } from './callCard/callCard.component';
import { AssignResourceComponent } from './resourceAssign/assignResources.component';

@Component({
  standalone: true,
  selector: 'calls-component',
  templateUrl: './calls.component.html',
  imports: [CreateCallsComponent, NgForOf, CallCardComponent, AssignResourceComponent, NgIf],
})
export class CallsComponent implements OnDestroy {
  callsPreFilter?: IEmergencyCall[] = [];
  calls?: IEmergencyCall[] = [];
  isLoading = false;
  predicate = 'id';
  public createdNewEvent: Function;
  currentCall: IEmergencyCall | null = null;

  showClosedCalls = false;

  showAssignResource = false;
  public theBoundCallback: Function;
  public thecloseBound: Function;
  public recieveRequestToRefreshBound: Function;

  changedClosedBound = this.changeClosed.bind(this);

  constructor(protected emegencyCallService: EmergencyCallService, private activatedRoute: ActivatedRoute) {
    this.theBoundCallback = this.createdNewCall.bind(this);
    this.thecloseBound = this.close.bind(this);
    this.recieveRequestToRefreshBound = this.recieveRequestToRefresh.bind(this);
  }

  private interval: number | null = null;

  ngOnInit(): void {
    setInterval(() => {
      this.refresh();
    }, 20000);
    this.refresh();
  }

  ngOnDestroy(): void {
    if (this.interval !== null) {
      clearInterval(this.interval);
    }
  }

  changeClosed() {
    this.showClosedCalls = !this.showClosedCalls;
    this.showControl();
  }

  recieveRequestToRefresh() {
    this.refresh();
  }

  close() {
    this.showAssignResource = false;
  }

  createdNewCall(x: IEmergencyCall) {
    console.log('created new call: ' + x);
    this.currentCall = x;
    this.showAssignResource = true;
  }

  showControl() {
    let newer = this.callsPreFilter;
    if (newer === undefined) return;
    if (!this.showClosedCalls) newer = newer.filter(x => x.closed !== true);
    this.calls = newer;
  }

  async refresh() {
    console.log('refresh');
    this.queryBackend().subscribe(x => {
      // TODO: This is janky fix later Harvey :)
      let newer = x.body?.filter(e => {
        return e.event?.id === parseInt(this.activatedRoute.snapshot.params.id, 10);
      }) as IEmergencyCall[];

      if (JSON.stringify(newer) === JSON.stringify(this.callsPreFilter)) {
        return;
      }
      this.callsPreFilter = newer;
      this.showControl();
    });
  }

  protected queryBackend(predicate?: string): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate),
    };
    return this.emegencyCallService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected getSortQueryParam(predicate = this.predicate): string[] {
    const ascendingQueryParam = ASC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
