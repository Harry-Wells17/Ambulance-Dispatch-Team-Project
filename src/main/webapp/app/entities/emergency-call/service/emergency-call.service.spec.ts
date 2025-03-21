import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEmergencyCall } from '../emergency-call.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../emergency-call.test-samples';

import { EmergencyCallService, RestEmergencyCall } from './emergency-call.service';

const requireRestSample: RestEmergencyCall = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
};

describe('EmergencyCall Service', () => {
  let service: EmergencyCallService;
  let httpMock: HttpTestingController;
  let expectedResult: IEmergencyCall | IEmergencyCall[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EmergencyCallService);
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

    it('should create a EmergencyCall', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const emergencyCall = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(emergencyCall).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EmergencyCall', () => {
      const emergencyCall = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(emergencyCall).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EmergencyCall', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EmergencyCall', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EmergencyCall', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEmergencyCallToCollectionIfMissing', () => {
      it('should add a EmergencyCall to an empty array', () => {
        const emergencyCall: IEmergencyCall = sampleWithRequiredData;
        expectedResult = service.addEmergencyCallToCollectionIfMissing([], emergencyCall);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(emergencyCall);
      });

      it('should not add a EmergencyCall to an array that contains it', () => {
        const emergencyCall: IEmergencyCall = sampleWithRequiredData;
        const emergencyCallCollection: IEmergencyCall[] = [
          {
            ...emergencyCall,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEmergencyCallToCollectionIfMissing(emergencyCallCollection, emergencyCall);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EmergencyCall to an array that doesn't contain it", () => {
        const emergencyCall: IEmergencyCall = sampleWithRequiredData;
        const emergencyCallCollection: IEmergencyCall[] = [sampleWithPartialData];
        expectedResult = service.addEmergencyCallToCollectionIfMissing(emergencyCallCollection, emergencyCall);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(emergencyCall);
      });

      it('should add only unique EmergencyCall to an array', () => {
        const emergencyCallArray: IEmergencyCall[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const emergencyCallCollection: IEmergencyCall[] = [sampleWithRequiredData];
        expectedResult = service.addEmergencyCallToCollectionIfMissing(emergencyCallCollection, ...emergencyCallArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const emergencyCall: IEmergencyCall = sampleWithRequiredData;
        const emergencyCall2: IEmergencyCall = sampleWithPartialData;
        expectedResult = service.addEmergencyCallToCollectionIfMissing([], emergencyCall, emergencyCall2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(emergencyCall);
        expect(expectedResult).toContain(emergencyCall2);
      });

      it('should accept null and undefined values', () => {
        const emergencyCall: IEmergencyCall = sampleWithRequiredData;
        expectedResult = service.addEmergencyCallToCollectionIfMissing([], null, emergencyCall, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(emergencyCall);
      });

      it('should return initial array if no EmergencyCall is added', () => {
        const emergencyCallCollection: IEmergencyCall[] = [sampleWithRequiredData];
        expectedResult = service.addEmergencyCallToCollectionIfMissing(emergencyCallCollection, undefined, null);
        expect(expectedResult).toEqual(emergencyCallCollection);
      });
    });

    describe('compareEmergencyCall', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEmergencyCall(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEmergencyCall(entity1, entity2);
        const compareResult2 = service.compareEmergencyCall(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEmergencyCall(entity1, entity2);
        const compareResult2 = service.compareEmergencyCall(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEmergencyCall(entity1, entity2);
        const compareResult2 = service.compareEmergencyCall(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
