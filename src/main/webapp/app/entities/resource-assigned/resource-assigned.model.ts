import dayjs from 'dayjs/esm';
import { IResource } from 'app/entities/resource/resource.model';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';

export interface IResourceAssigned {
  id: number;
  callRecievedTime?: dayjs.Dayjs | null;
  onSceneTime?: dayjs.Dayjs | null;
  leftSceneTime?: dayjs.Dayjs | null;
  arrivedHospitalTime?: dayjs.Dayjs | null;
  clearHospitalTime?: dayjs.Dayjs | null;
  greenTime?: dayjs.Dayjs | null;
  unAssignedTime?: dayjs.Dayjs | null;
  resource?: Pick<IResource, 'id'> | null;
  emergencyCall?: Pick<IEmergencyCall, 'id'> | null;
}

export type NewResourceAssigned = Omit<IResourceAssigned, 'id'> & { id: null };
