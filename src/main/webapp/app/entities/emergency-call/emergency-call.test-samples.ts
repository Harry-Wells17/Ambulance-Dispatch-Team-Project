import dayjs from 'dayjs/esm';

import { CallCategory } from 'app/entities/enumerations/call-category.model';
import { Sex } from 'app/entities/enumerations/sex.model';

import { IEmergencyCall, NewEmergencyCall } from './emergency-call.model';

export const sampleWithRequiredData: IEmergencyCall = {
  id: 21798,
  created: dayjs('2024-03-04T06:10'),
  latitude: 85115,
  longitude: 87613,
};

export const sampleWithPartialData: IEmergencyCall = {
  id: 64173,
  created: dayjs('2024-03-03T22:38'),
  type: CallCategory['CAT4'],
  sexAssignedAtBirth: Sex['MALE'],
  history: 'Metal International',
  condition: 'Practical Borders Cotton',
  latitude: 34950,
  longitude: 13610,
};

export const sampleWithFullData: IEmergencyCall = {
  id: 65342,
  created: dayjs('2024-03-04T15:59'),
  open: false,
  type: CallCategory['CAT5'],
  age: 61856,
  sexAssignedAtBirth: Sex['FEMALE'],
  history: 'Ireland Ringgit panel',
  injuries: 'architect Metal',
  condition: 'Credit',
  latitude: 86231,
  longitude: 27363,
  closed: true,
};

export const sampleWithNewData: NewEmergencyCall = {
  created: dayjs('2024-03-04T07:03'),
  latitude: 56005,
  longitude: 5835,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
