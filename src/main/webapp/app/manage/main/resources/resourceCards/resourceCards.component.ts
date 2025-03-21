import { NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { IResourceAssigned } from 'app/entities/resource-assigned/resource-assigned.model';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceOverviewCallCard } from './callResourceCard/callResourceCard.component';
import { ActivatedRoute } from '@angular/router';
import { ResourceAssignedService } from 'app/entities/resource-assigned/service/resource-assigned.service';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { AddLogs } from 'app/shared/logs';
import { AccountService } from 'app/core/auth/account.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';

interface finalOutput {
  resource: IResource;
  assigned: number;
  calls: {
    assinged: IEmergencyCall;
    call: IResourceAssigned;
  }[];
}

@Component({
  standalone: true,
  selector: 'resources-overview-card',
  templateUrl: './resourceCards.component.html',
  imports: [NgForOf, ResourceOverviewCallCard],
  changeDetection: ChangeDetectionStrategy.Default,
})
export class ResourOverviewCard implements OnChanges {
  color: string = '';
  brightColor: string = '';
  @Input() call: finalOutput;

  @Input() reload: Function;

  callLoop: {
    assinged: IResourceAssigned;
    call: IEmergencyCall;
  }[];

  border: string = '';

  constructor(
    private ResourceService: ResourceService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private logService: SystemLogService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    this.color = this.call?.resource.status === 'GREEN' ? 'rgba(0,255,0,0.3)' : 'rgba(255,0,0,0.3)';
    this.brightColor = this.call?.resource.status === 'GREEN' ? 'rgba(0,255,0,1)' : 'rgba(255,0,0,1)';
    this.callLoop = this.call?.calls ?? [];
    this.border = `2px solid ${this.call?.resource.status === 'GREEN' ? 'rgba(0,255,0,0.3)' : 'rgba(255,0,0,0.3)'}`;
  }

  changeStatus(k: string) {
    this.ResourceService.find(this.call.resource.id).subscribe(x => {
      this.ResourceService.update({
        ...x.body,
        status: k as any,
      } as any).subscribe(x => {
        this.reload();

        AddLogs({
          accountsService: this.accountService,
          eventId: this.activatedRoute.snapshot.params.id,
          logService: this.logService,
          logType: 'RESOURCE',
          messageContent: `Changed resources status "${x.body?.callSign ?? ''}" to "${k}"!`,
        });
      });
    });
  }
}
