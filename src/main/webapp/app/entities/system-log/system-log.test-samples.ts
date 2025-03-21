import dayjs from 'dayjs/esm';

import { LogType } from 'app/entities/enumerations/log-type.model';

import { ISystemLog, NewSystemLog } from './system-log.model';

export const sampleWithRequiredData: ISystemLog = {
  id: 44591,
  createdAt: dayjs('2024-03-04T02:39'),
  logType: LogType['CONTROLROOM'],
  messageContent: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: ISystemLog = {
  id: 4317,
  createdAt: dayjs('2024-03-03T22:21'),
  logType: LogType['RESOURCE'],
  messageContent: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: ISystemLog = {
  id: 99154,
  createdAt: dayjs('2024-03-04T02:43'),
  logType: LogType['CALL'],
  messageContent: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewSystemLog = {
  createdAt: dayjs('2024-03-04T04:22'),
  logType: LogType['BREAKLOG'],
  messageContent: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
