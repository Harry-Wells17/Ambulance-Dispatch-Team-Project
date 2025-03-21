import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../resource-breaks.test-samples';

import { ResourceBreaksFormService } from './resource-breaks-form.service';

describe('ResourceBreaks Form Service', () => {
  let service: ResourceBreaksFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResourceBreaksFormService);
  });

  describe('Service methods', () => {
    describe('createResourceBreaksFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createResourceBreaksFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            lastBreak: expect.any(Object),
            breakRequested: expect.any(Object),
            startedBreak: expect.any(Object),
            onBreak: expect.any(Object),
          })
        );
      });

      it('passing IResourceBreaks should create a new form with FormGroup', () => {
        const formGroup = service.createResourceBreaksFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            lastBreak: expect.any(Object),
            breakRequested: expect.any(Object),
            startedBreak: expect.any(Object),
            onBreak: expect.any(Object),
          })
        );
      });
    });

    describe('getResourceBreaks', () => {
      it('should return NewResourceBreaks for default ResourceBreaks initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createResourceBreaksFormGroup(sampleWithNewData);

        const resourceBreaks = service.getResourceBreaks(formGroup) as any;

        expect(resourceBreaks).toMatchObject(sampleWithNewData);
      });

      it('should return NewResourceBreaks for empty ResourceBreaks initial value', () => {
        const formGroup = service.createResourceBreaksFormGroup();

        const resourceBreaks = service.getResourceBreaks(formGroup) as any;

        expect(resourceBreaks).toMatchObject({});
      });

      it('should return IResourceBreaks', () => {
        const formGroup = service.createResourceBreaksFormGroup(sampleWithRequiredData);

        const resourceBreaks = service.getResourceBreaks(formGroup) as any;

        expect(resourceBreaks).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IResourceBreaks should not enable id FormControl', () => {
        const formGroup = service.createResourceBreaksFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewResourceBreaks should disable id FormControl', () => {
        const formGroup = service.createResourceBreaksFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
