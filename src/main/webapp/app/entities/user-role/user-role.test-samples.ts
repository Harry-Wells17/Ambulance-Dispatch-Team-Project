import { Role } from 'app/entities/enumerations/role.model';

import { IUserRole, NewUserRole } from './user-role.model';

export const sampleWithRequiredData: IUserRole = {
  id: 43156,
};

export const sampleWithPartialData: IUserRole = {
  id: 37235,
  role: Role['MANAGEMENT'],
};

export const sampleWithFullData: IUserRole = {
  id: 41293,
  role: Role['LOGGIST'],
};

export const sampleWithNewData: NewUserRole = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
