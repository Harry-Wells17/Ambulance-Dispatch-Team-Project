import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { UserRoleFormService, UserRoleFormGroup } from './user-role-form.service';
import { IUserRole } from '../user-role.model';
import { UserRoleService } from '../service/user-role.service';
import { Role } from 'app/entities/enumerations/role.model';

@Component({
  selector: 'jhi-user-role-update',
  templateUrl: './user-role-update.component.html',
})
export class UserRoleUpdateComponent implements OnInit {
  isSaving = false;
  userRole: IUserRole | null = null;
  roleValues = Object.keys(Role);

  editForm: UserRoleFormGroup = this.userRoleFormService.createUserRoleFormGroup();

  constructor(
    protected userRoleService: UserRoleService,
    protected userRoleFormService: UserRoleFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userRole }) => {
      this.userRole = userRole;
      if (userRole) {
        this.updateForm(userRole);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userRole = this.userRoleFormService.getUserRole(this.editForm);
    if (userRole.id !== null) {
      this.subscribeToSaveResponse(this.userRoleService.update(userRole));
    } else {
      this.subscribeToSaveResponse(this.userRoleService.create(userRole));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserRole>>): void {
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

  protected updateForm(userRole: IUserRole): void {
    this.userRole = userRole;
    this.userRoleFormService.resetForm(this.editForm, userRole);
  }
}
