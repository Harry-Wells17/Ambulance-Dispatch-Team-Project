import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserRoleFormService } from './user-role-form.service';
import { UserRoleService } from '../service/user-role.service';
import { IUserRole } from '../user-role.model';

import { UserRoleUpdateComponent } from './user-role-update.component';

describe('UserRole Management Update Component', () => {
  let comp: UserRoleUpdateComponent;
  let fixture: ComponentFixture<UserRoleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userRoleFormService: UserRoleFormService;
  let userRoleService: UserRoleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserRoleUpdateComponent],
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
      .overrideTemplate(UserRoleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserRoleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userRoleFormService = TestBed.inject(UserRoleFormService);
    userRoleService = TestBed.inject(UserRoleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const userRole: IUserRole = { id: 456 };

      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      expect(comp.userRole).toEqual(userRole);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserRole>>();
      const userRole = { id: 123 };
      jest.spyOn(userRoleFormService, 'getUserRole').mockReturnValue(userRole);
      jest.spyOn(userRoleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userRole }));
      saveSubject.complete();

      // THEN
      expect(userRoleFormService.getUserRole).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userRoleService.update).toHaveBeenCalledWith(expect.objectContaining(userRole));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserRole>>();
      const userRole = { id: 123 };
      jest.spyOn(userRoleFormService, 'getUserRole').mockReturnValue({ id: null });
      jest.spyOn(userRoleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userRole: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userRole }));
      saveSubject.complete();

      // THEN
      expect(userRoleFormService.getUserRole).toHaveBeenCalled();
      expect(userRoleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserRole>>();
      const userRole = { id: 123 };
      jest.spyOn(userRoleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userRoleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
