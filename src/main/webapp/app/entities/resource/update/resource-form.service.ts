import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IResource, NewResource } from '../resource.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResource for edit and NewResourceFormGroupInput for create.
 */
type ResourceFormGroupInput = IResource | PartialWithRequiredKeyOf<NewResource>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IResource | NewResource> = Omit<T, 'created'> & {
  created?: string | null;
};

type ResourceFormRawValue = FormValueOf<IResource>;

type NewResourceFormRawValue = FormValueOf<NewResource>;

type ResourceFormDefaults = Pick<NewResource, 'id' | 'created'>;

type ResourceFormGroupContent = {
  id: FormControl<ResourceFormRawValue['id'] | NewResource['id']>;
  created: FormControl<ResourceFormRawValue['created']>;
  type: FormControl<ResourceFormRawValue['type']>;
  status: FormControl<ResourceFormRawValue['status']>;
  callSign: FormControl<ResourceFormRawValue['callSign']>;
  latitude: FormControl<ResourceFormRawValue['latitude']>;
  longitude: FormControl<ResourceFormRawValue['longitude']>;
  resourceBreak: FormControl<ResourceFormRawValue['resourceBreak']>;
  event: FormControl<ResourceFormRawValue['event']>;
};

export type ResourceFormGroup = FormGroup<ResourceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResourceFormService {
  createResourceFormGroup(resource: ResourceFormGroupInput = { id: null }): ResourceFormGroup {
    const resourceRawValue = this.convertResourceToResourceRawValue({
      ...this.getFormDefaults(),
      ...resource,
    });
    return new FormGroup<ResourceFormGroupContent>({
      id: new FormControl(
        { value: resourceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      created: new FormControl(resourceRawValue.created, {
        validators: [Validators.required],
      }),
      type: new FormControl(resourceRawValue.type, {
        validators: [Validators.required],
      }),
      status: new FormControl(resourceRawValue.status, {
        validators: [Validators.required],
      }),
      callSign: new FormControl(resourceRawValue.callSign, {
        validators: [Validators.required],
      }),
      latitude: new FormControl(resourceRawValue.latitude, {
        validators: [Validators.required],
      }),
      longitude: new FormControl(resourceRawValue.longitude, {
        validators: [Validators.required],
      }),
      resourceBreak: new FormControl(resourceRawValue.resourceBreak, {
        validators: [Validators.required],
      }),
      event: new FormControl(resourceRawValue.event),
    });
  }

  getResource(form: ResourceFormGroup): IResource | NewResource {
    return this.convertResourceRawValueToResource(form.getRawValue() as ResourceFormRawValue | NewResourceFormRawValue);
  }

  resetForm(form: ResourceFormGroup, resource: ResourceFormGroupInput): void {
    const resourceRawValue = this.convertResourceToResourceRawValue({ ...this.getFormDefaults(), ...resource });
    form.reset(
      {
        ...resourceRawValue,
        id: { value: resourceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ResourceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
    };
  }

  private convertResourceRawValueToResource(rawResource: ResourceFormRawValue | NewResourceFormRawValue): IResource | NewResource {
    return {
      ...rawResource,
      created: dayjs(rawResource.created, DATE_TIME_FORMAT),
    };
  }

  private convertResourceToResourceRawValue(
    resource: IResource | (Partial<NewResource> & ResourceFormDefaults)
  ): ResourceFormRawValue | PartialWithRequiredKeyOf<NewResourceFormRawValue> {
    return {
      ...resource,
      created: resource.created ? resource.created.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
