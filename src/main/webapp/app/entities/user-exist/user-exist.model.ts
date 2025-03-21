import { IUser } from 'app/entities/user/user.model';

export interface IUserExist {
  id: number;
  exist?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewUserExist = Omit<IUserExist, 'id'> & { id: null };
