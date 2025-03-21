import dayjs from 'dayjs/esm';

import { IResourceBreaks, NewResourceBreaks } from './resource-breaks.model';

export const sampleWithRequiredData: IResourceBreaks = {
  id: 92042,
  lastBreak: dayjs('2024-03-04T16:40'),
  breakRequested: true,
};

export const sampleWithPartialData: IResourceBreaks = {
  id: 17748,
  lastBreak: dayjs('2024-03-04T07:31'),
  breakRequested: false,
  startedBreak: dayjs('2024-03-04T14:54'),
  onBreak: false,
};

export const sampleWithFullData: IResourceBreaks = {
  id: 25458,
  lastBreak: dayjs('2024-03-03T23:48'),
  breakRequested: true,
  startedBreak: dayjs('2024-03-04T14:29'),
  onBreak: false,
};

export const sampleWithNewData: NewResourceBreaks = {
  lastBreak: dayjs('2024-03-03T17:52'),
  breakRequested: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
