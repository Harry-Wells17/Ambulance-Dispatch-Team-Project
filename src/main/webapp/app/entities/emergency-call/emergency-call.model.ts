import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IEvent } from 'app/entities/event/event.model';
import { CallCategory } from 'app/entities/enumerations/call-category.model';
import { Sex } from 'app/entities/enumerations/sex.model';

export interface IEmergencyCall {
  id: number;
  created?: dayjs.Dayjs | null;
  open?: boolean | null;
  type?: CallCategory | null;
  age?: number | null;
  sexAssignedAtBirth?: Sex | null;
  history?: string | null;
  injuries?: string | null;
  condition?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  closed?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  event?: Pick<IEvent, 'id'> | null;
}

export type NewEmergencyCall = Omit<IEmergencyCall, 'id'> & { id: null };
