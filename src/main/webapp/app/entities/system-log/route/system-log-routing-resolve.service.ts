import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISystemLog } from '../system-log.model';
import { SystemLogService } from '../service/system-log.service';

@Injectable({ providedIn: 'root' })
export class SystemLogRoutingResolveService implements Resolve<ISystemLog | null> {
  constructor(protected service: SystemLogService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISystemLog | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((systemLog: HttpResponse<ISystemLog>) => {
          if (systemLog.body) {
            return of(systemLog.body);
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
