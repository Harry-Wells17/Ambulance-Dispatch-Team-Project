import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserExist } from '../user-exist.model';

@Component({
  selector: 'jhi-user-exist-detail',
  templateUrl: './user-exist-detail.component.html',
})
export class UserExistDetailComponent implements OnInit {
  userExist: IUserExist | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userExist }) => {
      this.userExist = userExist;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
