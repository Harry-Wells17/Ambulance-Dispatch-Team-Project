import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IUserExist, NewUserExist } from '../user-exist.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserExist for edit and NewUserExistFormGroupInput for create.
 */
type UserExistFormGroupInput = IUserExist | PartialWithRequiredKeyOf<NewUserExist>;

type UserExistFormDefaults = Pick<NewUserExist, 'id' | 'exist'>;

type UserExistFormGroupContent = {
  id: FormControl<IUserExist['id'] | NewUserExist['id']>;
  exist: FormControl<IUserExist['exist']>;
  createdBy: FormControl<IUserExist['createdBy']>;
};

export type UserExistFormGroup = FormGroup<UserExistFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserExistFormService {
  createUserExistFormGroup(userExist: UserExistFormGroupInput = { id: null }): UserExistFormGroup {
    const userExistRawValue = {
      ...this.getFormDefaults(),
      ...userExist,
    };
    return new FormGroup<UserExistFormGroupContent>({
      id: new FormControl(
        { value: userExistRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      exist: new FormControl(userExistRawValue.exist),
      createdBy: new FormControl(userExistRawValue.createdBy),
    });
  }

  getUserExist(form: UserExistFormGroup): IUserExist | NewUserExist {
    return form.getRawValue() as IUserExist | NewUserExist;
  }

  resetForm(form: UserExistFormGroup, userExist: UserExistFormGroupInput): void {
    const userExistRawValue = { ...this.getFormDefaults(), ...userExist };
    form.reset(
      {
        ...userExistRawValue,
        id: { value: userExistRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserExistFormDefaults {
    return {
      id: null,
      exist: false,
    };
  }
}
