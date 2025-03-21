import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEmergencyCall } from '../emergency-call.model';

@Component({
  selector: 'jhi-emergency-call-detail',
  templateUrl: './emergency-call-detail.component.html',
})
export class EmergencyCallDetailComponent implements OnInit {
  emergencyCall: IEmergencyCall | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ emergencyCall }) => {
      this.emergencyCall = emergencyCall;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
