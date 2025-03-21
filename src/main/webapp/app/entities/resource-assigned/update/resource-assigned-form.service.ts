import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IResourceAssigned, NewResourceAssigned } from '../resource-assigned.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResourceAssigned for edit and NewResourceAssignedFormGroupInput for create.
 */
type ResourceAssignedFormGroupInput = IResourceAssigned | PartialWithRequiredKeyOf<NewResourceAssigned>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IResourceAssigned | NewResourceAssigned> = Omit<
  T,
  'callRecievedTime' | 'onSceneTime' | 'leftSceneTime' | 'arrivedHospitalTime' | 'clearHospitalTime' | 'greenTime' | 'unAssignedTime'
> & {
  callRecievedTime?: string | null;
  onSceneTime?: string | null;
  leftSceneTime?: string | null;
  arrivedHospitalTime?: string | null;
  clearHospitalTime?: string | null;
  greenTime?: string | null;
  unAssignedTime?: string | null;
};

type ResourceAssignedFormRawValue = FormValueOf<IResourceAssigned>;

type NewResourceAssignedFormRawValue = FormValueOf<NewResourceAssigned>;

type ResourceAssignedFormDefaults = Pick<
  NewResourceAssigned,
  'id' | 'callRecievedTime' | 'onSceneTime' | 'leftSceneTime' | 'arrivedHospitalTime' | 'clearHospitalTime' | 'greenTime' | 'unAssignedTime'
>;

type ResourceAssignedFormGroupContent = {
  id: FormControl<ResourceAssignedFormRawValue['id'] | NewResourceAssigned['id']>;
  callRecievedTime: FormControl<ResourceAssignedFormRawValue['callRecievedTime']>;
  onSceneTime: FormControl<ResourceAssignedFormRawValue['onSceneTime']>;
  leftSceneTime: FormControl<ResourceAssignedFormRawValue['leftSceneTime']>;
  arrivedHospitalTime: FormControl<ResourceAssignedFormRawValue['arrivedHospitalTime']>;
  clearHospitalTime: FormControl<ResourceAssignedFormRawValue['clearHospitalTime']>;
  greenTime: FormControl<ResourceAssignedFormRawValue['greenTime']>;
  unAssignedTime: FormControl<ResourceAssignedFormRawValue['unAssignedTime']>;
  resource: FormControl<ResourceAssignedFormRawValue['resource']>;
  emergencyCall: FormControl<ResourceAssignedFormRawValue['emergencyCall']>;
};

export type ResourceAssignedFormGroup = FormGroup<ResourceAssignedFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResourceAssignedFormService {
  createResourceAssignedFormGroup(resourceAssigned: ResourceAssignedFormGroupInput = { id: null }): ResourceAssignedFormGroup {
    const resourceAssignedRawValue = this.convertResourceAssignedToResourceAssignedRawValue({
      ...this.getFormDefaults(),
      ...resourceAssigned,
    });
    return new FormGroup<ResourceAssignedFormGroupContent>({
      id: new FormControl(
        { value: resourceAssignedRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      callRecievedTime: new FormControl(resourceAssignedRawValue.callRecievedTime, {
        validators: [Validators.required],
      }),
      onSceneTime: new FormControl(resourceAssignedRawValue.onSceneTime),
      leftSceneTime: new FormControl(resourceAssignedRawValue.leftSceneTime),
      arrivedHospitalTime: new FormControl(resourceAssignedRawValue.arrivedHospitalTime),
      clearHospitalTime: new FormControl(resourceAssignedRawValue.clearHospitalTime),
      greenTime: new FormControl(resourceAssignedRawValue.greenTime),
      unAssignedTime: new FormControl(resourceAssignedRawValue.unAssignedTime),
      resource: new FormControl(resourceAssignedRawValue.resource),
      emergencyCall: new FormControl(resourceAssignedRawValue.emergencyCall),
    });
  }

  getResourceAssigned(form: ResourceAssignedFormGroup): IResourceAssigned | NewResourceAssigned {
    return this.convertResourceAssignedRawValueToResourceAssigned(
      form.getRawValue() as ResourceAssignedFormRawValue | NewResourceAssignedFormRawValue
    );
  }

  resetForm(form: ResourceAssignedFormGroup, resourceAssigned: ResourceAssignedFormGroupInput): void {
    const resourceAssignedRawValue = this.convertResourceAssignedToResourceAssignedRawValue({
      ...this.getFormDefaults(),
      ...resourceAssigned,
    });
    form.reset(
      {
        ...resourceAssignedRawValue,
        id: { value: resourceAssignedRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ResourceAssignedFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      callRecievedTime: currentTime,
      onSceneTime: currentTime,
      leftSceneTime: currentTime,
      arrivedHospitalTime: currentTime,
      clearHospitalTime: currentTime,
      greenTime: currentTime,
      unAssignedTime: currentTime,
    };
  }

  private convertResourceAssignedRawValueToResourceAssigned(
    rawResourceAssigned: ResourceAssignedFormRawValue | NewResourceAssignedFormRawValue
  ): IResourceAssigned | NewResourceAssigned {
    return {
      ...rawResourceAssigned,
      callRecievedTime: dayjs(rawResourceAssigned.callRecievedTime, DATE_TIME_FORMAT),
      onSceneTime: dayjs(rawResourceAssigned.onSceneTime, DATE_TIME_FORMAT),
      leftSceneTime: dayjs(rawResourceAssigned.leftSceneTime, DATE_TIME_FORMAT),
      arrivedHospitalTime: dayjs(rawResourceAssigned.arrivedHospitalTime, DATE_TIME_FORMAT),
      clearHospitalTime: dayjs(rawResourceAssigned.clearHospitalTime, DATE_TIME_FORMAT),
      greenTime: dayjs(rawResourceAssigned.greenTime, DATE_TIME_FORMAT),
      unAssignedTime: dayjs(rawResourceAssigned.unAssignedTime, DATE_TIME_FORMAT),
    };
  }

  private convertResourceAssignedToResourceAssignedRawValue(
    resourceAssigned: IResourceAssigned | (Partial<NewResourceAssigned> & ResourceAssignedFormDefaults)
  ): ResourceAssignedFormRawValue | PartialWithRequiredKeyOf<NewResourceAssignedFormRawValue> {
    return {
      ...resourceAssigned,
      callRecievedTime: resourceAssigned.callRecievedTime ? resourceAssigned.callRecievedTime.format(DATE_TIME_FORMAT) : undefined,
      onSceneTime: resourceAssigned.onSceneTime ? resourceAssigned.onSceneTime.format(DATE_TIME_FORMAT) : undefined,
      leftSceneTime: resourceAssigned.leftSceneTime ? resourceAssigned.leftSceneTime.format(DATE_TIME_FORMAT) : undefined,
      arrivedHospitalTime: resourceAssigned.arrivedHospitalTime ? resourceAssigned.arrivedHospitalTime.format(DATE_TIME_FORMAT) : undefined,
      clearHospitalTime: resourceAssigned.clearHospitalTime ? resourceAssigned.clearHospitalTime.format(DATE_TIME_FORMAT) : undefined,
      greenTime: resourceAssigned.greenTime ? resourceAssigned.greenTime.format(DATE_TIME_FORMAT) : undefined,
      unAssignedTime: resourceAssigned.unAssignedTime ? resourceAssigned.unAssignedTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
