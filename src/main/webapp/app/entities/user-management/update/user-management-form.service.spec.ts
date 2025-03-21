import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-management.test-samples';

import { UserManagementFormService } from './user-management-form.service';

describe('UserManagement Form Service', () => {
  let service: UserManagementFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserManagementFormService);
  });

  describe('Service methods', () => {
    describe('createUserManagementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserManagementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userRole: expect.any(Object),
            userPerms: expect.any(Object),
          })
        );
      });

      it('passing IUserManagement should create a new form with FormGroup', () => {
        const formGroup = service.createUserManagementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userRole: expect.any(Object),
            userPerms: expect.any(Object),
          })
        );
      });
    });

    describe('getUserManagement', () => {
      it('should return NewUserManagement for default UserManagement initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserManagementFormGroup(sampleWithNewData);

        const userManagement = service.getUserManagement(formGroup) as any;

        expect(userManagement).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserManagement for empty UserManagement initial value', () => {
        const formGroup = service.createUserManagementFormGroup();

        const userManagement = service.getUserManagement(formGroup) as any;

        expect(userManagement).toMatchObject({});
      });

      it('should return IUserManagement', () => {
        const formGroup = service.createUserManagementFormGroup(sampleWithRequiredData);

        const userManagement = service.getUserManagement(formGroup) as any;

        expect(userManagement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserManagement should not enable id FormControl', () => {
        const formGroup = service.createUserManagementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserManagement should disable id FormControl', () => {
        const formGroup = service.createUserManagementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
