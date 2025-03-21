import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResourceAssigned } from '../resource-assigned.model';

@Component({
  selector: 'jhi-resource-assigned-detail',
  templateUrl: './resource-assigned-detail.component.html',
})
export class ResourceAssignedDetailComponent implements OnInit {
  resourceAssigned: IResourceAssigned | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceAssigned }) => {
      this.resourceAssigned = resourceAssigned;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
