import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IUserPerms, NewUserPerms } from '../user-perms.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserPerms for edit and NewUserPermsFormGroupInput for create.
 */
type UserPermsFormGroupInput = IUserPerms | PartialWithRequiredKeyOf<NewUserPerms>;

type UserPermsFormDefaults = Pick<NewUserPerms, 'id'>;

type UserPermsFormGroupContent = {
  id: FormControl<IUserPerms['id'] | NewUserPerms['id']>;
  perms: FormControl<IUserPerms['perms']>;
};

export type UserPermsFormGroup = FormGroup<UserPermsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserPermsFormService {
  createUserPermsFormGroup(userPerms: UserPermsFormGroupInput = { id: null }): UserPermsFormGroup {
    const userPermsRawValue = {
      ...this.getFormDefaults(),
      ...userPerms,
    };
    return new FormGroup<UserPermsFormGroupContent>({
      id: new FormControl(
        { value: userPermsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      perms: new FormControl(userPermsRawValue.perms, {
        validators: [Validators.required],
      }),
    });
  }

  getUserPerms(form: UserPermsFormGroup): IUserPerms | NewUserPerms {
    return form.getRawValue() as IUserPerms | NewUserPerms;
  }

  resetForm(form: UserPermsFormGroup, userPerms: UserPermsFormGroupInput): void {
    const userPermsRawValue = { ...this.getFormDefaults(), ...userPerms };
    form.reset(
      {
        ...userPermsRawValue,
        id: { value: userPermsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserPermsFormDefaults {
    return {
      id: null,
    };
  }
}
