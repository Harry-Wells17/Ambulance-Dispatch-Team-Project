import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../emergency-call.test-samples';

import { EmergencyCallFormService } from './emergency-call-form.service';

describe('EmergencyCall Form Service', () => {
  let service: EmergencyCallFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmergencyCallFormService);
  });

  describe('Service methods', () => {
    describe('createEmergencyCallFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEmergencyCallFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            created: expect.any(Object),
            open: expect.any(Object),
            type: expect.any(Object),
            age: expect.any(Object),
            sexAssignedAtBirth: expect.any(Object),
            history: expect.any(Object),
            injuries: expect.any(Object),
            condition: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            closed: expect.any(Object),
            createdBy: expect.any(Object),
            event: expect.any(Object),
          })
        );
      });

      it('passing IEmergencyCall should create a new form with FormGroup', () => {
        const formGroup = service.createEmergencyCallFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            created: expect.any(Object),
            open: expect.any(Object),
            type: expect.any(Object),
            age: expect.any(Object),
            sexAssignedAtBirth: expect.any(Object),
            history: expect.any(Object),
            injuries: expect.any(Object),
            condition: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            closed: expect.any(Object),
            createdBy: expect.any(Object),
            event: expect.any(Object),
          })
        );
      });
    });

    describe('getEmergencyCall', () => {
      it('should return NewEmergencyCall for default EmergencyCall initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEmergencyCallFormGroup(sampleWithNewData);

        const emergencyCall = service.getEmergencyCall(formGroup) as any;

        expect(emergencyCall).toMatchObject(sampleWithNewData);
      });

      it('should return NewEmergencyCall for empty EmergencyCall initial value', () => {
        const formGroup = service.createEmergencyCallFormGroup();

        const emergencyCall = service.getEmergencyCall(formGroup) as any;

        expect(emergencyCall).toMatchObject({});
      });

      it('should return IEmergencyCall', () => {
        const formGroup = service.createEmergencyCallFormGroup(sampleWithRequiredData);

        const emergencyCall = service.getEmergencyCall(formGroup) as any;

        expect(emergencyCall).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEmergencyCall should not enable id FormControl', () => {
        const formGroup = service.createEmergencyCallFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEmergencyCall should disable id FormControl', () => {
        const formGroup = service.createEmergencyCallFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
