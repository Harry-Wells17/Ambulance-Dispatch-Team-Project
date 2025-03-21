import { NgForOf, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { Status } from 'app/entities/enumerations/status.model';
import { IResourceAssigned } from 'app/entities/resource-assigned/resource-assigned.model';
import { ResourceAssignedService } from 'app/entities/resource-assigned/service/resource-assigned.service';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { AddLogs } from 'app/shared/logs';
import Dayjs from 'dayjs/esm';

@Component({
  standalone: true,
  selector: 'edit-resource-component',
  templateUrl: './editResource.component.html',
  imports: [NgForOf, NgIf],
})
export class EditResourcesComponent {
  public resourcePlusDistance: { resource: IResource; distance: number; color: string }[] = [];

  public resourcesAlreadyCommited: { resource: IResource; distance: number; color: string; resourceAssigned: IResourceAssigned }[] = [];
  public resourcesNotCommited: { resource: IResource; distance: number; color: string; resourceAssigned: IResourceAssigned }[] = [];
  public resourcesLeftScene: { resource: IResource; distance: number; color: string; resourceAssigned: IResourceAssigned }[] = [];

  @Input() call!: IEmergencyCall | null;
  @Input() close!: Function;

  showCommitted = false;
  showCommitted2 = false;

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
    this.resourceService
      .query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      })
      .subscribe(x => {
        x?.body
          ?.filter(e => e.status !== Status.SHIFT_END)
          .forEach(resource => {
            this.resourcePlusDistance.push({
              resource: resource,
              color: resource.status === 'GREEN' ? 'green' : 'red',
              distance:
                Math.round(
                  1000 * Haversine(this.call?.latitude ?? 0, this.call?.longitude ?? 0, resource.latitude ?? 0, resource.longitude ?? 0)
                ) / 1000,
            });
          });

        this.resourceAssigned.query({ 'emergencyCallId.equals': this.call?.id }).subscribe(x => {
          let queries = x.body?.map(x => x.resource?.id);
          let hashMap = {};
          x.body?.forEach(k => {
            hashMap[(k.resource as any)?.id] = k;
          });
          this.resourcesNotCommited = [];
          this.resourcesAlreadyCommited = [];
          this.resourcePlusDistance.forEach(k => {
            if (hashMap[k.resource.id] !== undefined && hashMap[k.resource.id].greenTime !== undefined) {
              this.resourcesLeftScene.push({ ...k, resourceAssigned: hashMap[k.resource.id] });
            } else if (queries?.includes(k.resource.id)) {
              this.resourcesAlreadyCommited.push({ ...k, resourceAssigned: x.body?.find(x => k.resource.id === x.resource?.id) as any });
            } else {
              this.resourcesNotCommited.push({ ...k, resourceAssigned: x.body?.find(x => k.resource.id === x.resource?.id) as any });
            }
          });
        });
      });
  }

  addResource(resource: IResource) {
    this.resourceService.update({ ...(resource as any), status: Status.ENROUTE }).subscribe(() => {
      this.resourceAssigned
        .create({
          id: null,
          resource: resource,
          emergencyCall: this.call,
          callRecievedTime: Dayjs(),
        })
        .subscribe(() => {
          this.showCommitted = true;

          AddLogs({
            accountsService: this.accountService,
            eventId: this.activatedRoute.snapshot.params.id,
            logService: this.logService,
            emergencyCallId: this.call?.id,
            logType: 'CALL',
            messageContent: `Assigned the resource "${resource.callSign}" to call!`,
          });

          AddLogs({
            accountsService: this.accountService,
            eventId: this.activatedRoute.snapshot.params.id,
            logService: this.logService,
            logType: 'RESOURCE',
            messageContent: `Changed resources status "${resource.callSign}" to "${Status.ENROUTE}"!`,
          });

          setTimeout(() => {
            this.showCommitted = false;
          }, 3000);
          this.load();
        });
    });
  }

  removeResource(resource: IResource, resourceAssigned: IResourceAssigned) {
    this.resourceService.update({ ...(resource as any), status: Status.GREEN }).subscribe(() => {
      this.resourceAssigned.delete(resourceAssigned.id).subscribe(() => {
        this.load();
        this.showCommitted2 = true;
        AddLogs({
          accountsService: this.accountService,
          eventId: this.activatedRoute.snapshot.params.id,
          logService: this.logService,
          emergencyCallId: this.call?.id,
          logType: 'CALL',
          messageContent: `Removed the resource "${resource.callSign}" from call!`,
        });

        AddLogs({
          accountsService: this.accountService,
          eventId: this.activatedRoute.snapshot.params.id,
          logService: this.logService,
          logType: 'RESOURCE',
          messageContent: `Changed resources status "${resource.callSign}" to "${Status.GREEN}"!`,
        });

        setTimeout(() => {
          this.showCommitted2 = false;
        }, 3000);
      });
    });
  }

  submit() {
    /*
    // So lots has to happen
    // 1. We need to update the resources status to go from whatever to ENROUTE
    // 2. Next we need to generate a resource assigned for each resource
    this.resourcesToCommit.forEach(async resource => {
      this.resourceService.update({ ...resource, status: Status.ENROUTE }).subscribe(x => {
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
    */
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
