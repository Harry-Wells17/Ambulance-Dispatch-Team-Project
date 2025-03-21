import { Component, Input, OnDestroy } from '@angular/core';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { ActivatedRoute } from '@angular/router';
import { IResourceAssigned } from 'app/entities/resource-assigned/resource-assigned.model';
import { ResourceAssignedService } from 'app/entities/resource-assigned/service/resource-assigned.service';
import { ResourOverviewCard } from './resourceCards/resourceCards.component';
import { NgForOf, NgIf } from '@angular/common';

@Component({
  standalone: true,
  selector: 'resources-component',
  templateUrl: './resources.component.html',
  imports: [ResourOverviewCard, NgForOf, NgIf],
})
export class ResourceComponent implements OnDestroy {
  @Input() large: boolean = false;

  finalOutput: {
    resource: IResource;
    assigned: number;
    calls: {
      assinged: IResourceAssigned;
      call: IEmergencyCall;
    }[];
  }[] = [];

  preFilterFinalOutput: {
    resource: IResource;
    assigned: number;
    calls: {
      assinged: IResourceAssigned;
      call: IEmergencyCall;
    }[];
  }[] = [];

  private Interval: null | number = null;

  showEndShift = false;

  changeViewBound = this.changeView.bind(this);

  constructor(
    private ResourceService: ResourceService,
    private EmergencyCallService: EmergencyCallService,
    private ResourceAssignedService: ResourceAssignedService,
    private activatedRoute: ActivatedRoute
  ) {
    this.Interval = setInterval(() => {
      this.load();
    }, 3000) as any;
    this.load();
  }

  changeView() {
    this.showEndShift = !this.showEndShift;
    this.loadView();
  }

  ngOnDestroy(): void {
    if (this.Interval !== null) {
      clearInterval(this.Interval);
    }
  }

  async loadResources(): Promise<IResource[]> {
    return new Promise((resolve, reject) => {
      this.ResourceService.query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      }).subscribe(x => {
        resolve(x.body ?? []);
      });
    });
  }

  async loadCalls(): Promise<IEmergencyCall[]> {
    return new Promise((resolve, reject) => {
      this.EmergencyCallService.query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      }).subscribe(x => {
        resolve(x.body ?? []);
      });
    });
  }
  async loadResourceAssigned(): Promise<IResourceAssigned[]> {
    return new Promise((resolve, reject) => {
      this.ResourceAssignedService.query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      }).subscribe(x => {
        resolve(x.body ?? []);
      });
    });
  }

  async reload() {
    this.load();
  }

  async loadView() {
    let newer = this.preFilterFinalOutput;
    if (newer === undefined) return;
    if (!this.showEndShift) newer = newer.filter(x => x.resource.status !== 'SHIFT_END');
    this.finalOutput = newer;
  }

  async load() {
    let calls = await this.loadCalls();
    let resources = await this.loadResources();
    let resourceAssigned = await this.loadResourceAssigned();

    let newer = resources.map(e => {
      let relevantAssigns = resourceAssigned.filter(assigned => {
        return (assigned.resource ?? { id: -1 }).id === e.id;
      });
      return {
        resource: e,
        assigned:
          relevantAssigns
            .map(x => {
              return {
                assinged: calls.find(call => call.id === x.emergencyCall?.id),
                call: x as IResourceAssigned,
              };
            })
            .filter(x => {
              return x.call.greenTime === undefined && (x.call.emergencyCall as any)?.closed !== true;
            }).length ?? 0,
        calls: relevantAssigns
          .map(x => {
            return {
              assinged: calls.find(call => call.id === x.emergencyCall?.id) as any,
              call: x,
            };
          })
          .filter(x => {
            return x.call.greenTime === undefined && (x.call.emergencyCall as any).closed !== true;
          }),
      };
    });

    if (JSON.stringify(this.preFilterFinalOutput) !== JSON.stringify(newer)) {
      this.preFilterFinalOutput = newer;
    }
    this.loadView();
  }
}
