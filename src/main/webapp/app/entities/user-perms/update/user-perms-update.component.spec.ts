import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserPermsFormService } from './user-perms-form.service';
import { UserPermsService } from '../service/user-perms.service';
import { IUserPerms } from '../user-perms.model';

import { UserPermsUpdateComponent } from './user-perms-update.component';

describe('UserPerms Management Update Component', () => {
  let comp: UserPermsUpdateComponent;
  let fixture: ComponentFixture<UserPermsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userPermsFormService: UserPermsFormService;
  let userPermsService: UserPermsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserPermsUpdateComponent],
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
      .overrideTemplate(UserPermsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserPermsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userPermsFormService = TestBed.inject(UserPermsFormService);
    userPermsService = TestBed.inject(UserPermsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const userPerms: IUserPerms = { id: 456 };

      activatedRoute.data = of({ userPerms });
      comp.ngOnInit();

      expect(comp.userPerms).toEqual(userPerms);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserPerms>>();
      const userPerms = { id: 123 };
      jest.spyOn(userPermsFormService, 'getUserPerms').mockReturnValue(userPerms);
      jest.spyOn(userPermsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPerms });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userPerms }));
      saveSubject.complete();

      // THEN
      expect(userPermsFormService.getUserPerms).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userPermsService.update).toHaveBeenCalledWith(expect.objectContaining(userPerms));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserPerms>>();
      const userPerms = { id: 123 };
      jest.spyOn(userPermsFormService, 'getUserPerms').mockReturnValue({ id: null });
      jest.spyOn(userPermsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPerms: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userPerms }));
      saveSubject.complete();

      // THEN
      expect(userPermsFormService.getUserPerms).toHaveBeenCalled();
      expect(userPermsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserPerms>>();
      const userPerms = { id: 123 };
      jest.spyOn(userPermsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPerms });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userPermsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
