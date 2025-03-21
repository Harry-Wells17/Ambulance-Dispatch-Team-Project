import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEmergencyCall } from '../emergency-call.model';
import { EmergencyCallService } from '../service/emergency-call.service';

@Injectable({ providedIn: 'root' })
export class EmergencyCallRoutingResolveService implements Resolve<IEmergencyCall | null> {
  constructor(protected service: EmergencyCallService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEmergencyCall | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((emergencyCall: HttpResponse<IEmergencyCall>) => {
          if (emergencyCall.body) {
            return of(emergencyCall.body);
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
