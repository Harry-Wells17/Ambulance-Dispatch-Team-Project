import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserPerms } from '../user-perms.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../user-perms.test-samples';

import { UserPermsService } from './user-perms.service';

const requireRestSample: IUserPerms = {
  ...sampleWithRequiredData,
};

describe('UserPerms Service', () => {
  let service: UserPermsService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserPerms | IUserPerms[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserPermsService);
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

    it('should create a UserPerms', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const userPerms = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userPerms).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserPerms', () => {
      const userPerms = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userPerms).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserPerms', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserPerms', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserPerms', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addUserPermsToCollectionIfMissing', () => {
      it('should add a UserPerms to an empty array', () => {
        const userPerms: IUserPerms = sampleWithRequiredData;
        expectedResult = service.addUserPermsToCollectionIfMissing([], userPerms);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userPerms);
      });

      it('should not add a UserPerms to an array that contains it', () => {
        const userPerms: IUserPerms = sampleWithRequiredData;
        const userPermsCollection: IUserPerms[] = [
          {
            ...userPerms,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserPermsToCollectionIfMissing(userPermsCollection, userPerms);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserPerms to an array that doesn't contain it", () => {
        const userPerms: IUserPerms = sampleWithRequiredData;
        const userPermsCollection: IUserPerms[] = [sampleWithPartialData];
        expectedResult = service.addUserPermsToCollectionIfMissing(userPermsCollection, userPerms);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userPerms);
      });

      it('should add only unique UserPerms to an array', () => {
        const userPermsArray: IUserPerms[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userPermsCollection: IUserPerms[] = [sampleWithRequiredData];
        expectedResult = service.addUserPermsToCollectionIfMissing(userPermsCollection, ...userPermsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userPerms: IUserPerms = sampleWithRequiredData;
        const userPerms2: IUserPerms = sampleWithPartialData;
        expectedResult = service.addUserPermsToCollectionIfMissing([], userPerms, userPerms2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userPerms);
        expect(expectedResult).toContain(userPerms2);
      });

      it('should accept null and undefined values', () => {
        const userPerms: IUserPerms = sampleWithRequiredData;
        expectedResult = service.addUserPermsToCollectionIfMissing([], null, userPerms, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userPerms);
      });

      it('should return initial array if no UserPerms is added', () => {
        const userPermsCollection: IUserPerms[] = [sampleWithRequiredData];
        expectedResult = service.addUserPermsToCollectionIfMissing(userPermsCollection, undefined, null);
        expect(expectedResult).toEqual(userPermsCollection);
      });
    });

    describe('compareUserPerms', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserPerms(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareUserPerms(entity1, entity2);
        const compareResult2 = service.compareUserPerms(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareUserPerms(entity1, entity2);
        const compareResult2 = service.compareUserPerms(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareUserPerms(entity1, entity2);
        const compareResult2 = service.compareUserPerms(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
