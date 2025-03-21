import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { IEvent } from 'app/entities/event/event.model';
import { LogType } from 'app/entities/enumerations/log-type.model';

export interface ISystemLog {
  id: number;
  createdAt?: dayjs.Dayjs | null;
  logType?: LogType | null;
  messageContent?: string | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  emergencyCall?: Pick<IEmergencyCall, 'id'> | null;
  event?: Pick<IEvent, 'id'> | null;
}

export type NewSystemLog = Omit<ISystemLog, 'id'> & { id: null };
