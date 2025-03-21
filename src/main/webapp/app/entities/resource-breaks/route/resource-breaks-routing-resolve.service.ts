import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResourceBreaks } from '../resource-breaks.model';
import { ResourceBreaksService } from '../service/resource-breaks.service';

@Injectable({ providedIn: 'root' })
export class ResourceBreaksRoutingResolveService implements Resolve<IResourceBreaks | null> {
  constructor(protected service: ResourceBreaksService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResourceBreaks | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resourceBreaks: HttpResponse<IResourceBreaks>) => {
          if (resourceBreaks.body) {
            return of(resourceBreaks.body);
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
