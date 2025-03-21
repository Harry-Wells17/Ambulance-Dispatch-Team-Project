import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISystemLog, NewSystemLog } from '../system-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISystemLog for edit and NewSystemLogFormGroupInput for create.
 */
type SystemLogFormGroupInput = ISystemLog | PartialWithRequiredKeyOf<NewSystemLog>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISystemLog | NewSystemLog> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type SystemLogFormRawValue = FormValueOf<ISystemLog>;

type NewSystemLogFormRawValue = FormValueOf<NewSystemLog>;

type SystemLogFormDefaults = Pick<NewSystemLog, 'id' | 'createdAt'>;

type SystemLogFormGroupContent = {
  id: FormControl<SystemLogFormRawValue['id'] | NewSystemLog['id']>;
  createdAt: FormControl<SystemLogFormRawValue['createdAt']>;
  logType: FormControl<SystemLogFormRawValue['logType']>;
  messageContent: FormControl<SystemLogFormRawValue['messageContent']>;
  createdBy: FormControl<SystemLogFormRawValue['createdBy']>;
  emergencyCall: FormControl<SystemLogFormRawValue['emergencyCall']>;
  event: FormControl<SystemLogFormRawValue['event']>;
};

export type SystemLogFormGroup = FormGroup<SystemLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SystemLogFormService {
  createSystemLogFormGroup(systemLog: SystemLogFormGroupInput = { id: null }): SystemLogFormGroup {
    const systemLogRawValue = this.convertSystemLogToSystemLogRawValue({
      ...this.getFormDefaults(),
      ...systemLog,
    });
    return new FormGroup<SystemLogFormGroupContent>({
      id: new FormControl(
        { value: systemLogRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      createdAt: new FormControl(systemLogRawValue.createdAt, {
        validators: [Validators.required],
      }),
      logType: new FormControl(systemLogRawValue.logType, {
        validators: [Validators.required],
      }),
      messageContent: new FormControl(systemLogRawValue.messageContent, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(systemLogRawValue.createdBy, {
        validators: [Validators.required],
      }),
      emergencyCall: new FormControl(systemLogRawValue.emergencyCall),
      event: new FormControl(systemLogRawValue.event),
    });
  }

  getSystemLog(form: SystemLogFormGroup): ISystemLog | NewSystemLog {
    return this.convertSystemLogRawValueToSystemLog(form.getRawValue() as SystemLogFormRawValue | NewSystemLogFormRawValue);
  }

  resetForm(form: SystemLogFormGroup, systemLog: SystemLogFormGroupInput): void {
    const systemLogRawValue = this.convertSystemLogToSystemLogRawValue({ ...this.getFormDefaults(), ...systemLog });
    form.reset(
      {
        ...systemLogRawValue,
        id: { value: systemLogRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SystemLogFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertSystemLogRawValueToSystemLog(rawSystemLog: SystemLogFormRawValue | NewSystemLogFormRawValue): ISystemLog | NewSystemLog {
    return {
      ...rawSystemLog,
      createdAt: dayjs(rawSystemLog.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertSystemLogToSystemLogRawValue(
    systemLog: ISystemLog | (Partial<NewSystemLog> & SystemLogFormDefaults)
  ): SystemLogFormRawValue | PartialWithRequiredKeyOf<NewSystemLogFormRawValue> {
    return {
      ...systemLog,
      createdAt: systemLog.createdAt ? systemLog.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
