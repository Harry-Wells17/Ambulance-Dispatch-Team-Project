import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserExistFormService } from './user-exist-form.service';
import { UserExistService } from '../service/user-exist.service';
import { IUserExist } from '../user-exist.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { UserExistUpdateComponent } from './user-exist-update.component';

describe('UserExist Management Update Component', () => {
  let comp: UserExistUpdateComponent;
  let fixture: ComponentFixture<UserExistUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userExistFormService: UserExistFormService;
  let userExistService: UserExistService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserExistUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UserExistUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserExistUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userExistFormService = TestBed.inject(UserExistFormService);
    userExistService = TestBed.inject(UserExistService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userExist: IUserExist = { id: 456 };
      const createdBy: IUser = { id: 18942 };
      userExist.createdBy = createdBy;

      const userCollection: IUser[] = [{ id: 57810 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userExist });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userExist: IUserExist = { id: 456 };
      const createdBy: IUser = { id: 22723 };
      userExist.createdBy = createdBy;

      activatedRoute.data = of({ userExist });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.userExist).toEqual(userExist);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserExist>>();
      const userExist = { id: 123 };
      jest.spyOn(userExistFormService, 'getUserExist').mockReturnValue(userExist);
      jest.spyOn(userExistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userExist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userExist }));
      saveSubject.complete();

      // THEN
      expect(userExistFormService.getUserExist).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userExistService.update).toHaveBeenCalledWith(expect.objectContaining(userExist));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserExist>>();
      const userExist = { id: 123 };
      jest.spyOn(userExistFormService, 'getUserExist').mockReturnValue({ id: null });
      jest.spyOn(userExistService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userExist: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userExist }));
      saveSubject.complete();

      // THEN
      expect(userExistFormService.getUserExist).toHaveBeenCalled();
      expect(userExistService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserExist>>();
      const userExist = { id: 123 };
      jest.spyOn(userExistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userExist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userExistService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
