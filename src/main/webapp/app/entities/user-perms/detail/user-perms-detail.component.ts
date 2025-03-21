import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserPerms } from '../user-perms.model';

@Component({
  selector: 'jhi-user-perms-detail',
  templateUrl: './user-perms-detail.component.html',
})
export class UserPermsDetailComponent implements OnInit {
  userPerms: IUserPerms | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userPerms }) => {
      this.userPerms = userPerms;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
