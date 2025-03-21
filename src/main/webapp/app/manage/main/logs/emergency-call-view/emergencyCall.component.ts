import { Component, Input, OnInit } from '@angular/core';
import { EditCallsComponent } from '../../calls/edit/editCall.component';
import { NgIf } from '@angular/common';

@Component({
  standalone: true,
  selector: 'logs-emergency-component',
  templateUrl: './emergencyCall.component.html',
  imports: [EditCallsComponent, NgIf],
})
export class EmergencyLogCall {
  @Input() callId: number;
  view: boolean = false;

  closeNotBound: Function = this.close.bind(this);

  open() {
    this.view = true;
  }

  close() {
    this.view = false;
  }
}
