import { Component, Input } from '@angular/core';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { CreateAlert } from '../components/alert';

@Component({
  standalone: true,
  selector: 'shift-page',
  templateUrl: './shiftpage.component.html',
  imports: [],
})
export class ShiftPage {
  @Input() returnInfo: (e: IEvent) => void;

  setId: number | null = null;

  boundChange = this.changeBound.bind(this);
  boundSearch = this.searchForEvent.bind(this);

  changeBound(e: string): void {
    this.setId = parseInt(e, 10);
  }

  constructor(private eventService: EventService) {}

  searchForEvent(): void {
    this.eventService.find(this.setId ?? -1).subscribe({
      next: x => {
        if (x.body) {
          this.returnInfo(x.body);
        }
      },
      error: x => {
        if (x.status === 404) {
          CreateAlert({
            topText: 'Error',
            bottomText: 'Event not found',
            buttonNum: 1,
            onClick: () => {},
          });

          return;
        }

        if (x.status === 500) {
          CreateAlert({
            topText: 'Error',
            bottomText: 'Server error',
            buttonNum: 1,
            onClick: () => {},
          });

          return;
        }
      },
    });
  }
}
