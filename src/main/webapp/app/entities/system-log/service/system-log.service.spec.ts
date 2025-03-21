import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISystemLog } from '../system-log.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../system-log.test-samples';

import { SystemLogService, RestSystemLog } from './system-log.service';

const requireRestSample: RestSystemLog = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('SystemLog Service', () => {
  let service: SystemLogService;
  let httpMock: HttpTestingController;
  let expectedResult: ISystemLog | ISystemLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SystemLogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SystemLog', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const systemLog = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(systemLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SystemLog', () => {
      const systemLog = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(systemLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SystemLog', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SystemLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SystemLog', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSystemLogToCollectionIfMissing', () => {
      it('should add a SystemLog to an empty array', () => {
        const systemLog: ISystemLog = sampleWithRequiredData;
        expectedResult = service.addSystemLogToCollectionIfMissing([], systemLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(systemLog);
      });

      it('should not add a SystemLog to an array that contains it', () => {
        const systemLog: ISystemLog = sampleWithRequiredData;
        const systemLogCollection: ISystemLog[] = [
          {
            ...systemLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSystemLogToCollectionIfMissing(systemLogCollection, systemLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SystemLog to an array that doesn't contain it", () => {
        const systemLog: ISystemLog = sampleWithRequiredData;
        const systemLogCollection: ISystemLog[] = [sampleWithPartialData];
        expectedResult = service.addSystemLogToCollectionIfMissing(systemLogCollection, systemLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(systemLog);
      });

      it('should add only unique SystemLog to an array', () => {
        const systemLogArray: ISystemLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const systemLogCollection: ISystemLog[] = [sampleWithRequiredData];
        expectedResult = service.addSystemLogToCollectionIfMissing(systemLogCollection, ...systemLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const systemLog: ISystemLog = sampleWithRequiredData;
        const systemLog2: ISystemLog = sampleWithPartialData;
        expectedResult = service.addSystemLogToCollectionIfMissing([], systemLog, systemLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(systemLog);
        expect(expectedResult).toContain(systemLog2);
      });

      it('should accept null and undefined values', () => {
        const systemLog: ISystemLog = sampleWithRequiredData;
        expectedResult = service.addSystemLogToCollectionIfMissing([], null, systemLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(systemLog);
      });

      it('should return initial array if no SystemLog is added', () => {
        const systemLogCollection: ISystemLog[] = [sampleWithRequiredData];
        expectedResult = service.addSystemLogToCollectionIfMissing(systemLogCollection, undefined, null);
        expect(expectedResult).toEqual(systemLogCollection);
      });
    });

    describe('compareSystemLog', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSystemLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSystemLog(entity1, entity2);
        const compareResult2 = service.compareSystemLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSystemLog(entity1, entity2);
        const compareResult2 = service.compareSystemLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSystemLog(entity1, entity2);
        const compareResult2 = service.compareSystemLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
