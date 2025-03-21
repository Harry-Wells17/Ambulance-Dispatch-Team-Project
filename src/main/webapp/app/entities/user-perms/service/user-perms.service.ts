import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserPerms, NewUserPerms } from '../user-perms.model';

export type PartialUpdateUserPerms = Partial<IUserPerms> & Pick<IUserPerms, 'id'>;

export type EntityResponseType = HttpResponse<IUserPerms>;
export type EntityArrayResponseType = HttpResponse<IUserPerms[]>;

@Injectable({ providedIn: 'root' })
export class UserPermsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-perms');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userPerms: NewUserPerms): Observable<EntityResponseType> {
    return this.http.post<IUserPerms>(this.resourceUrl, userPerms, { observe: 'response' });
  }

  update(userPerms: IUserPerms): Observable<EntityResponseType> {
    return this.http.put<IUserPerms>(`${this.resourceUrl}/${this.getUserPermsIdentifier(userPerms)}`, userPerms, { observe: 'response' });
  }

  partialUpdate(userPerms: PartialUpdateUserPerms): Observable<EntityResponseType> {
    return this.http.patch<IUserPerms>(`${this.resourceUrl}/${this.getUserPermsIdentifier(userPerms)}`, userPerms, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserPerms>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserPerms[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserPermsIdentifier(userPerms: Pick<IUserPerms, 'id'>): number {
    return userPerms.id;
  }

  compareUserPerms(o1: Pick<IUserPerms, 'id'> | null, o2: Pick<IUserPerms, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserPermsIdentifier(o1) === this.getUserPermsIdentifier(o2) : o1 === o2;
  }

  addUserPermsToCollectionIfMissing<Type extends Pick<IUserPerms, 'id'>>(
    userPermsCollection: Type[],
    ...userPermsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userPerms: Type[] = userPermsToCheck.filter(isPresent);
    if (userPerms.length > 0) {
      const userPermsCollectionIdentifiers = userPermsCollection.map(userPermsItem => this.getUserPermsIdentifier(userPermsItem)!);
      const userPermsToAdd = userPerms.filter(userPermsItem => {
        const userPermsIdentifier = this.getUserPermsIdentifier(userPermsItem);
        if (userPermsCollectionIdentifiers.includes(userPermsIdentifier)) {
          return false;
        }
        userPermsCollectionIdentifiers.push(userPermsIdentifier);
        return true;
      });
      return [...userPermsToAdd, ...userPermsCollection];
    }
    return userPermsCollection;
  }
}
