import { Injectable, OnInit } from '@angular/core';
import { ASC, DESC } from 'app/config/navigation.constants';
import { Observable, tap } from 'rxjs';
import { EntityArrayResponseType as EntityCallResponseType } from 'app/entities/emergency-call/service/emergency-call.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { IGeoLocation } from 'app/entities/geo-location/geo-location.model';
import { EntityArrayResponseType as EntityGeoResponseType } from 'app/entities/geo-location/service/geo-location.service';
import { EntityArrayResponseType as EntityResourceResponseType } from 'app/entities/resource/service/resource.service';
import { GeoLocationService } from 'app/entities/geo-location/service/geo-location.service';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';

@Injectable({
  providedIn: 'root',
})
export class MapViewService {
  predicate = 'id';
  ascending = true;
  isLoadingR = false;
  isLoadingC = false;
  isLoadingG = false;

  calls?: IEmergencyCall[] = [];
  geos?: IGeoLocation[] = [];
  resources?: IResource[] = [];

  constructor(
    protected callService: EmergencyCallService,
    protected geoService: GeoLocationService,
    protected resourceService: ResourceService
  ) {}

  public queryResourceBackend(predicate?: string, ascending?: boolean): Observable<EntityResourceResponseType> {
    this.isLoadingR = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.resourceService.query(queryObject).pipe(tap(() => (this.isLoadingR = false)));
  }

  public queryCallBackend(predicate?: string, ascending?: boolean): Observable<EntityCallResponseType> {
    this.isLoadingC = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.callService.query(queryObject).pipe(tap(() => (this.isLoadingC = false)));
  }

  public queryGeoBackend(predicate?: string, ascending?: boolean): Observable<EntityGeoResponseType> {
    this.isLoadingG = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.geoService.query(queryObject).pipe(tap(() => (this.isLoadingG = false)));
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
