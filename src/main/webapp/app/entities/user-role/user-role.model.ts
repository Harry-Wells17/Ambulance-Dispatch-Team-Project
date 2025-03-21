import { Role } from 'app/entities/enumerations/role.model';

export interface IUserRole {
  id: number;
  role?: Role | null;
}

export type NewUserRole = Omit<IUserRole, 'id'> & { id: null };
