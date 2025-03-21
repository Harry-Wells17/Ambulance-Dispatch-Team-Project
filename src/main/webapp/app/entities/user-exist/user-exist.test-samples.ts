import { IUserExist, NewUserExist } from './user-exist.model';

export const sampleWithRequiredData: IUserExist = {
  id: 25066,
};

export const sampleWithPartialData: IUserExist = {
  id: 28254,
  exist: true,
};

export const sampleWithFullData: IUserExist = {
  id: 80972,
  exist: false,
};

export const sampleWithNewData: NewUserExist = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
