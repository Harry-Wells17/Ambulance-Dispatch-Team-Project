import dayjs from 'dayjs/esm';

import { ResourceType } from 'app/entities/enumerations/resource-type.model';
import { Status } from 'app/entities/enumerations/status.model';

import { IResource, NewResource } from './resource.model';

export const sampleWithRequiredData: IResource = {
  id: 29528,
  created: dayjs('2024-03-04T07:37'),
  type: ResourceType['LOGISTICS'],
  status: Status['BREAK'],
  callSign: 'enable Orchestrator',
  latitude: 96574,
  longitude: 40730,
};

export const sampleWithPartialData: IResource = {
  id: 11742,
  created: dayjs('2024-03-04T06:08'),
  type: ResourceType['AMBULANCE'],
  status: Status['GREEN'],
  callSign: 'Configuration quantify',
  latitude: 82316,
  longitude: 73627,
};

export const sampleWithFullData: IResource = {
  id: 88598,
  created: dayjs('2024-03-04T06:14'),
  type: ResourceType['TREATMENTCENTRE'],
  status: Status['RED'],
  callSign: 'Granite Loan',
  latitude: 60726,
  longitude: 84650,
};

export const sampleWithNewData: NewResource = {
  created: dayjs('2024-03-04T11:44'),
  type: ResourceType['TREATMENTCENTRE'],
  status: Status['CLEAR'],
  callSign: 'Borders deposit Kwacha',
  latitude: 91338,
  longitude: 54180,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
