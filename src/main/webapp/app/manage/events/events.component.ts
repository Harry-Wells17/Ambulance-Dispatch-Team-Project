import { Component, OnInit } from '@angular/core';
import { ASC, DESC } from 'app/config/navigation.constants';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { EntityArrayResponseType, SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { Observable, tap } from 'rxjs';
import { EventCard } from './card/eventCard.component';
import { NgForOf, NgIf } from '@angular/common';
import { RouterLink, RouterModule } from '@angular/router';
import { EventFormGroup, EventFormService } from 'app/entities/event/update/event-form.service';
import { ReactiveFormsModule } from '@angular/forms';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { AddLogs } from 'app/shared/logs';
import { Title } from '@angular/platform-browser';

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './events.component.html',
  imports: [EventCard, NgForOf, NgIf, RouterLink, ReactiveFormsModule, RouterModule],
})
export class EventPage implements OnInit {
  events?: IEvent[] = [];
  isLoading = false;

  predicate = 'id';
  ascending = true;

  showCreateNew = false;

  showSuccess = false;
  showError = false;
  loadingNew = false;

  event: IEvent | null = null;
  editForm: EventFormGroup = this.eventFormService.createEventFormGroup();

  showCreateNewEvent() {
    this.showCreateNew = true;
  }
  hideCreateNewEvent() {
    this.showCreateNew = false;
  }

  account: Account | null;

  constructor(
    protected eventService: EventService,
    protected eventFormService: EventFormService,
    private accountService: AccountService,
    protected logService: SystemLogService,
    private titleService: Title
  ) {
    this.titleService.setTitle('Select an Event');
  }

  create() {
    if (this.loadingNew) return;
    this.loadingNew = true;

    const event = this.eventFormService.getEvent(this.editForm);
    event.created = new Date().toISOString();
    //event.eventId = '0';

    if (event.id == null) {
      this.eventService.create(event).subscribe(
        x => {
          this.showSuccess = true;
          this.showCreateNew = false;
          this.loadingNew = false;
          setTimeout(() => {
            this.showSuccess = false;
          }, 3000);

          AddLogs({
            accountsService: this.accountService,
            eventId: x.body?.id as any,
            logService: this.logService,
            logType: 'CONTROLROOM',
            messageContent: `Created new event with name "${x.body?.eventName}"!`,
          });

          this.queryBackend().subscribe(x => {
            this.events = x.body as any;
          });
        },
        err => {
          this.showError = true;

          this.loadingNew = false;
          setTimeout(() => {
            this.showError = false;
          }, 3000);
        }
      );
    }
  }

  protected updateForm(event: IEvent): void {
    this.event = event;
    this.eventFormService.resetForm(this.editForm, event);
  }

  ngOnInit(): void {
    (async () => {
      this.queryBackend().subscribe(x => {
        this.events = x.body as any;
      });
    })();

    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }

  protected queryBackend(predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.eventService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
