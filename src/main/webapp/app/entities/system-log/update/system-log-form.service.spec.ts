import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../system-log.test-samples';

import { SystemLogFormService } from './system-log-form.service';

describe('SystemLog Form Service', () => {
  let service: SystemLogFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SystemLogFormService);
  });

  describe('Service methods', () => {
    describe('createSystemLogFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSystemLogFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            createdAt: expect.any(Object),
            logType: expect.any(Object),
            messageContent: expect.any(Object),
            createdBy: expect.any(Object),
            emergencyCall: expect.any(Object),
            event: expect.any(Object),
          })
        );
      });

      it('passing ISystemLog should create a new form with FormGroup', () => {
        const formGroup = service.createSystemLogFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            createdAt: expect.any(Object),
            logType: expect.any(Object),
            messageContent: expect.any(Object),
            createdBy: expect.any(Object),
            emergencyCall: expect.any(Object),
            event: expect.any(Object),
          })
        );
      });
    });

    describe('getSystemLog', () => {
      it('should return NewSystemLog for default SystemLog initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSystemLogFormGroup(sampleWithNewData);

        const systemLog = service.getSystemLog(formGroup) as any;

        expect(systemLog).toMatchObject(sampleWithNewData);
      });

      it('should return NewSystemLog for empty SystemLog initial value', () => {
        const formGroup = service.createSystemLogFormGroup();

        const systemLog = service.getSystemLog(formGroup) as any;

        expect(systemLog).toMatchObject({});
      });

      it('should return ISystemLog', () => {
        const formGroup = service.createSystemLogFormGroup(sampleWithRequiredData);

        const systemLog = service.getSystemLog(formGroup) as any;

        expect(systemLog).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISystemLog should not enable id FormControl', () => {
        const formGroup = service.createSystemLogFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSystemLog should disable id FormControl', () => {
        const formGroup = service.createSystemLogFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
