import { IUserManagement, NewUserManagement } from './user-management.model';

export const sampleWithRequiredData: IUserManagement = {
  id: 34516,
};

export const sampleWithPartialData: IUserManagement = {
  id: 70338,
};

export const sampleWithFullData: IUserManagement = {
  id: 65461,
};

export const sampleWithNewData: NewUserManagement = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
