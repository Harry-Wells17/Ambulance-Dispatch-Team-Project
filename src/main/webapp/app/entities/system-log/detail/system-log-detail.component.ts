import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISystemLog } from '../system-log.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-system-log-detail',
  templateUrl: './system-log-detail.component.html',
})
export class SystemLogDetailComponent implements OnInit {
  systemLog: ISystemLog | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemLog }) => {
      this.systemLog = systemLog;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
