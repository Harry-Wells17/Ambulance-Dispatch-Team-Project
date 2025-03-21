import { Component, Input, OnInit } from '@angular/core';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { AssignResourceComponent } from '../resourceAssign/assignResources.component';
import { NgIf } from '@angular/common';
import { EditResourcesComponent } from './editResource/editResource.component';
import { EditCallsComponent } from '../edit/editCall.component';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { AddLogs } from 'app/shared/logs';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';

@Component({
  standalone: true,
  selector: 'call-card-overview',
  templateUrl: './callCard.component.html',
  imports: [EditResourcesComponent, NgIf, EditCallsComponent],
})
export class CallCardComponent implements OnInit {
  @Input() emergencyCall!: IEmergencyCall;
  @Input() refresh!: Function;
  color: string = '';

  showAssignResource = false;
  showEditCall = false;
  showConfirmClose = false;

  public thecloseBound: Function;
  public theEditCloseBound: Function;

  closeCallBound = this.closeCall.bind(this);

  closeConfirmCloseBound = this.closeConfirmClose.bind(this);
  openConfirmCloseBound = this.openConfirmClose.bind(this);

  constructor(
    private emergencycalls: EmergencyCallService,
    private activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private systemLogService: SystemLogService
  ) {
    this.thecloseBound = this.close.bind(this);
    this.theEditCloseBound = this.closeEdit.bind(this);
  }

  closeConfirmClose() {
    this.showConfirmClose = false;
  }

  openConfirmClose() {
    this.showConfirmClose = true;
  }

  closeEdit() {
    this.showEditCall = false;
  }

  openEdit() {
    this.showEditCall = true;
  }

  close() {
    this.showAssignResource = false;
  }

  open() {
    this.showAssignResource = true;
  }

  closeCall() {
    AddLogs({
      accountsService: this.accountService,
      eventId: parseInt(this.activatedRoute.snapshot.params.id, 10),
      logService: this.systemLogService,
      emergencyCallId: this.emergencyCall.id,
      logType: 'CALL',
      messageContent: `This call was marked as close.`,
    });
    this.emergencycalls.update({ ...this.emergencyCall, closed: true }).subscribe(() => {
      this.refresh();
    });
  }

  reOpenCall() {
    AddLogs({
      accountsService: this.accountService,
      eventId: parseInt(this.activatedRoute.snapshot.params.id, 10),
      logService: this.systemLogService,
      emergencyCallId: this.emergencyCall.id,
      logType: 'CALL',
      messageContent: `This call was reopened and marked as open.`,
    });
    this.emergencycalls.update({ ...this.emergencyCall, closed: false }).subscribe(() => {
      this.refresh();
    });
  }

  ngOnInit() {
    this.color =
      this.emergencyCall.type === 'CAT1'
        ? '#b91c1c'
        : this.emergencyCall.type === 'CAT2'
        ? '#991b1b'
        : this.emergencyCall.type === 'CAT3'
        ? '#7f1d1d'
        : this.emergencyCall.type === 'CAT4'
        ? '#1e40af'
        : this.emergencyCall.type === 'CAT5'
        ? '#166534'
        : '#9a3412';
  }
}
