import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { ResourceType } from 'app/entities/enumerations/resource-type.model';
import { Status } from 'app/entities/enumerations/status.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import L from 'leaflet';

@Component({
  standalone: true,
  selector: 'resource-assigned-component',
  templateUrl: './resourceAssigned.component.html',
  imports: [],
})
export class ResourceAssignedComponent implements OnDestroy {
  map: L.Map;
  resourceMarkers: L.Marker[] = [];
  callMarkers: L.Marker[] = [];

  private mainInterval: number | null;

  constructor(
    private resourceService: ResourceService,
    private activatedRoute: ActivatedRoute,
    private emergencyCallService: EmergencyCallService
  ) {
    let id = Math.random();
    this.mainInterval = setInterval(() => {
      this.loadMarkers();
    }, 3000) as any;
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.mainInterval) {
      clearInterval(this.mainInterval);
    }
  }

  private loadResources() {
    this.resourceService
      .query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      })
      .subscribe(x => {
        let newList: L.Marker[] = [];

        x.body?.forEach(resource => {
          if (resource.status === Status.SHIFT_END) return;
          let marker = L.marker([resource.latitude ?? 0, resource.longitude ?? 0], {
            alt: 'Resource',
            title: 'Drag me to the location of the emergency',
            icon: L.icon({
              iconUrl: `/content/images/${
                resource.type === ResourceType.AMBULANCE
                  ? 'Ambulance'
                  : resource.type === ResourceType.CYCLERESPONSE
                  ? 'Cycle'
                  : resource.type === ResourceType.FOOTTEAM
                  ? 'footTeam'
                  : resource.type === ResourceType.LOGISTICS
                  ? 'logistics'
                  : resource.type === ResourceType.MANAGEMENT
                  ? 'management'
                  : 'TreatmentCenter'
              }.svg`,
              iconSize: [40, 40],
              iconAnchor: [20, 40],
            }),
          });
          marker
            .bindPopup(`${resource.callSign} - ${resource.status}`, {
              offset: L.point(0, -20),
            })
            .openPopup();
          marker.addTo(this.map);
          newList.push(marker);
        });

        this.resourceMarkers.forEach(x => x.remove());
        this.resourceMarkers = newList;
      });
  }

  private loadCalls() {
    this.emergencyCallService
      .query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      })
      .subscribe(x => {
        let newList: L.Marker[] = [];

        x.body?.forEach(resource => {
          if (resource.closed) return;
          let marker = L.marker([resource.latitude ?? 0, resource.longitude ?? 0], {
            title: 'Drag me to the location of the emergency',
            icon: L.icon({
              iconUrl: `/content/images/${resource.type}.svg`,
              iconSize: [40, 40],
              iconAnchor: [20, 40],
            }),
          });
          marker
            .bindPopup(`${resource.type} - ${resource.injuries} - ${resource.condition}`, {
              offset: L.point(0, -20),
            })
            .openPopup();
          marker.addTo(this.map);
          newList.push(marker);
        });

        this.callMarkers.forEach(x => x.remove());
        this.callMarkers = newList;
      });
  }

  private loadMarkers() {
    this.loadResources();
    this.loadCalls();
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
          .bindPopup(`Your Location (CONTROL)`, {
            offset: L.point(0, -20),
          })
          .openPopup();

        /*

        L.marker([lat, lon], {
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
          });*/
      });
    }, 50);
  }
}
