import { NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { IResourceAssigned } from 'app/entities/resource-assigned/resource-assigned.model';

@Component({
  standalone: true,
  selector: 'resources-overview-call-card',
  templateUrl: './callResourceCard.component.html',
  imports: [NgForOf],
  changeDetection: ChangeDetectionStrategy.Default,
})
export class ResourceOverviewCallCard implements OnInit {
  @Input() call: {
    assinged: IEmergencyCall;
    call: IResourceAssigned;
  };

  minStatus: string = '';

  @Input() index: number;

  timeAgo: string = '';

  ngOnInit() {
    this.timeAgo = TimeSince({ time: new Date(`${this.call.call.callRecievedTime}`) });

    console.log(this.call.call);
    if (this.call.call.greenTime !== undefined) {
      this.minStatus = 'GREEN';
    } else if (this.call.call.clearHospitalTime !== undefined) {
      this.minStatus = 'CLEARED';
    } else if (this.call.call.arrivedHospitalTime !== undefined) {
      this.minStatus = 'ARRIVED HOSP';
    } else if (this.call.call.leftSceneTime !== undefined) {
      this.minStatus = 'LEFT SCENE';
    } else if (this.call.call.onSceneTime !== undefined) {
      this.minStatus = 'ON SCENE';
    } else {
      this.minStatus = 'DISPATCHED';
    }
  }
}

const TimeSince = ({ time }: { time: Date }) => {
  const diff = Date.now() - time.getTime();
  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);

  if (hours > 0) {
    return `${hours}h`;
  } else if (minutes > 0) {
    return `${minutes}m`;
  } else {
    return `${seconds}s`;
  }
};
