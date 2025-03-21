import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResourceAssigned } from '../resource-assigned.model';
import { ResourceAssignedService } from '../service/resource-assigned.service';

@Injectable({ providedIn: 'root' })
export class ResourceAssignedRoutingResolveService implements Resolve<IResourceAssigned | null> {
  constructor(protected service: ResourceAssignedService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResourceAssigned | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resourceAssigned: HttpResponse<IResourceAssigned>) => {
          if (resourceAssigned.body) {
            return of(resourceAssigned.body);
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
