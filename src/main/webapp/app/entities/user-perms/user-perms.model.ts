import { Perms } from 'app/entities/enumerations/perms.model';

export interface IUserPerms {
  id: number;
  perms?: Perms | null;
}

export type NewUserPerms = Omit<IUserPerms, 'id'> & { id: null };
