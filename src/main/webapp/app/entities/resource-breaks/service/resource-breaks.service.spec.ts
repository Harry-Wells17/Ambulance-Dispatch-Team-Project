import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IResourceBreaks } from '../resource-breaks.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../resource-breaks.test-samples';

import { ResourceBreaksService, RestResourceBreaks } from './resource-breaks.service';

const requireRestSample: RestResourceBreaks = {
  ...sampleWithRequiredData,
  lastBreak: sampleWithRequiredData.lastBreak?.toJSON(),
  startedBreak: sampleWithRequiredData.startedBreak?.toJSON(),
};

describe('ResourceBreaks Service', () => {
  let service: ResourceBreaksService;
  let httpMock: HttpTestingController;
  let expectedResult: IResourceBreaks | IResourceBreaks[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ResourceBreaksService);
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

    it('should create a ResourceBreaks', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const resourceBreaks = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(resourceBreaks).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ResourceBreaks', () => {
      const resourceBreaks = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(resourceBreaks).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ResourceBreaks', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ResourceBreaks', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ResourceBreaks', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addResourceBreaksToCollectionIfMissing', () => {
      it('should add a ResourceBreaks to an empty array', () => {
        const resourceBreaks: IResourceBreaks = sampleWithRequiredData;
        expectedResult = service.addResourceBreaksToCollectionIfMissing([], resourceBreaks);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(resourceBreaks);
      });

      it('should not add a ResourceBreaks to an array that contains it', () => {
        const resourceBreaks: IResourceBreaks = sampleWithRequiredData;
        const resourceBreaksCollection: IResourceBreaks[] = [
          {
            ...resourceBreaks,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addResourceBreaksToCollectionIfMissing(resourceBreaksCollection, resourceBreaks);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ResourceBreaks to an array that doesn't contain it", () => {
        const resourceBreaks: IResourceBreaks = sampleWithRequiredData;
        const resourceBreaksCollection: IResourceBreaks[] = [sampleWithPartialData];
        expectedResult = service.addResourceBreaksToCollectionIfMissing(resourceBreaksCollection, resourceBreaks);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(resourceBreaks);
      });

      it('should add only unique ResourceBreaks to an array', () => {
        const resourceBreaksArray: IResourceBreaks[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const resourceBreaksCollection: IResourceBreaks[] = [sampleWithRequiredData];
        expectedResult = service.addResourceBreaksToCollectionIfMissing(resourceBreaksCollection, ...resourceBreaksArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const resourceBreaks: IResourceBreaks = sampleWithRequiredData;
        const resourceBreaks2: IResourceBreaks = sampleWithPartialData;
        expectedResult = service.addResourceBreaksToCollectionIfMissing([], resourceBreaks, resourceBreaks2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(resourceBreaks);
        expect(expectedResult).toContain(resourceBreaks2);
      });

      it('should accept null and undefined values', () => {
        const resourceBreaks: IResourceBreaks = sampleWithRequiredData;
        expectedResult = service.addResourceBreaksToCollectionIfMissing([], null, resourceBreaks, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(resourceBreaks);
      });

      it('should return initial array if no ResourceBreaks is added', () => {
        const resourceBreaksCollection: IResourceBreaks[] = [sampleWithRequiredData];
        expectedResult = service.addResourceBreaksToCollectionIfMissing(resourceBreaksCollection, undefined, null);
        expect(expectedResult).toEqual(resourceBreaksCollection);
      });
    });

    describe('compareResourceBreaks', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareResourceBreaks(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareResourceBreaks(entity1, entity2);
        const compareResult2 = service.compareResourceBreaks(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareResourceBreaks(entity1, entity2);
        const compareResult2 = service.compareResourceBreaks(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareResourceBreaks(entity1, entity2);
        const compareResult2 = service.compareResourceBreaks(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
