import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGeoLocation, NewGeoLocation } from '../geo-location.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGeoLocation for edit and NewGeoLocationFormGroupInput for create.
 */
type GeoLocationFormGroupInput = IGeoLocation | PartialWithRequiredKeyOf<NewGeoLocation>;

type GeoLocationFormDefaults = Pick<NewGeoLocation, 'id'>;

type GeoLocationFormGroupContent = {
  id: FormControl<IGeoLocation['id'] | NewGeoLocation['id']>;
  latitude: FormControl<IGeoLocation['latitude']>;
  longitude: FormControl<IGeoLocation['longitude']>;
};

export type GeoLocationFormGroup = FormGroup<GeoLocationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GeoLocationFormService {
  createGeoLocationFormGroup(geoLocation: GeoLocationFormGroupInput = { id: null }): GeoLocationFormGroup {
    const geoLocationRawValue = {
      ...this.getFormDefaults(),
      ...geoLocation,
    };
    return new FormGroup<GeoLocationFormGroupContent>({
      id: new FormControl(
        { value: geoLocationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      latitude: new FormControl(geoLocationRawValue.latitude, {
        validators: [Validators.required],
      }),
      longitude: new FormControl(geoLocationRawValue.longitude, {
        validators: [Validators.required],
      }),
    });
  }

  getGeoLocation(form: GeoLocationFormGroup): IGeoLocation | NewGeoLocation {
    return form.getRawValue() as IGeoLocation | NewGeoLocation;
  }

  resetForm(form: GeoLocationFormGroup, geoLocation: GeoLocationFormGroupInput): void {
    const geoLocationRawValue = { ...this.getFormDefaults(), ...geoLocation };
    form.reset(
      {
        ...geoLocationRawValue,
        id: { value: geoLocationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GeoLocationFormDefaults {
    return {
      id: null,
    };
  }
}
