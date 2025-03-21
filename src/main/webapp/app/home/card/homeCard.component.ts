import { Component, Input } from '@angular/core';
import { Call } from '../home.component';

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

@Component({
  standalone: true,
  selector: 'home-card',
  templateUrl: './homeCard.component.html',
  styles: [],
})
export class HomeCard {
  @Input() dataIn!: Call;

  public data!: Call & { color: string; howLongAgo: string; borderColor: string };

  ngOnInit() {
    const color =
      this.dataIn.cat === 'CAT 1'
        ? '#b91c1c'
        : this.dataIn.cat === 'CAT 2'
        ? '#991b1b'
        : this.dataIn.cat === 'CAT 3'
        ? '#7f1d1d'
        : this.dataIn.cat === 'CAT 4'
        ? '#1e40af'
        : this.dataIn.cat === 'CAT 5'
        ? '#166534'
        : '#9a3412';

    const border =
      this.dataIn.cat === 'CAT 1'
        ? '#a31818'
        : this.dataIn.cat === 'CAT 2'
        ? '#7a1515'
        : this.dataIn.cat === 'CAT 3'
        ? '#631717'
        : this.dataIn.cat === 'CAT 4'
        ? '#16318a'
        : this.dataIn.cat === 'CAT 5'
        ? '#14532d'
        : '#9a3412';

    this.data = {
      ...this.dataIn,
      color,
      borderColor: `solid 2px ${border}`,
      howLongAgo: TimeSince({ time: new Date(this.dataIn.time) }),
    };
  }
}
