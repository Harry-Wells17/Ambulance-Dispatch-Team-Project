import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserExistFormService, UserExistFormGroup } from './user-exist-form.service';
import { IUserExist } from '../user-exist.model';
import { UserExistService } from '../service/user-exist.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-user-exist-update',
  templateUrl: './user-exist-update.component.html',
})
export class UserExistUpdateComponent implements OnInit {
  isSaving = false;
  userExist: IUserExist | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: UserExistFormGroup = this.userExistFormService.createUserExistFormGroup();

  constructor(
    protected userExistService: UserExistService,
    protected userExistFormService: UserExistFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userExist }) => {
      this.userExist = userExist;
      if (userExist) {
        this.updateForm(userExist);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userExist = this.userExistFormService.getUserExist(this.editForm);
    if (userExist.id !== null) {
      this.subscribeToSaveResponse(this.userExistService.update(userExist));
    } else {
      this.subscribeToSaveResponse(this.userExistService.create(userExist));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserExist>>): void {
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

  protected updateForm(userExist: IUserExist): void {
    this.userExist = userExist;
    this.userExistFormService.resetForm(this.editForm, userExist);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userExist.createdBy);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userExist?.createdBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
