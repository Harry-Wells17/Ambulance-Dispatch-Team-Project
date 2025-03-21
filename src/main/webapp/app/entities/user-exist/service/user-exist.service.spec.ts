import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserExist } from '../user-exist.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../user-exist.test-samples';

import { UserExistService } from './user-exist.service';

const requireRestSample: IUserExist = {
  ...sampleWithRequiredData,
};

describe('UserExist Service', () => {
  let service: UserExistService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserExist | IUserExist[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserExistService);
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

    it('should create a UserExist', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const userExist = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userExist).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserExist', () => {
      const userExist = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userExist).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserExist', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserExist', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserExist', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addUserExistToCollectionIfMissing', () => {
      it('should add a UserExist to an empty array', () => {
        const userExist: IUserExist = sampleWithRequiredData;
        expectedResult = service.addUserExistToCollectionIfMissing([], userExist);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userExist);
      });

      it('should not add a UserExist to an array that contains it', () => {
        const userExist: IUserExist = sampleWithRequiredData;
        const userExistCollection: IUserExist[] = [
          {
            ...userExist,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserExistToCollectionIfMissing(userExistCollection, userExist);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserExist to an array that doesn't contain it", () => {
        const userExist: IUserExist = sampleWithRequiredData;
        const userExistCollection: IUserExist[] = [sampleWithPartialData];
        expectedResult = service.addUserExistToCollectionIfMissing(userExistCollection, userExist);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userExist);
      });

      it('should add only unique UserExist to an array', () => {
        const userExistArray: IUserExist[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userExistCollection: IUserExist[] = [sampleWithRequiredData];
        expectedResult = service.addUserExistToCollectionIfMissing(userExistCollection, ...userExistArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userExist: IUserExist = sampleWithRequiredData;
        const userExist2: IUserExist = sampleWithPartialData;
        expectedResult = service.addUserExistToCollectionIfMissing([], userExist, userExist2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userExist);
        expect(expectedResult).toContain(userExist2);
      });

      it('should accept null and undefined values', () => {
        const userExist: IUserExist = sampleWithRequiredData;
        expectedResult = service.addUserExistToCollectionIfMissing([], null, userExist, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userExist);
      });

      it('should return initial array if no UserExist is added', () => {
        const userExistCollection: IUserExist[] = [sampleWithRequiredData];
        expectedResult = service.addUserExistToCollectionIfMissing(userExistCollection, undefined, null);
        expect(expectedResult).toEqual(userExistCollection);
      });
    });

    describe('compareUserExist', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserExist(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareUserExist(entity1, entity2);
        const compareResult2 = service.compareUserExist(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareUserExist(entity1, entity2);
        const compareResult2 = service.compareUserExist(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareUserExist(entity1, entity2);
        const compareResult2 = service.compareUserExist(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
