import { NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { CreateAlert } from 'app/app-sim/components/alert';
import { AccountService } from 'app/core/auth/account.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { IResourceAssigned } from 'app/entities/resource-assigned/resource-assigned.model';
import { ResourceAssignedService } from 'app/entities/resource-assigned/service/resource-assigned.service';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { TimeSince } from 'app/manage/events/card/eventCard.component';
import { AddLogs } from 'app/shared/logs';
import dayjs from 'dayjs/esm';

@Component({
  standalone: true,
  selector: 'call-card-mobile',
  templateUrl: './callCard.component.html',
  imports: [NgIf],
})
export class CallCardMobile implements OnInit {
  @Input() resourceAssigned: IResourceAssigned;
  buttonText: string = '';

  emergencyCall: IEmergencyCall;

  reqChangeStateBound = this.reqChangeState.bind(this);

  constructor(
    private ResourceService: ResourceService,
    private ResourceAssignedService: ResourceAssignedService,
    private accountSystem: AccountService,
    private logSystem: SystemLogService
  ) {}

  ngOnInit(): void {
    this.emergencyCall = this.resourceAssigned.emergencyCall as IEmergencyCall;
  }

  getState() {
    if (this.resourceAssigned.greenTime !== undefined) {
      return '';
    } else if (this.resourceAssigned.clearHospitalTime !== undefined) {
      return 'Status GREEN';
    } else if (this.resourceAssigned.arrivedHospitalTime !== undefined) {
      return 'Cleared Hospital';
    } else if (this.resourceAssigned.leftSceneTime !== undefined) {
      return 'Arrived Hospital';
    } else if (this.resourceAssigned.onSceneTime !== undefined) {
      return 'Left Scene';
    } else {
      return 'Arrived Scene';
    }
  }

  changeState() {
    this.ResourceService.find((this.resourceAssigned.resource as any)?.id).subscribe(resource => {
      this.ResourceAssignedService.find(this.resourceAssigned?.id).subscribe(k => {
        let json = k.body as IResourceAssigned;

        let newStatus = '';
        let stateCurrent = this.getState();
        if (stateCurrent === 'Arrived Scene') {
          json.onSceneTime = dayjs();
          newStatus = 'ONSCENE';
        } else if (stateCurrent === 'Left Scene') {
          json.leftSceneTime = dayjs();
          newStatus = 'RED';
        } else if (stateCurrent === 'Arrived Hospital') {
          json.arrivedHospitalTime = dayjs();
          newStatus = 'RED';
        } else if (stateCurrent === 'Cleared Hospital') {
          json.clearHospitalTime = dayjs();
          newStatus = 'RED';
        } else {
          json.greenTime = dayjs();
          newStatus = 'GREEN';
        }

        this.ResourceService.update({
          ...(resource.body as any),
          status: newStatus,
        }).subscribe(() => {
          console.log('DONE');
        });

        AddLogs({
          accountsService: this.accountSystem,
          eventId: resource.body?.event?.id ?? -1,
          logService: this.logSystem,
          emergencyCallId: this.emergencyCall.id,
          logType: 'RESOURCE',
          messageContent: `Resource "${resource?.body?.callSign}" has changed call status to "${stateCurrent}" and changed resource status to ${newStatus}!`,
        });

        this.ResourceAssignedService.update(json).subscribe(() => {
          console.log('DONE');
        });
      });
    });
  }

  reqChangeState() {
    CreateAlert({ topText: 'Are you sure?', bottomText: `Change to '${this.getState()}'`, onClick: () => this.changeState() });
  }

  time(x: dayjs.Dayjs | null | undefined): string {
    return TimeSince({ time: new Date(`${x}`) });
  }
}
