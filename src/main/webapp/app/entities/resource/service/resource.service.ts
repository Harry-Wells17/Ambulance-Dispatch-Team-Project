import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResource, NewResource } from '../resource.model';

export type PartialUpdateResource = Partial<IResource> & Pick<IResource, 'id'>;

type RestOf<T extends IResource | NewResource> = Omit<T, 'created'> & {
  created?: string | null;
};

export type RestResource = RestOf<IResource>;

export type NewRestResource = RestOf<NewResource>;

export type PartialUpdateRestResource = RestOf<PartialUpdateResource>;

export type EntityResponseType = HttpResponse<IResource>;
export type EntityArrayResponseType = HttpResponse<IResource[]>;

@Injectable({ providedIn: 'root' })
export class ResourceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/resources');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resource: NewResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resource);
    return this.http
      .post<RestResource>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(resource: IResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resource);
    return this.http
      .put<RestResource>(`${this.resourceUrl}/${this.getResourceIdentifier(resource)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(resource: PartialUpdateResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resource);
    return this.http
      .patch<RestResource>(`${this.resourceUrl}/${this.getResourceIdentifier(resource)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestResource>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestResource[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResourceIdentifier(resource: Pick<IResource, 'id'>): number {
    return resource.id;
  }

  compareResource(o1: Pick<IResource, 'id'> | null, o2: Pick<IResource, 'id'> | null): boolean {
    return o1 && o2 ? this.getResourceIdentifier(o1) === this.getResourceIdentifier(o2) : o1 === o2;
  }

  addResourceToCollectionIfMissing<Type extends Pick<IResource, 'id'>>(
    resourceCollection: Type[],
    ...resourcesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const resources: Type[] = resourcesToCheck.filter(isPresent);
    if (resources.length > 0) {
      const resourceCollectionIdentifiers = resourceCollection.map(resourceItem => this.getResourceIdentifier(resourceItem)!);
      const resourcesToAdd = resources.filter(resourceItem => {
        const resourceIdentifier = this.getResourceIdentifier(resourceItem);
        if (resourceCollectionIdentifiers.includes(resourceIdentifier)) {
          return false;
        }
        resourceCollectionIdentifiers.push(resourceIdentifier);
        return true;
      });
      return [...resourcesToAdd, ...resourceCollection];
    }
    return resourceCollection;
  }

  protected convertDateFromClient<T extends IResource | NewResource | PartialUpdateResource>(resource: T): RestOf<T> {
    return {
      ...resource,
      created: resource.created?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restResource: RestResource): IResource {
    return {
      ...restResource,
      created: restResource.created ? dayjs(restResource.created) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestResource>): HttpResponse<IResource> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestResource[]>): HttpResponse<IResource[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
