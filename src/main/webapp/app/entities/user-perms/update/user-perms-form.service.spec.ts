import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-perms.test-samples';

import { UserPermsFormService } from './user-perms-form.service';

describe('UserPerms Form Service', () => {
  let service: UserPermsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserPermsFormService);
  });

  describe('Service methods', () => {
    describe('createUserPermsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserPermsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            perms: expect.any(Object),
          })
        );
      });

      it('passing IUserPerms should create a new form with FormGroup', () => {
        const formGroup = service.createUserPermsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            perms: expect.any(Object),
          })
        );
      });
    });

    describe('getUserPerms', () => {
      it('should return NewUserPerms for default UserPerms initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserPermsFormGroup(sampleWithNewData);

        const userPerms = service.getUserPerms(formGroup) as any;

        expect(userPerms).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserPerms for empty UserPerms initial value', () => {
        const formGroup = service.createUserPermsFormGroup();

        const userPerms = service.getUserPerms(formGroup) as any;

        expect(userPerms).toMatchObject({});
      });

      it('should return IUserPerms', () => {
        const formGroup = service.createUserPermsFormGroup(sampleWithRequiredData);

        const userPerms = service.getUserPerms(formGroup) as any;

        expect(userPerms).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserPerms should not enable id FormControl', () => {
        const formGroup = service.createUserPermsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserPerms should disable id FormControl', () => {
        const formGroup = service.createUserPermsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
