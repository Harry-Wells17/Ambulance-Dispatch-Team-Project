import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { BreaksComponent } from 'app/manage/main/breaks/breaks.component';

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './breaksView.component.html',
  imports: [BreaksComponent, NgIf, RouterModule],
})
export class LargeBreaksViewComponent implements OnInit {
  account: Account | null;

  constructor(private accountService: AccountService, private titleService: Title) {
    this.titleService.setTitle(`AmbuDispatch - Breaks`);
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }
}
