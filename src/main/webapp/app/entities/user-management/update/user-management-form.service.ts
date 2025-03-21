import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IUserManagement, NewUserManagement } from '../user-management.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserManagement for edit and NewUserManagementFormGroupInput for create.
 */
type UserManagementFormGroupInput = IUserManagement | PartialWithRequiredKeyOf<NewUserManagement>;

type UserManagementFormDefaults = Pick<NewUserManagement, 'id'>;

type UserManagementFormGroupContent = {
  id: FormControl<IUserManagement['id'] | NewUserManagement['id']>;
  userRole: FormControl<IUserManagement['userRole']>;
  userPerms: FormControl<IUserManagement['userPerms']>;
};

export type UserManagementFormGroup = FormGroup<UserManagementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserManagementFormService {
  createUserManagementFormGroup(userManagement: UserManagementFormGroupInput = { id: null }): UserManagementFormGroup {
    const userManagementRawValue = {
      ...this.getFormDefaults(),
      ...userManagement,
    };
    return new FormGroup<UserManagementFormGroupContent>({
      id: new FormControl(
        { value: userManagementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      userRole: new FormControl(userManagementRawValue.userRole),
      userPerms: new FormControl(userManagementRawValue.userPerms),
    });
  }

  getUserManagement(form: UserManagementFormGroup): IUserManagement | NewUserManagement {
    return form.getRawValue() as IUserManagement | NewUserManagement;
  }

  resetForm(form: UserManagementFormGroup, userManagement: UserManagementFormGroupInput): void {
    const userManagementRawValue = { ...this.getFormDefaults(), ...userManagement };
    form.reset(
      {
        ...userManagementRawValue,
        id: { value: userManagementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserManagementFormDefaults {
    return {
      id: null,
    };
  }
}
