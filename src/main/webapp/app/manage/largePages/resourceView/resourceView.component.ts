import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { ResourceComponent } from 'app/manage/main/resources/resources.component';

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './resourceView.component.html',
  imports: [ResourceComponent, RouterModule, NgIf],
})
export class LargeResourceViewComponent implements OnInit {
  account: Account | null;

  constructor(private accountService: AccountService, private titleService: Title) {
    this.titleService.setTitle(`AmbuDispatch - Resources`);
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }
}
