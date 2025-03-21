import { Perms } from 'app/entities/enumerations/perms.model';

import { IUserPerms, NewUserPerms } from './user-perms.model';

export const sampleWithRequiredData: IUserPerms = {
  id: 4895,
  perms: Perms['CREATE_CALL'],
};

export const sampleWithPartialData: IUserPerms = {
  id: 71136,
  perms: Perms['ADMIN'],
};

export const sampleWithFullData: IUserPerms = {
  id: 8786,
  perms: Perms['ADMIN'],
};

export const sampleWithNewData: NewUserPerms = {
  perms: Perms['ADDLOG'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
