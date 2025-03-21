import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserManagement } from '../user-management.model';
import { UserManagementService } from '../service/user-management.service';

@Injectable({ providedIn: 'root' })
export class UserManagementRoutingResolveService implements Resolve<IUserManagement | null> {
  constructor(protected service: UserManagementService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserManagement | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userManagement: HttpResponse<IUserManagement>) => {
          if (userManagement.body) {
            return of(userManagement.body);
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
