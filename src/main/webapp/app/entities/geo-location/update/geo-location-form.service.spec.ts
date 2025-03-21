import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../geo-location.test-samples';

import { GeoLocationFormService } from './geo-location-form.service';

describe('GeoLocation Form Service', () => {
  let service: GeoLocationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GeoLocationFormService);
  });

  describe('Service methods', () => {
    describe('createGeoLocationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGeoLocationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
          })
        );
      });

      it('passing IGeoLocation should create a new form with FormGroup', () => {
        const formGroup = service.createGeoLocationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
          })
        );
      });
    });

    describe('getGeoLocation', () => {
      it('should return NewGeoLocation for default GeoLocation initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGeoLocationFormGroup(sampleWithNewData);

        const geoLocation = service.getGeoLocation(formGroup) as any;

        expect(geoLocation).toMatchObject(sampleWithNewData);
      });

      it('should return NewGeoLocation for empty GeoLocation initial value', () => {
        const formGroup = service.createGeoLocationFormGroup();

        const geoLocation = service.getGeoLocation(formGroup) as any;

        expect(geoLocation).toMatchObject({});
      });

      it('should return IGeoLocation', () => {
        const formGroup = service.createGeoLocationFormGroup(sampleWithRequiredData);

        const geoLocation = service.getGeoLocation(formGroup) as any;

        expect(geoLocation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGeoLocation should not enable id FormControl', () => {
        const formGroup = service.createGeoLocationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGeoLocation should disable id FormControl', () => {
        const formGroup = service.createGeoLocationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
