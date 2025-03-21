import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserRole, NewUserRole } from '../user-role.model';

export type PartialUpdateUserRole = Partial<IUserRole> & Pick<IUserRole, 'id'>;

export type EntityResponseType = HttpResponse<IUserRole>;
export type EntityArrayResponseType = HttpResponse<IUserRole[]>;

@Injectable({ providedIn: 'root' })
export class UserRoleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-roles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userRole: NewUserRole): Observable<EntityResponseType> {
    return this.http.post<IUserRole>(this.resourceUrl, userRole, { observe: 'response' });
  }

  update(userRole: IUserRole): Observable<EntityResponseType> {
    return this.http.put<IUserRole>(`${this.resourceUrl}/${this.getUserRoleIdentifier(userRole)}`, userRole, { observe: 'response' });
  }

  partialUpdate(userRole: PartialUpdateUserRole): Observable<EntityResponseType> {
    return this.http.patch<IUserRole>(`${this.resourceUrl}/${this.getUserRoleIdentifier(userRole)}`, userRole, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserRole>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserRole[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserRoleIdentifier(userRole: Pick<IUserRole, 'id'>): number {
    return userRole.id;
  }

  compareUserRole(o1: Pick<IUserRole, 'id'> | null, o2: Pick<IUserRole, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserRoleIdentifier(o1) === this.getUserRoleIdentifier(o2) : o1 === o2;
  }

  addUserRoleToCollectionIfMissing<Type extends Pick<IUserRole, 'id'>>(
    userRoleCollection: Type[],
    ...userRolesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userRoles: Type[] = userRolesToCheck.filter(isPresent);
    if (userRoles.length > 0) {
      const userRoleCollectionIdentifiers = userRoleCollection.map(userRoleItem => this.getUserRoleIdentifier(userRoleItem)!);
      const userRolesToAdd = userRoles.filter(userRoleItem => {
        const userRoleIdentifier = this.getUserRoleIdentifier(userRoleItem);
        if (userRoleCollectionIdentifiers.includes(userRoleIdentifier)) {
          return false;
        }
        userRoleCollectionIdentifiers.push(userRoleIdentifier);
        return true;
      });
      return [...userRolesToAdd, ...userRoleCollection];
    }
    return userRoleCollection;
  }
}
