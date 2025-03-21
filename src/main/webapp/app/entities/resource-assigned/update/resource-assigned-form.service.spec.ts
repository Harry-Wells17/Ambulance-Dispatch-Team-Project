import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../resource-assigned.test-samples';

import { ResourceAssignedFormService } from './resource-assigned-form.service';

describe('ResourceAssigned Form Service', () => {
  let service: ResourceAssignedFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResourceAssignedFormService);
  });

  describe('Service methods', () => {
    describe('createResourceAssignedFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createResourceAssignedFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            callRecievedTime: expect.any(Object),
            onSceneTime: expect.any(Object),
            leftSceneTime: expect.any(Object),
            arrivedHospitalTime: expect.any(Object),
            clearHospitalTime: expect.any(Object),
            greenTime: expect.any(Object),
            unAssignedTime: expect.any(Object),
            resource: expect.any(Object),
            emergencyCall: expect.any(Object),
          })
        );
      });

      it('passing IResourceAssigned should create a new form with FormGroup', () => {
        const formGroup = service.createResourceAssignedFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            callRecievedTime: expect.any(Object),
            onSceneTime: expect.any(Object),
            leftSceneTime: expect.any(Object),
            arrivedHospitalTime: expect.any(Object),
            clearHospitalTime: expect.any(Object),
            greenTime: expect.any(Object),
            unAssignedTime: expect.any(Object),
            resource: expect.any(Object),
            emergencyCall: expect.any(Object),
          })
        );
      });
    });

    describe('getResourceAssigned', () => {
      it('should return NewResourceAssigned for default ResourceAssigned initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createResourceAssignedFormGroup(sampleWithNewData);

        const resourceAssigned = service.getResourceAssigned(formGroup) as any;

        expect(resourceAssigned).toMatchObject(sampleWithNewData);
      });

      it('should return NewResourceAssigned for empty ResourceAssigned initial value', () => {
        const formGroup = service.createResourceAssignedFormGroup();

        const resourceAssigned = service.getResourceAssigned(formGroup) as any;

        expect(resourceAssigned).toMatchObject({});
      });

      it('should return IResourceAssigned', () => {
        const formGroup = service.createResourceAssignedFormGroup(sampleWithRequiredData);

        const resourceAssigned = service.getResourceAssigned(formGroup) as any;

        expect(resourceAssigned).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IResourceAssigned should not enable id FormControl', () => {
        const formGroup = service.createResourceAssignedFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewResourceAssigned should disable id FormControl', () => {
        const formGroup = service.createResourceAssignedFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
