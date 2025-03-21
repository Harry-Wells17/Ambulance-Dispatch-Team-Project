import { Component, OnInit } from '@angular/core';
import { ShiftPage } from './shiftpage/shiftpage.component';
import { ResourceType } from './resourceType/ResourceType.component';
import { MainMobileComp } from './mainPage/mainPage.component';
import { NgIf } from '@angular/common';
import { IEvent } from 'app/entities/event/event.model';
import { IResource } from 'app/entities/resource/resource.model';
import { RouterModule } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { Title } from '@angular/platform-browser';

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './main.component.html',
  imports: [ShiftPage, ResourceType, MainMobileComp, NgIf, RouterModule],
})
export class MobileSim implements OnInit {
  stage = 0;
  event: IEvent | null = null;

  resourceBreak: IResource | null = null;

  account: Account | null;

  constructor(private accountService: AccountService, private title: Title) {
    this.title.setTitle('AmbuDispatch - Mobile Simulation');
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }

  stripRole(x: string): string {
    return x.split('_')[1];
  }

  setEventBound = this.setEvent.bind(this);
  changeEventBound = this.changeEvent.bind(this);
  setResourceBreakBound = this.setResourceBreak.bind(this);
  closeShiftBound = this.closeShift.bind(this);

  setEvent(e: IEvent): void {
    this.event = e;
    this.stage = 1;
  }

  setResourceBreak(e: IResource): void {
    this.stage = 2;
    this.resourceBreak = e;
  }

  changeEvent() {
    this.stage = 0;
  }

  closeShift() {
    this.stage = 0;
    this.resourceBreak = null;
  }
}
