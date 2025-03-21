import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: true,
  template: `
    <div>
      <h2>Welcome {{ name }}</h2>
      <button type="button" (click)="update()">Update Page!</button>
    </div>
  `,
  styles: [],
})
export class NewPage {
  public name = 'John';
  public update() {
    this.name = 'Paul';
  }
}
