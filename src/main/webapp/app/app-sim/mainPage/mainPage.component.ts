import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { CallCardMobile } from './callCard/callCard.component';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { IEvent } from 'app/entities/event/event.model';
import { AccountService } from 'app/core/auth/account.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { Status } from 'app/entities/enumerations/status.model';
import { CreateAlert } from '../components/alert';
import { AddLogs } from 'app/shared/logs';
import { NgForOf } from '@angular/common';
import { ResourceAssignedService } from 'app/entities/resource-assigned/service/resource-assigned.service';
import { IResourceAssigned } from 'app/entities/resource-assigned/resource-assigned.model';
import { ResourceBreaksService } from 'app/entities/resource-breaks/service/resource-breaks.service';

@Component({
  standalone: true,
  selector: 'main-mobile-comp',
  templateUrl: './mainPage.component.html',
  imports: [CallCardMobile, NgForOf],
})
export class MainMobileComp implements OnInit, OnDestroy {
  @Input() resourceBreak: IResource | null = null;
  @Input() event: IEvent | null = null;
  @Input() closeShift: () => void;
  internalResource: IResource | null = null;
  color: string = 'green';

  markers: L.Marker[] = [];

  resourceAssignedListActive: IResourceAssigned[] = [];
  resourceAssignedListClosed: IResourceAssigned[] = [];

  map: L.Map | null = null;

  intervalId: number | null = null;

  endShiftBound = this.endShift.bind(this);

  constructor(
    private ResourceService: ResourceService,
    private resourceAssigned: ResourceAssignedService,
    private accountService: AccountService,
    private logsService: SystemLogService,
    private breakService: ResourceBreaksService
  ) {}

  ngOnInit(): void {
    this.internalResource = this.resourceBreak;
    this.initMap();
    this.loadResourceAssigned();

    this.intervalId = setInterval(() => {
      if (this.internalResource?.id) {
        this.loadResourceAssigned();
        this.refreshResourceBreak(this.internalResource);
      }
    }, 2000) as any;
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  loadResourceAssigned() {
    this.resourceAssigned
      .query({
        'resourceId.equals': this.internalResource?.id,
      })
      .subscribe(x => {
        x.body?.filter(e => {
          return (e.emergencyCall as any)?.closed !== true && e.greenTime === null;
        });

        let audio = new Audio('./content/audio/call.wav');

        let activesOnes = x.body?.filter(e => (e.emergencyCall as any)?.closed !== true && e.greenTime === undefined) ?? [];
        if (activesOnes.length > this.resourceAssignedListActive.length) {
          CreateAlert({
            topText: 'New Call',
            bottomText: 'A new call has been assigned to you!',
            onClick: () => {},
            buttonNum: 1,
          });
          audio.play();
        }

        this.resourceAssignedListActive = activesOnes;
        this.resourceAssignedListClosed = x.body?.filter(e => (e.emergencyCall as any)?.closed === true || e.greenTime !== undefined) ?? [];

        let newList: L.Marker[] = [];
        this.resourceAssignedListActive.forEach(e => {
          if (this.map === null) return;
          newList.push(
            L.marker([(e.emergencyCall as any)?.latitude ?? 0, (e.emergencyCall as any)?.longitude ?? 0], {
              icon: L.icon({
                iconUrl: '/content/images/marker.svg',
                iconSize: [40, 40],
                iconAnchor: [20, 40],
              }),
            })
              .addTo(this.map)
              .bindPopup(`Call: ${(e.emergencyCall as any)?.type}`, {
                offset: L.point(0, -20),
              })
              .openPopup()
          );
        });
      });
  }

  refreshResourceBreak(e: IResource): void {
    this.ResourceService.find(e.id ?? -1).subscribe({
      next: x => {
        this.internalResource = x.body;
        if (x.body?.status !== Status.GREEN) {
          this.color = 'red';
        } else {
          this.color = 'green';
        }

        navigator.geolocation.getCurrentPosition(position => {
          this.ResourceService.update({
            ...x.body,
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          } as any);
        });
      },
      error: x => {
        // Oppsie, probs a network issue
        // #Slay #Girlboss
      },
    });
  }

  requestBreak() {
    CreateAlert({
      topText: 'Are you sure?',
      bottomText: 'Do you want to request a break?',
      onClick: () => {
        this.breakService.find(this.internalResource?.resourceBreak?.id ?? -1).subscribe(x => {
          this.breakService
            .update({
              ...x.body,
              breakRequested: true,
            } as any)
            .subscribe(x => {
              AddLogs({
                accountsService: this.accountService,
                eventId: this.event?.id ?? -1,
                logService: this.logsService,
                logType: 'RESOURCE',
                messageContent: `Resource ${this.internalResource?.callSign} has requested a break!`,
              });
            });
        });
      },
    });
  }

  endShift() {
    CreateAlert({
      topText: 'Are you sure?',
      bottomText: 'This action is irreversible!',
      onClick: () => {
        this.ResourceService.find(this.internalResource?.id ?? -1).subscribe(x => {
          this.ResourceService.update({
            ...x.body,
            status: Status.SHIFT_END,
          } as any).subscribe(x => {
            AddLogs({
              accountsService: this.accountService,
              eventId: this.event?.id ?? -1,
              logService: this.logsService,
              logType: 'RESOURCE',
              messageContent: `Resource ${this.internalResource?.callSign} has signed off and status has been set to SHIFT_END!`,
            });
            this.closeShift();
          });
        });
      },
    });
  }

  private initMap(): void {
    console.log('init map');

    let interval = setInterval(() => {
      let mapElmenet = document.getElementById('map-element');
      if (!mapElmenet) return;
      clearInterval(interval);

      navigator.geolocation.getCurrentPosition(position => {
        let lat = position.coords.latitude;
        let lon = position.coords.longitude;
        this.map = L.map('map-element', {
          center: [lat, lon],
          zoom: 15,
        });

        console.log('map', this.map);

        //adding Map layer
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 20,
          minZoom: 10,
          attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        }).addTo(this.map);

        L.marker([lat, lon], {
          icon: L.icon({
            iconUrl: '/content/images/marker.svg',
            iconSize: [40, 40],
            iconAnchor: [20, 40],
          }),
        })
          .addTo(this.map)
          .bindPopup(`Your Location`, {
            offset: L.point(0, -20),
          })
          .openPopup();
      });
    }, 50);
  }
}
