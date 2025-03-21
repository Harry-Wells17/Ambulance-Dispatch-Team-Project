import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResourceBreaks, NewResourceBreaks } from '../resource-breaks.model';

export type PartialUpdateResourceBreaks = Partial<IResourceBreaks> & Pick<IResourceBreaks, 'id'>;

type RestOf<T extends IResourceBreaks | NewResourceBreaks> = Omit<T, 'lastBreak' | 'startedBreak'> & {
  lastBreak?: string | null;
  startedBreak?: string | null;
};

export type RestResourceBreaks = RestOf<IResourceBreaks>;

export type NewRestResourceBreaks = RestOf<NewResourceBreaks>;

export type PartialUpdateRestResourceBreaks = RestOf<PartialUpdateResourceBreaks>;

export type EntityResponseType = HttpResponse<IResourceBreaks>;
export type EntityArrayResponseType = HttpResponse<IResourceBreaks[]>;

@Injectable({ providedIn: 'root' })
export class ResourceBreaksService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/resource-breaks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resourceBreaks: NewResourceBreaks): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resourceBreaks);
    return this.http
      .post<RestResourceBreaks>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(resourceBreaks: IResourceBreaks): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resourceBreaks);
    return this.http
      .put<RestResourceBreaks>(`${this.resourceUrl}/${this.getResourceBreaksIdentifier(resourceBreaks)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(resourceBreaks: PartialUpdateResourceBreaks): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resourceBreaks);
    return this.http
      .patch<RestResourceBreaks>(`${this.resourceUrl}/${this.getResourceBreaksIdentifier(resourceBreaks)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestResourceBreaks>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestResourceBreaks[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResourceBreaksIdentifier(resourceBreaks: Pick<IResourceBreaks, 'id'>): number {
    return resourceBreaks.id;
  }

  compareResourceBreaks(o1: Pick<IResourceBreaks, 'id'> | null, o2: Pick<IResourceBreaks, 'id'> | null): boolean {
    return o1 && o2 ? this.getResourceBreaksIdentifier(o1) === this.getResourceBreaksIdentifier(o2) : o1 === o2;
  }

  addResourceBreaksToCollectionIfMissing<Type extends Pick<IResourceBreaks, 'id'>>(
    resourceBreaksCollection: Type[],
    ...resourceBreaksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const resourceBreaks: Type[] = resourceBreaksToCheck.filter(isPresent);
    if (resourceBreaks.length > 0) {
      const resourceBreaksCollectionIdentifiers = resourceBreaksCollection.map(
        resourceBreaksItem => this.getResourceBreaksIdentifier(resourceBreaksItem)!
      );
      const resourceBreaksToAdd = resourceBreaks.filter(resourceBreaksItem => {
        const resourceBreaksIdentifier = this.getResourceBreaksIdentifier(resourceBreaksItem);
        if (resourceBreaksCollectionIdentifiers.includes(resourceBreaksIdentifier)) {
          return false;
        }
        resourceBreaksCollectionIdentifiers.push(resourceBreaksIdentifier);
        return true;
      });
      return [...resourceBreaksToAdd, ...resourceBreaksCollection];
    }
    return resourceBreaksCollection;
  }

  protected convertDateFromClient<T extends IResourceBreaks | NewResourceBreaks | PartialUpdateResourceBreaks>(
    resourceBreaks: T
  ): RestOf<T> {
    return {
      ...resourceBreaks,
      lastBreak: resourceBreaks.lastBreak?.toJSON() ?? null,
      startedBreak: resourceBreaks.startedBreak?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restResourceBreaks: RestResourceBreaks): IResourceBreaks {
    return {
      ...restResourceBreaks,
      lastBreak: restResourceBreaks.lastBreak ? dayjs(restResourceBreaks.lastBreak) : undefined,
      startedBreak: restResourceBreaks.startedBreak ? dayjs(restResourceBreaks.startedBreak) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestResourceBreaks>): HttpResponse<IResourceBreaks> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestResourceBreaks[]>): HttpResponse<IResourceBreaks[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
