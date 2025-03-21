import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { UserPermsFormService, UserPermsFormGroup } from './user-perms-form.service';
import { IUserPerms } from '../user-perms.model';
import { UserPermsService } from '../service/user-perms.service';
import { Perms } from 'app/entities/enumerations/perms.model';

@Component({
  selector: 'jhi-user-perms-update',
  templateUrl: './user-perms-update.component.html',
})
export class UserPermsUpdateComponent implements OnInit {
  isSaving = false;
  userPerms: IUserPerms | null = null;
  permsValues = Object.keys(Perms);

  editForm: UserPermsFormGroup = this.userPermsFormService.createUserPermsFormGroup();

  constructor(
    protected userPermsService: UserPermsService,
    protected userPermsFormService: UserPermsFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userPerms }) => {
      this.userPerms = userPerms;
      if (userPerms) {
        this.updateForm(userPerms);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userPerms = this.userPermsFormService.getUserPerms(this.editForm);
    if (userPerms.id !== null) {
      this.subscribeToSaveResponse(this.userPermsService.update(userPerms));
    } else {
      this.subscribeToSaveResponse(this.userPermsService.create(userPerms));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserPerms>>): void {
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

  protected updateForm(userPerms: IUserPerms): void {
    this.userPerms = userPerms;
    this.userPermsFormService.resetForm(this.editForm, userPerms);
  }
}
