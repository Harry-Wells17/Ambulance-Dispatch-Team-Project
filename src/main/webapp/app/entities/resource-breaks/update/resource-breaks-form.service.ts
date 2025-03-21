import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IResourceBreaks, NewResourceBreaks } from '../resource-breaks.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResourceBreaks for edit and NewResourceBreaksFormGroupInput for create.
 */
type ResourceBreaksFormGroupInput = IResourceBreaks | PartialWithRequiredKeyOf<NewResourceBreaks>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IResourceBreaks | NewResourceBreaks> = Omit<T, 'lastBreak' | 'startedBreak'> & {
  lastBreak?: string | null;
  startedBreak?: string | null;
};

type ResourceBreaksFormRawValue = FormValueOf<IResourceBreaks>;

type NewResourceBreaksFormRawValue = FormValueOf<NewResourceBreaks>;

type ResourceBreaksFormDefaults = Pick<NewResourceBreaks, 'id' | 'lastBreak' | 'breakRequested' | 'startedBreak' | 'onBreak'>;

type ResourceBreaksFormGroupContent = {
  id: FormControl<ResourceBreaksFormRawValue['id'] | NewResourceBreaks['id']>;
  lastBreak: FormControl<ResourceBreaksFormRawValue['lastBreak']>;
  breakRequested: FormControl<ResourceBreaksFormRawValue['breakRequested']>;
  startedBreak: FormControl<ResourceBreaksFormRawValue['startedBreak']>;
  onBreak: FormControl<ResourceBreaksFormRawValue['onBreak']>;
};

export type ResourceBreaksFormGroup = FormGroup<ResourceBreaksFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResourceBreaksFormService {
  createResourceBreaksFormGroup(resourceBreaks: ResourceBreaksFormGroupInput = { id: null }): ResourceBreaksFormGroup {
    const resourceBreaksRawValue = this.convertResourceBreaksToResourceBreaksRawValue({
      ...this.getFormDefaults(),
      ...resourceBreaks,
    });
    return new FormGroup<ResourceBreaksFormGroupContent>({
      id: new FormControl(
        { value: resourceBreaksRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      lastBreak: new FormControl(resourceBreaksRawValue.lastBreak, {
        validators: [Validators.required],
      }),
      breakRequested: new FormControl(resourceBreaksRawValue.breakRequested, {
        validators: [Validators.required],
      }),
      startedBreak: new FormControl(resourceBreaksRawValue.startedBreak),
      onBreak: new FormControl(resourceBreaksRawValue.onBreak),
    });
  }

  getResourceBreaks(form: ResourceBreaksFormGroup): IResourceBreaks | NewResourceBreaks {
    return this.convertResourceBreaksRawValueToResourceBreaks(
      form.getRawValue() as ResourceBreaksFormRawValue | NewResourceBreaksFormRawValue
    );
  }

  resetForm(form: ResourceBreaksFormGroup, resourceBreaks: ResourceBreaksFormGroupInput): void {
    const resourceBreaksRawValue = this.convertResourceBreaksToResourceBreaksRawValue({ ...this.getFormDefaults(), ...resourceBreaks });
    form.reset(
      {
        ...resourceBreaksRawValue,
        id: { value: resourceBreaksRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ResourceBreaksFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastBreak: currentTime,
      breakRequested: false,
      startedBreak: currentTime,
      onBreak: false,
    };
  }

  private convertResourceBreaksRawValueToResourceBreaks(
    rawResourceBreaks: ResourceBreaksFormRawValue | NewResourceBreaksFormRawValue
  ): IResourceBreaks | NewResourceBreaks {
    return {
      ...rawResourceBreaks,
      lastBreak: dayjs(rawResourceBreaks.lastBreak, DATE_TIME_FORMAT),
      startedBreak: dayjs(rawResourceBreaks.startedBreak, DATE_TIME_FORMAT),
    };
  }

  private convertResourceBreaksToResourceBreaksRawValue(
    resourceBreaks: IResourceBreaks | (Partial<NewResourceBreaks> & ResourceBreaksFormDefaults)
  ): ResourceBreaksFormRawValue | PartialWithRequiredKeyOf<NewResourceBreaksFormRawValue> {
    return {
      ...resourceBreaks,
      lastBreak: resourceBreaks.lastBreak ? resourceBreaks.lastBreak.format(DATE_TIME_FORMAT) : undefined,
      startedBreak: resourceBreaks.startedBreak ? resourceBreaks.startedBreak.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
