import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResourceBreaks } from '../resource-breaks.model';

@Component({
  selector: 'jhi-resource-breaks-detail',
  templateUrl: './resource-breaks-detail.component.html',
})
export class ResourceBreaksDetailComponent implements OnInit {
  resourceBreaks: IResourceBreaks | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceBreaks }) => {
      this.resourceBreaks = resourceBreaks;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
