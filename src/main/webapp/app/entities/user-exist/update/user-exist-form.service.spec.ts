import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-exist.test-samples';

import { UserExistFormService } from './user-exist-form.service';

describe('UserExist Form Service', () => {
  let service: UserExistFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserExistFormService);
  });

  describe('Service methods', () => {
    describe('createUserExistFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserExistFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            exist: expect.any(Object),
            createdBy: expect.any(Object),
          })
        );
      });

      it('passing IUserExist should create a new form with FormGroup', () => {
        const formGroup = service.createUserExistFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            exist: expect.any(Object),
            createdBy: expect.any(Object),
          })
        );
      });
    });

    describe('getUserExist', () => {
      it('should return NewUserExist for default UserExist initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserExistFormGroup(sampleWithNewData);

        const userExist = service.getUserExist(formGroup) as any;

        expect(userExist).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserExist for empty UserExist initial value', () => {
        const formGroup = service.createUserExistFormGroup();

        const userExist = service.getUserExist(formGroup) as any;

        expect(userExist).toMatchObject({});
      });

      it('should return IUserExist', () => {
        const formGroup = service.createUserExistFormGroup(sampleWithRequiredData);

        const userExist = service.getUserExist(formGroup) as any;

        expect(userExist).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserExist should not enable id FormControl', () => {
        const formGroup = service.createUserExistFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserExist should disable id FormControl', () => {
        const formGroup = service.createUserExistFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
