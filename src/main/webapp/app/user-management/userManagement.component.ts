import { Component } from '@angular/core';
import { RouterLink, RouterModule } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { UserManagementService } from 'app/admin/user-management/service/user-management.service';
import dayjs from 'dayjs/esm';
import { TimeSince } from 'app/manage/events/card/eventCard.component';
import { RegisterService } from 'app/account/register/register.service';
import { v4 } from 'uuid';
import { UserExistService } from 'app/entities/user-exist/service/user-exist.service';

export interface RequestType {
  id: number;
  login: string;
  firstName: string;
  lastName: string;
  email: string;
  imageUrl: string;
  activated: boolean;
  langKey: string;
  createdBy: string;
  createdDate: null;
  lastModifiedBy: string;
  lastModifiedDate: null;
  authorities: string[];
}

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './userManagement.component.html',
  imports: [RouterLink, RouterModule, NgForOf, NgIf],
})
export class userManagement {
  account: Account | undefined | null;

  users: RequestType[] = [];

  showAddUser = false;

  username = '';
  password = '';
  newRole = '';

  showSuccess = false;
  showError = false;

  loadBound = this.load.bind(this);
  changeRoleBound = this.changeRole.bind(this);
  openBound = this.open.bind(this);
  closeBound = this.close.bind(this);
  changeUsernameBound = this.changeUsername.bind(this);
  changePasswordBound = this.changePassword.bind(this);
  createNewUserBound = this.createNewUser.bind(this);
  changeNewRoleBound = this.changeNewRole.bind(this);
  changeActiveBound = this.changeActive.bind(this);
  removeBound = this.remove.bind(this);

  constructor(
    private accountService: AccountService,
    private userService: UserManagementService,
    private registerService: RegisterService,
    private UserExistService: UserExistService
  ) {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
    this.load();
  }

  changeNewRole(x: string) {
    this.newRole = x;
  }

  changeUsername(x: string) {
    console.log(x);
    this.username = x.toLowerCase();
  }

  changePassword(x: string) {
    this.password = x;
  }

  createNewUser() {
    this.registerService
      .save({ login: this.username, password: this.password, email: v4() + '@' + v4() + '.com', langKey: 'en' })
      .subscribe({
        next: () => {
          this.userService.find(this.username).subscribe({
            next: user => {
              this.userService.update({ ...user, authorities: [this.newRole], activated: true }).subscribe({
                next: () => {
                  this.load();
                  this.close();
                },
                error: () => {
                  this.showError = true;
                  setTimeout(() => {
                    this.showError = false;
                  }, 3000);
                },
              });
            },
            error: () => {
              this.showError = true;
              setTimeout(() => {
                this.showError = false;
              }, 3000);
            },
          });
        },
        error: () => {
          this.showError = true;
          setTimeout(() => {
            this.showError = false;
          }, 3000);
        },
      });
  }

  time(x: dayjs.Dayjs | null | undefined): string {
    if (x === null) {
      return 'unknown';
    }
    return TimeSince({
      time: new Date(`${x}`),
    });
  }

  changeRole(userId: string, role: string) {
    if (userId === this.account?.login) {
      this.showError = true;
      setTimeout(() => {
        this.showError = false;
      }, 3000);
      return;
    }
    this.userService.find(userId).subscribe(user => {
      this.userService.update({ ...user, authorities: [role] }).subscribe(() => {
        this.load();
      });
    });
  }

  stripRole(x: string[]) {
    return x.map(e => e.replace('ROLE_', '')).join(', ');
  }

  close() {
    this.showAddUser = false;
  }
  open() {
    this.showAddUser = true;
  }

  changeActive(userId: string, checked: boolean) {
    if (userId === this.account?.login) {
      this.showError = true;
      setTimeout(() => {
        this.showError = false;
      }, 3000);
      return;
    }
    this.userService.find(userId).subscribe(user => {
      this.userService.update({ ...user, activated: checked }).subscribe(() => {
        this.load();
      });
    });
  }

  remove(login: RequestType) {
    if (login.login === this.account?.login) {
      this.showError = true;
      setTimeout(() => {
        this.showError = false;
      }, 3000);
      return;
    }
    this.UserExistService.create({
      id: null,
      exist: false,
      createdBy: { login: login.login, id: login.id },
    }).subscribe({
      next: () => {
        this.userService
          .update({
            ...login,
            activated: false,
          } as any)
          .subscribe({
            next: x => {
              this.load();
              this.showSuccess = true;
              setTimeout(() => {
                this.showSuccess = false;
              }, 3000);
            },

            error: () => {
              this.showError = true;
              setTimeout(() => {
                this.showError = false;
              }, 3000);
            },
          });
      },
      error: () => {
        this.showError = true;
        setTimeout(() => {
          this.showError = false;
        }, 3000);
      },
    });
  }

  load() {
    this.userService.query().subscribe(users => {
      this.UserExistService.query().subscribe(exists => {
        let list: RequestType[] = [];
        (users.body ?? []).forEach(element => {
          let item = exists.body?.find(x => x.createdBy?.id === element.id);
          if (item?.exist === false) {
            return;
          }
          list.push(element as any);
        });

        this.users = list;
      });
    });
  }
}
