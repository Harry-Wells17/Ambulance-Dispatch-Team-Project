import { NgForOf, NgIf } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import { ISystemLog } from 'app/entities/system-log/system-log.model';
import dayjs from 'dayjs/esm';
import { EmergencyLogCall } from './emergency-call-view/emergencyCall.component';
import { AddLogs } from 'app/shared/logs';
import { AccountService } from 'app/core/auth/account.service';
import { IUser } from 'app/entities/user/user.model';

declare const Buffer;
@Component({
  standalone: true,
  selector: 'logs-component',
  templateUrl: './logs.component.html',
  imports: [NgForOf, EmergencyLogCall, NgIf],
})
export class LogsComponent implements OnDestroy {
  logs: ISystemLog[] = [];
  details: string = '';
  showSuccess = false;
  showAddLogs = false;

  private interval: number | null = null;

  sendLog = this.addCallLog.bind(this);
  updateText = this.changeDetails.bind(this);
  downloadLogsBound = this.downloadLogs.bind(this);

  constructor(private systemLogService: SystemLogService, private activatedRoute: ActivatedRoute, private accountService: AccountService) {
    this.load();
    setInterval(() => {
      this.load();
    }, 10000);
  }

  ngOnDestroy(): void {
    if (this.interval !== null) {
      clearInterval(this.interval);
    }
  }

  dateFix(x: dayjs.Dayjs | null | undefined) {
    return x?.format('YYYY-MM-DD HH:mm:ss');
  }

  getColor(x: string) {
    if (x === 'BREAKLOG') return 'rgb(220 38 38)';
    if (x === 'CALL') return 'rgb(38 220 38)';
    if (x === 'CONTROLROOM') return 'rgb(96 165 250)';
    if (x === 'RESOURCE') return 'rgb(220 220 38)';
    return 'white';
  }

  open() {
    this.showAddLogs = true;
  }
  close() {
    this.showAddLogs = false;
  }

  changeDetails(x: any) {
    this.details = x.value;
  }

  downloadLogs() {
    this.systemLogService
      .query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      })
      .subscribe(x => {
        let newer = (x.body as ISystemLog[]).sort((a, b) => {
          return dayjs(b.createdAt).unix() - dayjs(a.createdAt).unix();
        });

        //turn into csv
        let out = 'ID, Date and Time, Created By, Log Type, Message, Emergency Call Id, Event Id';
        newer.forEach(x => {
          out += `\n${x.id}, ${this.dateFix(x.createdAt)}, ${(x?.createdBy as any)?.login}, ${x.logType}, ${x.messageContent}, ${
            (x?.emergencyCall as any)?.id
          }, ${(x?.event as any)?.id}`;
        });

        let outBuffer = btoa(out);

        let w = window.open('data:application/octet-stream;charset=utf-8;base64,' + outBuffer);
      });
  }

  addCallLog() {
    (async () => {
      await AddLogs({
        accountsService: this.accountService,
        eventId: parseInt(this.activatedRoute.snapshot.params.id, 10),
        logService: this.systemLogService,
        logType: 'CONTROLROOM',
        messageContent: `${this.details}`,
      });

      console.log(this.details);
      this.close();
      this.details = '';
      this.showSuccess = true;
      setTimeout(() => {
        this.showSuccess = false;
      }, 2000);
    })();
  }

  load() {
    this.systemLogService
      .query({
        'eventId.equals': parseInt(this.activatedRoute.snapshot.params.id, 10),
      })
      .subscribe(x => {
        let newer = (x.body as ISystemLog[]).sort((a, b) => {
          return dayjs(b.createdAt).unix() - dayjs(a.createdAt).unix();
        });

        if (JSON.stringify(newer) === JSON.stringify(this.logs)) return;
        this.logs = newer;
      });
  }
}
