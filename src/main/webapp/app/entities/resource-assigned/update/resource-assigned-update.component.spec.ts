import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ResourceAssignedFormService } from './resource-assigned-form.service';
import { ResourceAssignedService } from '../service/resource-assigned.service';
import { IResourceAssigned } from '../resource-assigned.model';
import { IResource } from 'app/entities/resource/resource.model';
import { ResourceService } from 'app/entities/resource/service/resource.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';

import { ResourceAssignedUpdateComponent } from './resource-assigned-update.component';

describe('ResourceAssigned Management Update Component', () => {
  let comp: ResourceAssignedUpdateComponent;
  let fixture: ComponentFixture<ResourceAssignedUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let resourceAssignedFormService: ResourceAssignedFormService;
  let resourceAssignedService: ResourceAssignedService;
  let resourceService: ResourceService;
  let emergencyCallService: EmergencyCallService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ResourceAssignedUpdateComponent],
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
      .overrideTemplate(ResourceAssignedUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceAssignedUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    resourceAssignedFormService = TestBed.inject(ResourceAssignedFormService);
    resourceAssignedService = TestBed.inject(ResourceAssignedService);
    resourceService = TestBed.inject(ResourceService);
    emergencyCallService = TestBed.inject(EmergencyCallService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Resource query and add missing value', () => {
      const resourceAssigned: IResourceAssigned = { id: 456 };
      const resource: IResource = { id: 72977 };
      resourceAssigned.resource = resource;

      const resourceCollection: IResource[] = [{ id: 71732 }];
      jest.spyOn(resourceService, 'query').mockReturnValue(of(new HttpResponse({ body: resourceCollection })));
      const additionalResources = [resource];
      const expectedCollection: IResource[] = [...additionalResources, ...resourceCollection];
      jest.spyOn(resourceService, 'addResourceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ resourceAssigned });
      comp.ngOnInit();

      expect(resourceService.query).toHaveBeenCalled();
      expect(resourceService.addResourceToCollectionIfMissing).toHaveBeenCalledWith(
        resourceCollection,
        ...additionalResources.map(expect.objectContaining)
      );
      expect(comp.resourcesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call EmergencyCall query and add missing value', () => {
      const resourceAssigned: IResourceAssigned = { id: 456 };
      const emergencyCall: IEmergencyCall = { id: 18593 };
      resourceAssigned.emergencyCall = emergencyCall;

      const emergencyCallCollection: IEmergencyCall[] = [{ id: 47219 }];
      jest.spyOn(emergencyCallService, 'query').mockReturnValue(of(new HttpResponse({ body: emergencyCallCollection })));
      const additionalEmergencyCalls = [emergencyCall];
      const expectedCollection: IEmergencyCall[] = [...additionalEmergencyCalls, ...emergencyCallCollection];
      jest.spyOn(emergencyCallService, 'addEmergencyCallToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ resourceAssigned });
      comp.ngOnInit();

      expect(emergencyCallService.query).toHaveBeenCalled();
      expect(emergencyCallService.addEmergencyCallToCollectionIfMissing).toHaveBeenCalledWith(
        emergencyCallCollection,
        ...additionalEmergencyCalls.map(expect.objectContaining)
      );
      expect(comp.emergencyCallsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const resourceAssigned: IResourceAssigned = { id: 456 };
      const resource: IResource = { id: 6323 };
      resourceAssigned.resource = resource;
      const emergencyCall: IEmergencyCall = { id: 44303 };
      resourceAssigned.emergencyCall = emergencyCall;

      activatedRoute.data = of({ resourceAssigned });
      comp.ngOnInit();

      expect(comp.resourcesSharedCollection).toContain(resource);
      expect(comp.emergencyCallsSharedCollection).toContain(emergencyCall);
      expect(comp.resourceAssigned).toEqual(resourceAssigned);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceAssigned>>();
      const resourceAssigned = { id: 123 };
      jest.spyOn(resourceAssignedFormService, 'getResourceAssigned').mockReturnValue(resourceAssigned);
      jest.spyOn(resourceAssignedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceAssigned });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceAssigned }));
      saveSubject.complete();

      // THEN
      expect(resourceAssignedFormService.getResourceAssigned).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(resourceAssignedService.update).toHaveBeenCalledWith(expect.objectContaining(resourceAssigned));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceAssigned>>();
      const resourceAssigned = { id: 123 };
      jest.spyOn(resourceAssignedFormService, 'getResourceAssigned').mockReturnValue({ id: null });
      jest.spyOn(resourceAssignedService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceAssigned: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceAssigned }));
      saveSubject.complete();

      // THEN
      expect(resourceAssignedFormService.getResourceAssigned).toHaveBeenCalled();
      expect(resourceAssignedService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceAssigned>>();
      const resourceAssigned = { id: 123 };
      jest.spyOn(resourceAssignedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceAssigned });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(resourceAssignedService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareResource', () => {
      it('Should forward to resourceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(resourceService, 'compareResource');
        comp.compareResource(entity, entity2);
        expect(resourceService.compareResource).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEmergencyCall', () => {
      it('Should forward to emergencyCallService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(emergencyCallService, 'compareEmergencyCall');
        comp.compareEmergencyCall(entity, entity2);
        expect(emergencyCallService.compareEmergencyCall).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
