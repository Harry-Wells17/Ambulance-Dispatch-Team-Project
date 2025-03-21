import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ResourceBreaksFormService, ResourceBreaksFormGroup } from './resource-breaks-form.service';
import { IResourceBreaks } from '../resource-breaks.model';
import { ResourceBreaksService } from '../service/resource-breaks.service';

@Component({
  selector: 'jhi-resource-breaks-update',
  templateUrl: './resource-breaks-update.component.html',
})
export class ResourceBreaksUpdateComponent implements OnInit {
  isSaving = false;
  resourceBreaks: IResourceBreaks | null = null;

  editForm: ResourceBreaksFormGroup = this.resourceBreaksFormService.createResourceBreaksFormGroup();

  constructor(
    protected resourceBreaksService: ResourceBreaksService,
    protected resourceBreaksFormService: ResourceBreaksFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceBreaks }) => {
      this.resourceBreaks = resourceBreaks;
      if (resourceBreaks) {
        this.updateForm(resourceBreaks);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resourceBreaks = this.resourceBreaksFormService.getResourceBreaks(this.editForm);
    if (resourceBreaks.id !== null) {
      this.subscribeToSaveResponse(this.resourceBreaksService.update(resourceBreaks));
    } else {
      this.subscribeToSaveResponse(this.resourceBreaksService.create(resourceBreaks));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResourceBreaks>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(resourceBreaks: IResourceBreaks): void {
    this.resourceBreaks = resourceBreaks;
    this.resourceBreaksFormService.resetForm(this.editForm, resourceBreaks);
  }
}
