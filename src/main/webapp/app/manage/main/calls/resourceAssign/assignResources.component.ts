import { NgForOf, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { Status } from 'app/entities/enumerations/status.model';
import { ResourceAssignedService } from 'app/entities/resource-assigned/service/resource-assigned.service';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { AddLogs } from 'app/shared/logs';
import Dayjs from 'dayjs/esm';

@Component({
  standalone: true,
  selector: 'assignResource-component',
  templateUrl: './assignResource.component.html',
  imports: [NgForOf, NgIf],
})
export class AssignResourceComponent {
  public resourcePlusDistance: { resource: IResource; distance: number; color: string }[] = [];
  public renderResource: { resource: IResource; distance: number; color: string }[] = [];

  public resourcesToCommit: IResource[] = [];

  showCommited = false;

  @Input() call!: IEmergencyCall | null;
  @Input() close!: Function;
  @Input() edit!: string;

  constructor(
    private resourceService: ResourceService,
    private activatedRoute: ActivatedRoute,
    private resourceAssigned: ResourceAssignedService,
    private accountService: AccountService,
    private logService: SystemLogService
  ) {
    this.load();
  }

  load() {
    this.resourcePlusDistance = [];
    this.resourcesToCommit = [];
    this.resourceService
      .query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      })
      .subscribe(x => {
        x?.body?.forEach(resource => {
          this.resourcePlusDistance.push({
            resource: resource,
            color: resource.status === 'GREEN' ? 'green' : 'red',
            distance:
              Math.round(
                1000 * Haversine(this.call?.latitude ?? 0, this.call?.longitude ?? 0, resource.latitude ?? 0, resource.longitude ?? 0)
              ) / 1000,
          });
          this.display();
        });
      });
  }

  display() {
    if (this.showCommited) {
      this.renderResource = this.resourcePlusDistance.filter(x => x.resource.status !== 'SHIFT_END');
    } else {
      this.renderResource = this.resourcePlusDistance.filter(x => x.resource.status === 'GREEN');
    }
  }

  changeCommited() {
    this.showCommited = !this.showCommited;
    this.display();
  }

  commitResource(resource: IResource) {
    if (this.resourcesToCommit.includes(resource)) {
      this.resourcesToCommit = this.resourcesToCommit.filter(x => x !== resource);
    } else {
      this.resourcesToCommit.push(resource);
    }
  }

  submit() {
    // So lots has to happen
    // 1. We need to update the resources status to go from whatever to ENROUTE
    // 2. Next we need to generate a resource assigned for each resource
    AddLogs({
      accountsService: this.accountService,
      eventId: this.activatedRoute.snapshot.params.id,
      logService: this.logService,
      emergencyCallId: (this.call as any).id,
      logType: 'CALL',
      messageContent: `Assigned resources "${this.resourcesToCommit.map(e => e.callSign).join(', ')}" to a call!`,
    });

    this.resourcesToCommit.forEach(async resource => {
      this.resourceService.update({ ...resource, status: Status.ENROUTE }).subscribe(x => {
        AddLogs({
          accountsService: this.accountService,
          eventId: this.activatedRoute.snapshot.params.id,
          logService: this.logService,
          logType: 'RESOURCE',
          messageContent: `Changed resources status "${resource.callSign}" to "${Status.ENROUTE}"!`,
        });

        this.resourceAssigned
          .create({
            resource,
            callRecievedTime: Dayjs(),
            emergencyCall: this.call,
            id: null,
          })
          .subscribe(() => {
            this.close();

            console.log('Resource Assigned');
          });
      });
    });
  }
}

// To calculate the distance apart, cause we are on a globe we need to use the haversine formula
// More info can be found here: https://en.wikipedia.org/wiki/Haversine_formula#:~:text=The%20haversine%20formula%20determines%20the,and%20angles%20of%20spherical%20triangles.
// -- Harvey :)

let EarthsRadius = 6378000; // BTW thats Meters

function Haversine(lat1D: number, long1D: number, lat2D: number, long2D: number) {
  let lat1 = Radians(lat1D);
  let lat2 = Radians(lat2D);
  let long1 = Radians(long1D);
  let long2 = Radians(long2D);

  let latCal = 1 - Math.cos(lat2 - lat1) + Math.cos(lat1) * Math.cos(lat2);
  let longCal = 1 - Math.cos(long2 - long1);

  let nextThingy = Math.sqrt((latCal * longCal) / 2);

  // Looks confusing use wikipedia above

  return EarthsRadius * 2 * Math.asin(nextThingy);
}

function Radians(x: number) {
  return (x * Math.PI) / 180;
}
