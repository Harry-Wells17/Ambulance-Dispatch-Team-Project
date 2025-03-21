import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEmergencyCall, NewEmergencyCall } from '../emergency-call.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEmergencyCall for edit and NewEmergencyCallFormGroupInput for create.
 */
type EmergencyCallFormGroupInput = IEmergencyCall | PartialWithRequiredKeyOf<NewEmergencyCall>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEmergencyCall | NewEmergencyCall> = Omit<T, 'created'> & {
  created?: string | null;
};

type EmergencyCallFormRawValue = FormValueOf<IEmergencyCall>;

type NewEmergencyCallFormRawValue = FormValueOf<NewEmergencyCall>;

type EmergencyCallFormDefaults = Pick<NewEmergencyCall, 'id' | 'created' | 'open' | 'closed'>;

type EmergencyCallFormGroupContent = {
  id: FormControl<EmergencyCallFormRawValue['id'] | NewEmergencyCall['id']>;
  created: FormControl<EmergencyCallFormRawValue['created']>;
  open: FormControl<EmergencyCallFormRawValue['open']>;
  type: FormControl<EmergencyCallFormRawValue['type']>;
  age: FormControl<EmergencyCallFormRawValue['age']>;
  sexAssignedAtBirth: FormControl<EmergencyCallFormRawValue['sexAssignedAtBirth']>;
  history: FormControl<EmergencyCallFormRawValue['history']>;
  injuries: FormControl<EmergencyCallFormRawValue['injuries']>;
  condition: FormControl<EmergencyCallFormRawValue['condition']>;
  latitude: FormControl<EmergencyCallFormRawValue['latitude']>;
  longitude: FormControl<EmergencyCallFormRawValue['longitude']>;
  closed: FormControl<EmergencyCallFormRawValue['closed']>;
  createdBy: FormControl<EmergencyCallFormRawValue['createdBy']>;
  event: FormControl<EmergencyCallFormRawValue['event']>;
};

export type EmergencyCallFormGroup = FormGroup<EmergencyCallFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EmergencyCallFormService {
  createEmergencyCallFormGroup(emergencyCall: EmergencyCallFormGroupInput = { id: null }): EmergencyCallFormGroup {
    const emergencyCallRawValue = this.convertEmergencyCallToEmergencyCallRawValue({
      ...this.getFormDefaults(),
      ...emergencyCall,
    });
    return new FormGroup<EmergencyCallFormGroupContent>({
      id: new FormControl(
        { value: emergencyCallRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      created: new FormControl(emergencyCallRawValue.created, {
        validators: [Validators.required],
      }),
      open: new FormControl(emergencyCallRawValue.open),
      type: new FormControl(emergencyCallRawValue.type),
      age: new FormControl(emergencyCallRawValue.age),
      sexAssignedAtBirth: new FormControl(emergencyCallRawValue.sexAssignedAtBirth),
      history: new FormControl(emergencyCallRawValue.history),
      injuries: new FormControl(emergencyCallRawValue.injuries),
      condition: new FormControl(emergencyCallRawValue.condition),
      latitude: new FormControl(emergencyCallRawValue.latitude, {
        validators: [Validators.required],
      }),
      longitude: new FormControl(emergencyCallRawValue.longitude, {
        validators: [Validators.required],
      }),
      closed: new FormControl(emergencyCallRawValue.closed),
      createdBy: new FormControl(emergencyCallRawValue.createdBy, {
        validators: [Validators.required],
      }),
      event: new FormControl(emergencyCallRawValue.event),
    });
  }

  getEmergencyCall(form: EmergencyCallFormGroup): IEmergencyCall | NewEmergencyCall {
    return this.convertEmergencyCallRawValueToEmergencyCall(form.getRawValue() as EmergencyCallFormRawValue | NewEmergencyCallFormRawValue);
  }

  resetForm(form: EmergencyCallFormGroup, emergencyCall: EmergencyCallFormGroupInput): void {
    const emergencyCallRawValue = this.convertEmergencyCallToEmergencyCallRawValue({ ...this.getFormDefaults(), ...emergencyCall });
    form.reset(
      {
        ...emergencyCallRawValue,
        id: { value: emergencyCallRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EmergencyCallFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      open: false,
      closed: false,
    };
  }

  private convertEmergencyCallRawValueToEmergencyCall(
    rawEmergencyCall: EmergencyCallFormRawValue | NewEmergencyCallFormRawValue
  ): IEmergencyCall | NewEmergencyCall {
    return {
      ...rawEmergencyCall,
      created: dayjs(rawEmergencyCall.created, DATE_TIME_FORMAT),
    };
  }

  private convertEmergencyCallToEmergencyCallRawValue(
    emergencyCall: IEmergencyCall | (Partial<NewEmergencyCall> & EmergencyCallFormDefaults)
  ): EmergencyCallFormRawValue | PartialWithRequiredKeyOf<NewEmergencyCallFormRawValue> {
    return {
      ...emergencyCall,
      created: emergencyCall.created ? emergencyCall.created.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
