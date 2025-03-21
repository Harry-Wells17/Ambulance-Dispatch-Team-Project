import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserManagement, NewUserManagement } from '../user-management.model';

export type PartialUpdateUserManagement = Partial<IUserManagement> & Pick<IUserManagement, 'id'>;

export type EntityResponseType = HttpResponse<IUserManagement>;
export type EntityArrayResponseType = HttpResponse<IUserManagement[]>;

@Injectable({ providedIn: 'root' })
export class UserManagementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-managements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userManagement: NewUserManagement): Observable<EntityResponseType> {
    return this.http.post<IUserManagement>(this.resourceUrl, userManagement, { observe: 'response' });
  }

  update(userManagement: IUserManagement): Observable<EntityResponseType> {
    return this.http.put<IUserManagement>(`${this.resourceUrl}/${this.getUserManagementIdentifier(userManagement)}`, userManagement, {
      observe: 'response',
    });
  }

  partialUpdate(userManagement: PartialUpdateUserManagement): Observable<EntityResponseType> {
    return this.http.patch<IUserManagement>(`${this.resourceUrl}/${this.getUserManagementIdentifier(userManagement)}`, userManagement, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserManagement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserManagement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserManagementIdentifier(userManagement: Pick<IUserManagement, 'id'>): number {
    return userManagement.id;
  }

  compareUserManagement(o1: Pick<IUserManagement, 'id'> | null, o2: Pick<IUserManagement, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserManagementIdentifier(o1) === this.getUserManagementIdentifier(o2) : o1 === o2;
  }

  addUserManagementToCollectionIfMissing<Type extends Pick<IUserManagement, 'id'>>(
    userManagementCollection: Type[],
    ...userManagementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userManagements: Type[] = userManagementsToCheck.filter(isPresent);
    if (userManagements.length > 0) {
      const userManagementCollectionIdentifiers = userManagementCollection.map(
        userManagementItem => this.getUserManagementIdentifier(userManagementItem)!
      );
      const userManagementsToAdd = userManagements.filter(userManagementItem => {
        const userManagementIdentifier = this.getUserManagementIdentifier(userManagementItem);
        if (userManagementCollectionIdentifiers.includes(userManagementIdentifier)) {
          return false;
        }
        userManagementCollectionIdentifiers.push(userManagementIdentifier);
        return true;
      });
      return [...userManagementsToAdd, ...userManagementCollection];
    }
    return userManagementCollection;
  }
}
