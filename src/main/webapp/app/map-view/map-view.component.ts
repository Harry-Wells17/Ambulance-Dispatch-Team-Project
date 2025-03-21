import { AfterViewInit, Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { MapViewService } from './service/map-view.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { IGeoLocation } from 'app/entities/geo-location/geo-location.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { GeoLocationService } from 'app/entities/geo-location/service/geo-location.service';
import { NgForOf, NgIf } from '@angular/common';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { RouterLink, RouterModule } from '@angular/router';
import { IResource } from 'app/entities/resource/resource.model';

@Component({
  standalone: true,
  selector: 'jhi-map-view',
  templateUrl: './map-view.component.html',
  styleUrls: ['./map-view.component.scss'],
  imports: [NgIf, RouterLink, RouterModule],
})
export class MapViewComponent implements AfterViewInit, OnInit {
  private map;
  calls?: IEmergencyCall[] = [];
  geos?: IGeoLocation[] = [];
  openCalls?: IEmergencyCall[] = [];
  resources?: IResource[] = [];

  tiles: L.TileLayer;
  resourceMarkers?: L.Marker[] = [];
  callMarkers?: L.CircleMarker[] = [];

  resourceLayer: L.LayerGroup;
  callLayer: L.LayerGroup;

  layerControl;

  account: Account | null = null;

  //initialise map
  private initMap(): void {
    this.tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 20,
      minZoom: 10,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    });

    this.map = L.map('map', {
      center: [52.4519, -1.943],
      zoom: 15,
      layers: [this.tiles],
    });

    //adding Map layer
  }

  constructor(
    protected mapService: MapViewService,
    protected callService: EmergencyCallService,
    protected geoService: GeoLocationService,
    protected accountService: AccountService
  ) {}

  addLayerControl(): void {
    var map = {
      Map: this.tiles,
    };
    console.log(this.callMarkers);
    console.log(this.resourceMarkers);

    this.layerControl = L.control.layers(map).addTo(this.map);
  }

  ngOnInit(): void {
    //run subscriptions
    this.subscribeBackend();
    // this.subscribeAccount();
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.addLayerControl();
  }

  //add markers of open calls to map
  addCallMarkers(): void {
    for (var call of this.calls!) {
      if (call.open == true) {
        this.openCalls?.push(call);
      }
    }
    if (this.openCalls!.length != 0) {
      return;
      /*
      for (var openCall of this.openCalls!) {
        for (var geo of this.geos!) {
          if (JSON.stringify(geo) === JSON.stringify(openCall.geoId)) {
            var marker = L.circleMarker([geo.latitude!, geo.longitude!]); //.addTo(this.map);
            this.addCallPopup(marker, openCall);
            this.setCallType(marker, openCall);
            this.callMarkers!.push(marker);
          }
        }
      }*/
    }
    this.callLayer = L.layerGroup(this.callMarkers);
    this.layerControl.addOverlay(this.callLayer, 'Calls');
  }

  //add resource markers to map
  addResourceMarkers(): void {
    for (var resource of this.resources!) {
      var resourceType = resource.type;
      var status = resource.status;
      var resourceString = '';
      var statusString = '';
      var iconURL = '';

      switch (resourceType) {
        case 'AMBULANCE':
          resourceString = 'ambulance/ambulance_';
          break;
        case 'CYCLERESPONSE':
          resourceString = 'cycle/cycling_';
          break;
        case 'FOOTTEAM':
          resourceString = 'footteam/pedestriancrossing_';
          break;
        case 'LOGISTICS':
          resourceString = 'logistics/van_';
          break;
        case 'MANAGEMENT':
          resourceString = 'management/administration_';
          break;
        case 'TREATMENTCENTRE':
          resourceString = 'treatment/hospital-building_';
          break;
      }

      switch (status) {
        case 'RED':
          statusString = 'r';
          break;
        case 'GREEN':
          statusString = 'fg';
          break;
        case 'BREAK':
          statusString = 'g';
          break;
        case 'CLEAR':
          statusString = 'c';
          break;
        case 'ONSCENE':
          statusString = 'o';
          break;
        case 'ENROUTE':
          statusString = 'p';
          break;
      }

      iconURL = 'content/images/ambudispatch-icons/' + resourceString + statusString + '.png';

      var resourceIcon = L.icon({
        iconUrl: iconURL,
        iconSize: [32, 37],
        iconAnchor: [16, 5],
        popupAnchor: [0, 0],
      });

      var marker = L.marker([resource.latitude!, resource.longitude!], { icon: resourceIcon }); //.addTo(this.map);
      this.addResourcePopup(marker, resource);
      this.resourceMarkers!.push(marker);
    }
    this.resourceLayer = L.layerGroup(this.resourceMarkers);
    this.layerControl.addOverlay(this.resourceLayer, 'Resources');
  }

  //add popups to resources
  addResourcePopup(marker, resource): void {
    var content = `<center>
      <p>
        <strong>Resource: ${resource.id}</strong>
      </p>
      <p>
        Type: ${resource.type},
        Status: ${resource.status}
      </p>
      <p>
        Open since: ${resource.created}
      </p>
      `;
    marker.bindPopup(content);
  }

  setCallType(marker, call): void {
    var callType = call.type;
    switch (callType) {
      case 'CAT1':
        marker.setStyle({ color: '#ff6257' });
        break;
      case 'CAT2':
        marker.setStyle({ color: '#bf170b' });
        break;
      case 'CAT3':
        marker.setStyle({ color: '#6b0801' });
        break;
      case 'CAT4':
        marker.setStyle({ color: '#141dc7' });
        break;
      case 'CAT5':
        marker.setStyle({ color: '#18d62b' });
        break;
    }
  }

  //add popups to calls
  addCallPopup(marker, call): void {
    var content = `<center>
      <p>
        <strong>Call: ${call.id}</strong>
      </p>
      <p>
        Category: ${call.type},
        Age: ${call.age}, Sex: ${call.sexAssignedAtBirth}
     </p>
      <p>
        Injuries: ${call.injuries}
      </p>
    `;
    marker.bindPopup(content);
    // marker.bindPopup('<a [routerLink]="/login"><button class="button bg-slate-600 hover:bg-slate-700 w-[100px] mt-3">Sign In</button></a>');
  }

  subscribeBackend(): void {
    (async () => {
      this.mapService.queryGeoBackend().subscribe(x => {
        console.log(x.body);
        this.geos = x.body as any;
        (async () => {
          this.mapService.queryCallBackend().subscribe(x => {
            console.log(x.body);
            this.calls = x.body as any;
            this.addCallMarkers();
          });
        })();
        (async () => {
          this.mapService.queryResourceBackend().subscribe(x => {
            console.log(x.body);
            this.resources = x.body as any;
            this.addResourceMarkers();
          });
        })();
      });
    })();
  }

  // subscribeAccount(): void {
  //   this.accountService.getAuthenticationState().subscribe(account => {
  //     this.account = account;
  //   })
  // }
}
