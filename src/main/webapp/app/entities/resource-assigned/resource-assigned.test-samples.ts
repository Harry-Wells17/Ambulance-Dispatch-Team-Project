import dayjs from 'dayjs/esm';

import { IResourceAssigned, NewResourceAssigned } from './resource-assigned.model';

export const sampleWithRequiredData: IResourceAssigned = {
  id: 19609,
  callRecievedTime: dayjs('2024-03-04T12:14'),
};

export const sampleWithPartialData: IResourceAssigned = {
  id: 70571,
  callRecievedTime: dayjs('2024-03-04T01:00'),
  greenTime: dayjs('2024-03-04T14:41'),
};

export const sampleWithFullData: IResourceAssigned = {
  id: 57619,
  callRecievedTime: dayjs('2024-03-03T17:49'),
  onSceneTime: dayjs('2024-03-04T11:13'),
  leftSceneTime: dayjs('2024-03-04T14:34'),
  arrivedHospitalTime: dayjs('2024-03-04T09:21'),
  clearHospitalTime: dayjs('2024-03-04T00:39'),
  greenTime: dayjs('2024-03-03T22:36'),
  unAssignedTime: dayjs('2024-03-04T02:17'),
};

export const sampleWithNewData: NewResourceAssigned = {
  callRecievedTime: dayjs('2024-03-04T09:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
