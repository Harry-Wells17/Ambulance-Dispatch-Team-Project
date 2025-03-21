import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserExist, NewUserExist } from '../user-exist.model';

export type PartialUpdateUserExist = Partial<IUserExist> & Pick<IUserExist, 'id'>;

export type EntityResponseType = HttpResponse<IUserExist>;
export type EntityArrayResponseType = HttpResponse<IUserExist[]>;

@Injectable({ providedIn: 'root' })
export class UserExistService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-exists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userExist: NewUserExist): Observable<EntityResponseType> {
    return this.http.post<IUserExist>(this.resourceUrl, userExist, { observe: 'response' });
  }

  update(userExist: IUserExist): Observable<EntityResponseType> {
    return this.http.put<IUserExist>(`${this.resourceUrl}/${this.getUserExistIdentifier(userExist)}`, userExist, { observe: 'response' });
  }

  partialUpdate(userExist: PartialUpdateUserExist): Observable<EntityResponseType> {
    return this.http.patch<IUserExist>(`${this.resourceUrl}/${this.getUserExistIdentifier(userExist)}`, userExist, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserExist>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserExist[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserExistIdentifier(userExist: Pick<IUserExist, 'id'>): number {
    return userExist.id;
  }

  compareUserExist(o1: Pick<IUserExist, 'id'> | null, o2: Pick<IUserExist, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserExistIdentifier(o1) === this.getUserExistIdentifier(o2) : o1 === o2;
  }

  addUserExistToCollectionIfMissing<Type extends Pick<IUserExist, 'id'>>(
    userExistCollection: Type[],
    ...userExistsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userExists: Type[] = userExistsToCheck.filter(isPresent);
    if (userExists.length > 0) {
      const userExistCollectionIdentifiers = userExistCollection.map(userExistItem => this.getUserExistIdentifier(userExistItem)!);
      const userExistsToAdd = userExists.filter(userExistItem => {
        const userExistIdentifier = this.getUserExistIdentifier(userExistItem);
        if (userExistCollectionIdentifiers.includes(userExistIdentifier)) {
          return false;
        }
        userExistCollectionIdentifiers.push(userExistIdentifier);
        return true;
      });
      return [...userExistsToAdd, ...userExistCollection];
    }
    return userExistCollection;
  }
}
