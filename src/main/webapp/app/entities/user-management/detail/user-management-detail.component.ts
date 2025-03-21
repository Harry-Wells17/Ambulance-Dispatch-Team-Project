import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserManagement } from '../user-management.model';

@Component({
  selector: 'jhi-user-management-detail',
  templateUrl: './user-management-detail.component.html',
})
export class UserManagementDetailComponent implements OnInit {
  userManagement: IUserManagement | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userManagement }) => {
      this.userManagement = userManagement;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
