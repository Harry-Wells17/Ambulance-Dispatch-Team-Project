import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGeoLocation } from '../geo-location.model';
import { GeoLocationService } from '../service/geo-location.service';

@Injectable({ providedIn: 'root' })
export class GeoLocationRoutingResolveService implements Resolve<IGeoLocation | null> {
  constructor(protected service: GeoLocationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGeoLocation | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((geoLocation: HttpResponse<IGeoLocation>) => {
          if (geoLocation.body) {
            return of(geoLocation.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
