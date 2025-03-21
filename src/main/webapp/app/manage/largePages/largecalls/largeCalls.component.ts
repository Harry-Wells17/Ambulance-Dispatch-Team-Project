import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { CallsComponent } from 'app/manage/main/calls/call.component';

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './largeCalls.component.html',
  imports: [CallsComponent, NgIf, RouterModule],
})
export class LargeCallsComponent implements OnInit {
  account: Account | null;

  constructor(private accountService: AccountService, private titleService: Title) {
    this.titleService.setTitle(`AmbuDispatch - Calls`);
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }
}
