import { Component, Input } from '@angular/core';
import { IEvent } from 'app/entities/event/event.model';
import { ResourceBreaksService } from 'app/entities/resource-breaks/service/resource-breaks.service';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { TimeSince } from 'app/manage/events/card/eventCard.component';
import dayjs from 'dayjs/esm';
import { CreateAlert } from '../components/alert';
import { IResourceBreaks } from 'app/entities/resource-breaks/resource-breaks.model';
import { GeoLocationComponent } from 'app/entities/geo-location/list/geo-location.component';
import { Status } from 'app/entities/enumerations/status.model';
import { AccountService } from 'app/core/auth/account.service';
import { ActivatedRoute } from '@angular/router';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { AddLogs } from 'app/shared/logs';

@Component({
  standalone: true,
  selector: 'resource-type-page',
  templateUrl: './ResourceType.component.html',
  imports: [],
})
export class ResourceType {
  @Input() returnInfo: (e: IResource) => void;
  @Input() event: IEvent | null = null;
  @Input() goBack: () => void;

  debounce = false;

  resourceType: string = 'AMBULANCE';
  callSign: string = '';

  changeResourceTypeBound = this.changeResourceType.bind(this);
  changeCallSignBound = this.changeCallSign.bind(this);
  createResourceBound = this.createResource.bind(this);

  constructor(
    private resourceService: ResourceService,
    private resourceBreak: ResourceBreaksService,
    private accountService: AccountService,
    private logsService: SystemLogService
  ) {}

  changeResourceType(e: string): void {
    this.resourceType = e;
  }

  changeCallSign(e: string): void {
    this.callSign = e;
  }

  createResource() {
    if (this.debounce) return;
    this.debounce = true;
    this.resourceBreak
      .create({
        id: null,
        breakRequested: false,
        lastBreak: dayjs(),
        onBreak: false,
        startedBreak: null,
      })
      .subscribe({
        next: x => {
          navigator.geolocation.getCurrentPosition(z => {
            this.resourceService
              .create({
                callSign: this.callSign,
                id: null,
                created: dayjs(),
                event: { id: this?.event?.id ?? 1 },
                latitude: z.coords.latitude,
                longitude: z.coords.longitude,
                status: Status.GREEN,
                type: this.resourceType as any,
                resourceBreak: { id: x.body?.id ?? 1 },
              })
              .subscribe({
                next: x => {
                  if (x.body === null) {
                    CreateAlert({
                      topText: 'Error',
                      bottomText: 'Server error, unable to create resource',
                      buttonNum: 1,
                      onClick: () => {},
                    });
                    return;
                  }

                  AddLogs({
                    accountsService: this.accountService,
                    eventId: this.event?.id ?? -1,
                    logService: this.logsService,
                    logType: 'RESOURCE',
                    messageContent: `Resource ${x.body.callSign} has signed on and been made GREEN!`,
                  });
                  this.returnInfo(x.body);
                  this.debounce = false;
                },
                error: x => {
                  this.debounce = false;
                  CreateAlert({
                    topText: 'Error',
                    bottomText: 'Server error, unable to create resource',
                    buttonNum: 1,
                    onClick: () => {},
                  });
                },
              });
          });
        },
        error: x => {
          this.debounce = false;
          CreateAlert({
            topText: 'Error',
            bottomText: 'Server error, unable to create resource',
            buttonNum: 1,
            onClick: () => {},
          });
        },
      });
  }

  howLongAgo(x: string | undefined | null) {
    return TimeSince({
      time: new Date(`${x}`),
    });
  }
}
