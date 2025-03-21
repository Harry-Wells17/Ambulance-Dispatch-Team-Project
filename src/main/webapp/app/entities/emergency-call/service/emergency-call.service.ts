import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEmergencyCall, NewEmergencyCall } from '../emergency-call.model';

export type PartialUpdateEmergencyCall = Partial<IEmergencyCall> & Pick<IEmergencyCall, 'id'>;

type RestOf<T extends IEmergencyCall | NewEmergencyCall> = Omit<T, 'created'> & {
  created?: string | null;
};

export type RestEmergencyCall = RestOf<IEmergencyCall>;

export type NewRestEmergencyCall = RestOf<NewEmergencyCall>;

export type PartialUpdateRestEmergencyCall = RestOf<PartialUpdateEmergencyCall>;

export type EntityResponseType = HttpResponse<IEmergencyCall>;
export type EntityArrayResponseType = HttpResponse<IEmergencyCall[]>;

@Injectable({ providedIn: 'root' })
export class EmergencyCallService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/emergency-calls');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(emergencyCall: NewEmergencyCall): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(emergencyCall);
    return this.http
      .post<RestEmergencyCall>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(emergencyCall: IEmergencyCall): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(emergencyCall);
    return this.http
      .put<RestEmergencyCall>(`${this.resourceUrl}/${this.getEmergencyCallIdentifier(emergencyCall)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(emergencyCall: PartialUpdateEmergencyCall): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(emergencyCall);
    return this.http
      .patch<RestEmergencyCall>(`${this.resourceUrl}/${this.getEmergencyCallIdentifier(emergencyCall)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEmergencyCall>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEmergencyCall[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEmergencyCallIdentifier(emergencyCall: Pick<IEmergencyCall, 'id'>): number {
    return emergencyCall.id;
  }

  compareEmergencyCall(o1: Pick<IEmergencyCall, 'id'> | null, o2: Pick<IEmergencyCall, 'id'> | null): boolean {
    return o1 && o2 ? this.getEmergencyCallIdentifier(o1) === this.getEmergencyCallIdentifier(o2) : o1 === o2;
  }

  addEmergencyCallToCollectionIfMissing<Type extends Pick<IEmergencyCall, 'id'>>(
    emergencyCallCollection: Type[],
    ...emergencyCallsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const emergencyCalls: Type[] = emergencyCallsToCheck.filter(isPresent);
    if (emergencyCalls.length > 0) {
      const emergencyCallCollectionIdentifiers = emergencyCallCollection.map(
        emergencyCallItem => this.getEmergencyCallIdentifier(emergencyCallItem)!
      );
      const emergencyCallsToAdd = emergencyCalls.filter(emergencyCallItem => {
        const emergencyCallIdentifier = this.getEmergencyCallIdentifier(emergencyCallItem);
        if (emergencyCallCollectionIdentifiers.includes(emergencyCallIdentifier)) {
          return false;
        }
        emergencyCallCollectionIdentifiers.push(emergencyCallIdentifier);
        return true;
      });
      return [...emergencyCallsToAdd, ...emergencyCallCollection];
    }
    return emergencyCallCollection;
  }

  protected convertDateFromClient<T extends IEmergencyCall | NewEmergencyCall | PartialUpdateEmergencyCall>(emergencyCall: T): RestOf<T> {
    return {
      ...emergencyCall,
      created: emergencyCall.created?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEmergencyCall: RestEmergencyCall): IEmergencyCall {
    return {
      ...restEmergencyCall,
      created: restEmergencyCall.created ? dayjs(restEmergencyCall.created) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEmergencyCall>): HttpResponse<IEmergencyCall> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEmergencyCall[]>): HttpResponse<IEmergencyCall[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
