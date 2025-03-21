import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ResourceBreaksFormService } from './resource-breaks-form.service';
import { ResourceBreaksService } from '../service/resource-breaks.service';
import { IResourceBreaks } from '../resource-breaks.model';

import { ResourceBreaksUpdateComponent } from './resource-breaks-update.component';

describe('ResourceBreaks Management Update Component', () => {
  let comp: ResourceBreaksUpdateComponent;
  let fixture: ComponentFixture<ResourceBreaksUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let resourceBreaksFormService: ResourceBreaksFormService;
  let resourceBreaksService: ResourceBreaksService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ResourceBreaksUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ResourceBreaksUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceBreaksUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    resourceBreaksFormService = TestBed.inject(ResourceBreaksFormService);
    resourceBreaksService = TestBed.inject(ResourceBreaksService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const resourceBreaks: IResourceBreaks = { id: 456 };

      activatedRoute.data = of({ resourceBreaks });
      comp.ngOnInit();

      expect(comp.resourceBreaks).toEqual(resourceBreaks);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceBreaks>>();
      const resourceBreaks = { id: 123 };
      jest.spyOn(resourceBreaksFormService, 'getResourceBreaks').mockReturnValue(resourceBreaks);
      jest.spyOn(resourceBreaksService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceBreaks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceBreaks }));
      saveSubject.complete();

      // THEN
      expect(resourceBreaksFormService.getResourceBreaks).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(resourceBreaksService.update).toHaveBeenCalledWith(expect.objectContaining(resourceBreaks));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceBreaks>>();
      const resourceBreaks = { id: 123 };
      jest.spyOn(resourceBreaksFormService, 'getResourceBreaks').mockReturnValue({ id: null });
      jest.spyOn(resourceBreaksService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceBreaks: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceBreaks }));
      saveSubject.complete();

      // THEN
      expect(resourceBreaksFormService.getResourceBreaks).toHaveBeenCalled();
      expect(resourceBreaksService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceBreaks>>();
      const resourceBreaks = { id: 123 };
      jest.spyOn(resourceBreaksService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceBreaks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(resourceBreaksService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
