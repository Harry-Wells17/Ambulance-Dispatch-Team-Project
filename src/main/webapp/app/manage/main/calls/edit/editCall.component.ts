import { NgIf, NgForOf, CommonModule } from '@angular/common';
import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { EmergencyCallFormGroup, EmergencyCallFormService } from 'app/entities/emergency-call/update/emergency-call-form.service';
import { GeoLocationService } from 'app/entities/geo-location/service/geo-location.service';
import dayjs from 'dayjs/esm';
import { Subject, takeUntil } from 'rxjs';
import * as L from 'leaflet';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { AddLogs } from 'app/shared/logs';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';

@Component({
  standalone: true,
  selector: 'edit-calls-component',
  templateUrl: './editCall.component.html',
  imports: [NgIf, ReactiveFormsModule, NgForOf, CommonModule],
})
export class EditCallsComponent implements OnInit, OnDestroy {
  @Input() callId: number;
  @Input() close: Function;
  @Input() refresh: Function;

  private map;

  loadingNew = false;
  showSuccess = false;
  showError = false;

  account: Account | null = null;

  editForm: EmergencyCallFormGroup = this.emergencyCallFormService.createEmergencyCallFormGroup();
  newCall: IEmergencyCall = {
    id: null as any,
  };

  private readonly destroy$ = new Subject<void>();

  public resources: IResource[] = [];

  constructor(
    private accountService: AccountService,
    protected emergencyCallService: EmergencyCallService,
    protected emergencyCallFormService: EmergencyCallFormService,
    protected geoLocationService: GeoLocationService,
    private activatedRoute: ActivatedRoute,
    private resourceService: ResourceService,
    private logService: SystemLogService
  ) {
    this.editForm.controls.type.setValue('CAT1' as any);
  }

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        console.log('Account', account);
        return (this.account = account);
      });

    this.emergencyCallService.find(this.callId).subscribe(x => {
      this.editForm = this.emergencyCallFormService.createEmergencyCallFormGroup(x.body as any);
    });

    this.initMap();
  }

  private initMap(): void {
    let interval = setInterval(() => {
      let mapElmenet = document.getElementById('map-element-3');
      console.log('Looking for map');
      if (!mapElmenet) return;
      clearInterval(interval);

      console.log('found map');

      navigator.geolocation.getCurrentPosition(position => {
        let lat = position.coords.latitude;
        let lon = position.coords.longitude;
        this.map = L.map('map-element-3', {
          center: [lat, lon],
          zoom: 15,
        });

        console.log('Map loaded');

        //adding Map layer
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 20,
          minZoom: 10,
          attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        }).addTo(this.map);

        L.marker([lat, lon], {
          alt: 'Call',
          draggable: true,
          autoPan: true,
          title: 'Drag me to the location of the emergency',
          interactive: true,
          icon: L.icon({
            iconUrl: '/content/images/marker.svg',
            iconSize: [40, 40],
            iconAnchor: [20, 40],
          }),
        })
          .addTo(this.map)
          .on('dragend', e => {
            let newLat = e.target.getLatLng().lat;
            let newLon = e.target.getLatLng().lng;

            this.editForm.controls.latitude.setValue(newLat);
            this.editForm.controls.longitude.setValue(newLon);
          });
      });
    }, 50);
  }

  showCreateCall() {
    this.close();

    this.initMap();
  }
  hideCreateNewCall() {
    this.close();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  selectCatagory(input: string) {
    this.editForm.controls.type.setValue(input as any);
  }

  quickActionSex(input: string) {
    this.editForm.controls.sexAssignedAtBirth.setValue(input as any);
  }

  quickActionInjuries(input: string) {
    this.editForm.controls.injuries.setValue(((this.editForm.controls.injuries.value ?? '') + input) as any);
  }

  quickActionCondition(input: string) {
    this.editForm.controls.condition.setValue(((this.editForm.controls.condition.value ?? '') + input) as any);
  }

  create() {
    if (this.loadingNew) return;
    this.loadingNew = true;

    const event = this.emergencyCallFormService.getEmergencyCall(this.editForm);
    event.created = dayjs();

    event.createdBy = { login: this.account?.login, id: (this.account as any)?.id };
    event.event = { id: parseInt(this.activatedRoute.snapshot.params.id, 10) };

    this.emergencyCallService.update(event as any).subscribe(
      x => {
        this.showSuccess = true;
        this.close();
        this.loadingNew = false;
        setTimeout(() => {
          this.showSuccess = false;
        }, 3000);

        AddLogs({
          accountsService: this.accountService,
          eventId: this.activatedRoute.snapshot.params.id,
          logService: this.logService,
          emergencyCallId: (x.body as any).id,
          logType: 'CALL',
          messageContent: `Edited a ${event.type} call!`,
        });
        this.refresh();
      },
      err => {
        this.showError = true;

        this.loadingNew = false;
        setTimeout(() => {
          this.showError = false;
        }, 3000);
      }
    );
  }
}
