import dayjs from 'dayjs/esm';
import { IResourceBreaks } from 'app/entities/resource-breaks/resource-breaks.model';
import { IEvent } from 'app/entities/event/event.model';
import { ResourceType } from 'app/entities/enumerations/resource-type.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IResource {
  id: number;
  created?: dayjs.Dayjs | null;
  type?: ResourceType | null;
  status?: Status | null;
  callSign?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  resourceBreak?: Pick<IResourceBreaks, 'id'> | null;
  event?: Pick<IEvent, 'id'> | null;
}

export type NewResource = Omit<IResource, 'id'> & { id: null };
