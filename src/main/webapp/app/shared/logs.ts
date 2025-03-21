/*
 CONTROLROOM (Control_Room), 
    CALL (Call_Log),
    RESOURCE (Resource_Log),
    BREAKLOG (Break_Log)*/

import { AccountService } from 'app/core/auth/account.service';
import { SystemLogService } from 'app/entities/system-log/service/system-log.service';
import dayjs from 'dayjs/esm';

export async function AddLogs({
  accountsService,
  eventId,
  logService,
  logType,
  messageContent,
  emergencyCallId,
}: {
  logType: 'CONTROLROOM' | 'CALL' | 'RESOURCE' | 'BREAKLOG';
  messageContent: string;
  emergencyCallId?: number;
  eventId: number;
  accountsService: AccountService;
  logService: SystemLogService;
}) {
  accountsService.identity().subscribe(x => {
    console.log(x);
    console.log('Creating log');
    logService
      .create({
        logType: logType as any,
        messageContent,
        emergencyCall: emergencyCallId ? { id: emergencyCallId } : null,
        event: { id: eventId },
        createdAt: dayjs() as any,
        createdBy: { id: (x as any)?.id, login: x?.login },
        id: null,
      })
      .subscribe(x => {
        console.log(x);
      });
  });
}
