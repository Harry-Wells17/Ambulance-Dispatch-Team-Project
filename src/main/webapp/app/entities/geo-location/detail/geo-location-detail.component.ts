import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGeoLocation } from '../geo-location.model';

@Component({
  selector: 'jhi-geo-location-detail',
  templateUrl: './geo-location-detail.component.html',
})
export class GeoLocationDetailComponent implements OnInit {
  geoLocation: IGeoLocation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ geoLocation }) => {
      this.geoLocation = geoLocation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
