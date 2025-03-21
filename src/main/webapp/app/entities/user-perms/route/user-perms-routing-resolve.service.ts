import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserPerms } from '../user-perms.model';
import { UserPermsService } from '../service/user-perms.service';

@Injectable({ providedIn: 'root' })
export class UserPermsRoutingResolveService implements Resolve<IUserPerms | null> {
  constructor(protected service: UserPermsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserPerms | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userPerms: HttpResponse<IUserPerms>) => {
          if (userPerms.body) {
            return of(userPerms.body);
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
