import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ASC, DESC } from 'app/config/navigation.constants';
import { IEvent } from 'app/entities/event/event.model';
import { EntityResponseType, EventService } from 'app/entities/event/service/event.service';
import { Observable, tap } from 'rxjs';
import { LogsComponent } from './logs/logs.component';
import { ResourceComponent } from './resources/resources.component';
import { CallsComponent } from './calls/call.component';
import { BreaksComponent } from './breaks/breaks.component';
import { ResourceAssignedComponent } from './resourceAssigned/resourceAssigned.component';
import { Title } from '@angular/platform-browser';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { NgIf } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './main.component.html',
  imports: [LogsComponent, ResourceComponent, CallsComponent, BreaksComponent, ResourceAssignedComponent, NgIf, RouterModule],
})
export class MainEventsComponent implements OnInit {
  event: IEvent | null = null;
  isLoading = false;

  predicate = 'id';
  ascending = true;

  link: string = '';

  account: Account | null;

  constructor(
    protected eventService: EventService,
    private activatedRoute: ActivatedRoute,
    private titleService: Title,
    private accountService: AccountService
  ) {
    this.link = this.activatedRoute.snapshot.url.join('/');
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
    this.queryBackend(this.activatedRoute.snapshot.params.id).subscribe(x => {
      this.event = x?.body as any;
      this.titleService.setTitle(`AmbuDispatch - ${x.body?.eventName}`);
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  protected queryBackend(id: string): Observable<EntityResponseType> {
    this.isLoading = true;

    return this.eventService.find(parseInt(id, 10)).pipe(tap(() => (this.isLoading = false)));
  }
}
