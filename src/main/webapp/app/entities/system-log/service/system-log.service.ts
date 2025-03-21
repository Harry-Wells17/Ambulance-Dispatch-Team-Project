import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISystemLog, NewSystemLog } from '../system-log.model';

export type PartialUpdateSystemLog = Partial<ISystemLog> & Pick<ISystemLog, 'id'>;

type RestOf<T extends ISystemLog | NewSystemLog> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestSystemLog = RestOf<ISystemLog>;

export type NewRestSystemLog = RestOf<NewSystemLog>;

export type PartialUpdateRestSystemLog = RestOf<PartialUpdateSystemLog>;

export type EntityResponseType = HttpResponse<ISystemLog>;
export type EntityArrayResponseType = HttpResponse<ISystemLog[]>;

@Injectable({ providedIn: 'root' })
export class SystemLogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/system-logs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(systemLog: NewSystemLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(systemLog);
    return this.http
      .post<RestSystemLog>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(systemLog: ISystemLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(systemLog);
    return this.http
      .put<RestSystemLog>(`${this.resourceUrl}/${this.getSystemLogIdentifier(systemLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(systemLog: PartialUpdateSystemLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(systemLog);
    return this.http
      .patch<RestSystemLog>(`${this.resourceUrl}/${this.getSystemLogIdentifier(systemLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSystemLog>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSystemLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSystemLogIdentifier(systemLog: Pick<ISystemLog, 'id'>): number {
    return systemLog.id;
  }

  compareSystemLog(o1: Pick<ISystemLog, 'id'> | null, o2: Pick<ISystemLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getSystemLogIdentifier(o1) === this.getSystemLogIdentifier(o2) : o1 === o2;
  }

  addSystemLogToCollectionIfMissing<Type extends Pick<ISystemLog, 'id'>>(
    systemLogCollection: Type[],
    ...systemLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const systemLogs: Type[] = systemLogsToCheck.filter(isPresent);
    if (systemLogs.length > 0) {
      const systemLogCollectionIdentifiers = systemLogCollection.map(systemLogItem => this.getSystemLogIdentifier(systemLogItem)!);
      const systemLogsToAdd = systemLogs.filter(systemLogItem => {
        const systemLogIdentifier = this.getSystemLogIdentifier(systemLogItem);
        if (systemLogCollectionIdentifiers.includes(systemLogIdentifier)) {
          return false;
        }
        systemLogCollectionIdentifiers.push(systemLogIdentifier);
        return true;
      });
      return [...systemLogsToAdd, ...systemLogCollection];
    }
    return systemLogCollection;
  }

  protected convertDateFromClient<T extends ISystemLog | NewSystemLog | PartialUpdateSystemLog>(systemLog: T): RestOf<T> {
    return {
      ...systemLog,
      createdAt: systemLog.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSystemLog: RestSystemLog): ISystemLog {
    return {
      ...restSystemLog,
      createdAt: restSystemLog.createdAt ? dayjs(restSystemLog.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSystemLog>): HttpResponse<ISystemLog> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSystemLog[]>): HttpResponse<ISystemLog[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
