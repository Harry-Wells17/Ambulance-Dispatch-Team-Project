import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserManagementFormService, UserManagementFormGroup } from './user-management-form.service';
import { IUserManagement } from '../user-management.model';
import { UserManagementService } from '../service/user-management.service';
import { IUserRole } from 'app/entities/user-role/user-role.model';
import { UserRoleService } from 'app/entities/user-role/service/user-role.service';
import { IUserPerms } from 'app/entities/user-perms/user-perms.model';
import { UserPermsService } from 'app/entities/user-perms/service/user-perms.service';

@Component({
  selector: 'jhi-user-management-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
  isSaving = false;
  userManagement: IUserManagement | null = null;

  userRolesCollection: IUserRole[] = [];
  userPermsSharedCollection: IUserPerms[] = [];

  editForm: UserManagementFormGroup = this.userManagementFormService.createUserManagementFormGroup();

  constructor(
    protected userManagementService: UserManagementService,
    protected userManagementFormService: UserManagementFormService,
    protected userRoleService: UserRoleService,
    protected userPermsService: UserPermsService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUserRole = (o1: IUserRole | null, o2: IUserRole | null): boolean => this.userRoleService.compareUserRole(o1, o2);

  compareUserPerms = (o1: IUserPerms | null, o2: IUserPerms | null): boolean => this.userPermsService.compareUserPerms(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userManagement }) => {
      this.userManagement = userManagement;
      if (userManagement) {
        this.updateForm(userManagement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userManagement = this.userManagementFormService.getUserManagement(this.editForm);
    if (userManagement.id !== null) {
      this.subscribeToSaveResponse(this.userManagementService.update(userManagement));
    } else {
      this.subscribeToSaveResponse(this.userManagementService.create(userManagement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserManagement>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userManagement: IUserManagement): void {
    this.userManagement = userManagement;
    this.userManagementFormService.resetForm(this.editForm, userManagement);

    this.userRolesCollection = this.userRoleService.addUserRoleToCollectionIfMissing<IUserRole>(
      this.userRolesCollection,
      userManagement.userRole
    );
    this.userPermsSharedCollection = this.userPermsService.addUserPermsToCollectionIfMissing<IUserPerms>(
      this.userPermsSharedCollection,
      userManagement.userPerms
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userRoleService
      .query({ 'userManagementId.specified': 'false' })
      .pipe(map((res: HttpResponse<IUserRole[]>) => res.body ?? []))
      .pipe(
        map((userRoles: IUserRole[]) =>
          this.userRoleService.addUserRoleToCollectionIfMissing<IUserRole>(userRoles, this.userManagement?.userRole)
        )
      )
      .subscribe((userRoles: IUserRole[]) => (this.userRolesCollection = userRoles));

    this.userPermsService
      .query()
      .pipe(map((res: HttpResponse<IUserPerms[]>) => res.body ?? []))
      .pipe(
        map((userPerms: IUserPerms[]) =>
          this.userPermsService.addUserPermsToCollectionIfMissing<IUserPerms>(userPerms, this.userManagement?.userPerms)
        )
      )
      .subscribe((userPerms: IUserPerms[]) => (this.userPermsSharedCollection = userPerms));
  }
}
