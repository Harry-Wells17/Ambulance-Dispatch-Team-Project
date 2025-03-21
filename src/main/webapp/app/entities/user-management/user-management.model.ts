import { IUserRole } from 'app/entities/user-role/user-role.model';
import { IUserPerms } from 'app/entities/user-perms/user-perms.model';

export interface IUserManagement {
  id: number;
  userRole?: Pick<IUserRole, 'id'> | null;
  userPerms?: Pick<IUserPerms, 'id'> | null;
}

export type NewUserManagement = Omit<IUserManagement, 'id'> & { id: null };
