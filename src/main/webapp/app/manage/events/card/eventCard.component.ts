import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import { IEvent } from 'app/entities/event/event.model';

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

export { TimeSince };
@Component({
  standalone: true,
  selector: 'event-card',
  templateUrl: './eventCard.component.html',
  styles: [],
  imports: [RouterLink, RouterOutlet, CommonModule, RouterModule],
})
export class EventCard {
  @Input() dataIn!: IEvent;
  data: IEvent & { howLongAgo: string } = {} as any;

  ngOnInit() {
    this.data = {
      ...this.dataIn,
      howLongAgo: TimeSince({ time: new Date(this.dataIn.created ?? '') }),
    };
  }
}
