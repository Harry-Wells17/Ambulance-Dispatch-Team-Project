import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserExist } from '../user-exist.model';
import { UserExistService } from '../service/user-exist.service';

@Injectable({ providedIn: 'root' })
export class UserExistRoutingResolveService implements Resolve<IUserExist | null> {
  constructor(protected service: UserExistService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserExist | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userExist: HttpResponse<IUserExist>) => {
          if (userExist.body) {
            return of(userExist.body);
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
