import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserManagementFormService } from './user-management-form.service';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement } from '../user-management.model';
import { IUserRole } from 'app/entities/user-role/user-role.model';
import { UserRoleService } from 'app/entities/user-role/service/user-role.service';
import { IUserPerms } from 'app/entities/user-perms/user-perms.model';
import { UserPermsService } from 'app/entities/user-perms/service/user-perms.service';

import { UserManagementUpdateComponent } from './user-management-update.component';

describe('UserManagement Management Update Component', () => {
  let comp: UserManagementUpdateComponent;
  let fixture: ComponentFixture<UserManagementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userManagementFormService: UserManagementFormService;
  let userManagementService: UserManagementService;
  let userRoleService: UserRoleService;
  let userPermsService: UserPermsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserManagementUpdateComponent],
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
      .overrideTemplate(UserManagementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserManagementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userManagementFormService = TestBed.inject(UserManagementFormService);
    userManagementService = TestBed.inject(UserManagementService);
    userRoleService = TestBed.inject(UserRoleService);
    userPermsService = TestBed.inject(UserPermsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call userRole query and add missing value', () => {
      const userManagement: IUserManagement = { id: 456 };
      const userRole: IUserRole = { id: 42339 };
      userManagement.userRole = userRole;

      const userRoleCollection: IUserRole[] = [{ id: 89084 }];
      jest.spyOn(userRoleService, 'query').mockReturnValue(of(new HttpResponse({ body: userRoleCollection })));
      const expectedCollection: IUserRole[] = [userRole, ...userRoleCollection];
      jest.spyOn(userRoleService, 'addUserRoleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userManagement });
      comp.ngOnInit();

      expect(userRoleService.query).toHaveBeenCalled();
      expect(userRoleService.addUserRoleToCollectionIfMissing).toHaveBeenCalledWith(userRoleCollection, userRole);
      expect(comp.userRolesCollection).toEqual(expectedCollection);
    });

    it('Should call UserPerms query and add missing value', () => {
      const userManagement: IUserManagement = { id: 456 };
      const userPerms: IUserPerms = { id: 61189 };
      userManagement.userPerms = userPerms;

      const userPermsCollection: IUserPerms[] = [{ id: 3183 }];
      jest.spyOn(userPermsService, 'query').mockReturnValue(of(new HttpResponse({ body: userPermsCollection })));
      const additionalUserPerms = [userPerms];
      const expectedCollection: IUserPerms[] = [...additionalUserPerms, ...userPermsCollection];
      jest.spyOn(userPermsService, 'addUserPermsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userManagement });
      comp.ngOnInit();

      expect(userPermsService.query).toHaveBeenCalled();
      expect(userPermsService.addUserPermsToCollectionIfMissing).toHaveBeenCalledWith(
        userPermsCollection,
        ...additionalUserPerms.map(expect.objectContaining)
      );
      expect(comp.userPermsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userManagement: IUserManagement = { id: 456 };
      const userRole: IUserRole = { id: 76827 };
      userManagement.userRole = userRole;
      const userPerms: IUserPerms = { id: 84691 };
      userManagement.userPerms = userPerms;

      activatedRoute.data = of({ userManagement });
      comp.ngOnInit();

      expect(comp.userRolesCollection).toContain(userRole);
      expect(comp.userPermsSharedCollection).toContain(userPerms);
      expect(comp.userManagement).toEqual(userManagement);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserManagement>>();
      const userManagement = { id: 123 };
      jest.spyOn(userManagementFormService, 'getUserManagement').mockReturnValue(userManagement);
      jest.spyOn(userManagementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userManagement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userManagement }));
      saveSubject.complete();

      // THEN
      expect(userManagementFormService.getUserManagement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userManagementService.update).toHaveBeenCalledWith(expect.objectContaining(userManagement));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserManagement>>();
      const userManagement = { id: 123 };
      jest.spyOn(userManagementFormService, 'getUserManagement').mockReturnValue({ id: null });
      jest.spyOn(userManagementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userManagement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userManagement }));
      saveSubject.complete();

      // THEN
      expect(userManagementFormService.getUserManagement).toHaveBeenCalled();
      expect(userManagementService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserManagement>>();
      const userManagement = { id: 123 };
      jest.spyOn(userManagementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userManagement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userManagementService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserRole', () => {
      it('Should forward to userRoleService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userRoleService, 'compareUserRole');
        comp.compareUserRole(entity, entity2);
        expect(userRoleService.compareUserRole).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUserPerms', () => {
      it('Should forward to userPermsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userPermsService, 'compareUserPerms');
        comp.compareUserPerms(entity, entity2);
        expect(userPermsService.compareUserPerms).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
