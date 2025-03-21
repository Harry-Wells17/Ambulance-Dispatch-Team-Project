import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResourceAssigned, NewResourceAssigned } from '../resource-assigned.model';

export type PartialUpdateResourceAssigned = Partial<IResourceAssigned> & Pick<IResourceAssigned, 'id'>;

type RestOf<T extends IResourceAssigned | NewResourceAssigned> = Omit<
  T,
  'callRecievedTime' | 'onSceneTime' | 'leftSceneTime' | 'arrivedHospitalTime' | 'clearHospitalTime' | 'greenTime' | 'unAssignedTime'
> & {
  callRecievedTime?: string | null;
  onSceneTime?: string | null;
  leftSceneTime?: string | null;
  arrivedHospitalTime?: string | null;
  clearHospitalTime?: string | null;
  greenTime?: string | null;
  unAssignedTime?: string | null;
};

export type RestResourceAssigned = RestOf<IResourceAssigned>;

export type NewRestResourceAssigned = RestOf<NewResourceAssigned>;

export type PartialUpdateRestResourceAssigned = RestOf<PartialUpdateResourceAssigned>;

export type EntityResponseType = HttpResponse<IResourceAssigned>;
export type EntityArrayResponseType = HttpResponse<IResourceAssigned[]>;

@Injectable({ providedIn: 'root' })
export class ResourceAssignedService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/resource-assigneds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resourceAssigned: NewResourceAssigned): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resourceAssigned);
    return this.http
      .post<RestResourceAssigned>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(resourceAssigned: IResourceAssigned): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resourceAssigned);
    return this.http
      .put<RestResourceAssigned>(`${this.resourceUrl}/${this.getResourceAssignedIdentifier(resourceAssigned)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(resourceAssigned: PartialUpdateResourceAssigned): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resourceAssigned);
    return this.http
      .patch<RestResourceAssigned>(`${this.resourceUrl}/${this.getResourceAssignedIdentifier(resourceAssigned)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestResourceAssigned>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestResourceAssigned[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResourceAssignedIdentifier(resourceAssigned: Pick<IResourceAssigned, 'id'>): number {
    return resourceAssigned.id;
  }

  compareResourceAssigned(o1: Pick<IResourceAssigned, 'id'> | null, o2: Pick<IResourceAssigned, 'id'> | null): boolean {
    return o1 && o2 ? this.getResourceAssignedIdentifier(o1) === this.getResourceAssignedIdentifier(o2) : o1 === o2;
  }

  addResourceAssignedToCollectionIfMissing<Type extends Pick<IResourceAssigned, 'id'>>(
    resourceAssignedCollection: Type[],
    ...resourceAssignedsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const resourceAssigneds: Type[] = resourceAssignedsToCheck.filter(isPresent);
    if (resourceAssigneds.length > 0) {
      const resourceAssignedCollectionIdentifiers = resourceAssignedCollection.map(
        resourceAssignedItem => this.getResourceAssignedIdentifier(resourceAssignedItem)!
      );
      const resourceAssignedsToAdd = resourceAssigneds.filter(resourceAssignedItem => {
        const resourceAssignedIdentifier = this.getResourceAssignedIdentifier(resourceAssignedItem);
        if (resourceAssignedCollectionIdentifiers.includes(resourceAssignedIdentifier)) {
          return false;
        }
        resourceAssignedCollectionIdentifiers.push(resourceAssignedIdentifier);
        return true;
      });
      return [...resourceAssignedsToAdd, ...resourceAssignedCollection];
    }
    return resourceAssignedCollection;
  }

  protected convertDateFromClient<T extends IResourceAssigned | NewResourceAssigned | PartialUpdateResourceAssigned>(
    resourceAssigned: T
  ): RestOf<T> {
    return {
      ...resourceAssigned,
      callRecievedTime: resourceAssigned.callRecievedTime?.toJSON() ?? null,
      onSceneTime: resourceAssigned.onSceneTime?.toJSON() ?? null,
      leftSceneTime: resourceAssigned.leftSceneTime?.toJSON() ?? null,
      arrivedHospitalTime: resourceAssigned.arrivedHospitalTime?.toJSON() ?? null,
      clearHospitalTime: resourceAssigned.clearHospitalTime?.toJSON() ?? null,
      greenTime: resourceAssigned.greenTime?.toJSON() ?? null,
      unAssignedTime: resourceAssigned.unAssignedTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restResourceAssigned: RestResourceAssigned): IResourceAssigned {
    return {
      ...restResourceAssigned,
      callRecievedTime: restResourceAssigned.callRecievedTime ? dayjs(restResourceAssigned.callRecievedTime) : undefined,
      onSceneTime: restResourceAssigned.onSceneTime ? dayjs(restResourceAssigned.onSceneTime) : undefined,
      leftSceneTime: restResourceAssigned.leftSceneTime ? dayjs(restResourceAssigned.leftSceneTime) : undefined,
      arrivedHospitalTime: restResourceAssigned.arrivedHospitalTime ? dayjs(restResourceAssigned.arrivedHospitalTime) : undefined,
      clearHospitalTime: restResourceAssigned.clearHospitalTime ? dayjs(restResourceAssigned.clearHospitalTime) : undefined,
      greenTime: restResourceAssigned.greenTime ? dayjs(restResourceAssigned.greenTime) : undefined,
      unAssignedTime: restResourceAssigned.unAssignedTime ? dayjs(restResourceAssigned.unAssignedTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestResourceAssigned>): HttpResponse<IResourceAssigned> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestResourceAssigned[]>): HttpResponse<IResourceAssigned[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
