import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GeoLocationFormService } from './geo-location-form.service';
import { GeoLocationService } from '../service/geo-location.service';
import { IGeoLocation } from '../geo-location.model';

import { GeoLocationUpdateComponent } from './geo-location-update.component';

describe('GeoLocation Management Update Component', () => {
  let comp: GeoLocationUpdateComponent;
  let fixture: ComponentFixture<GeoLocationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let geoLocationFormService: GeoLocationFormService;
  let geoLocationService: GeoLocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GeoLocationUpdateComponent],
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
      .overrideTemplate(GeoLocationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GeoLocationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    geoLocationFormService = TestBed.inject(GeoLocationFormService);
    geoLocationService = TestBed.inject(GeoLocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const geoLocation: IGeoLocation = { id: 456 };

      activatedRoute.data = of({ geoLocation });
      comp.ngOnInit();

      expect(comp.geoLocation).toEqual(geoLocation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGeoLocation>>();
      const geoLocation = { id: 123 };
      jest.spyOn(geoLocationFormService, 'getGeoLocation').mockReturnValue(geoLocation);
      jest.spyOn(geoLocationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ geoLocation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: geoLocation }));
      saveSubject.complete();

      // THEN
      expect(geoLocationFormService.getGeoLocation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(geoLocationService.update).toHaveBeenCalledWith(expect.objectContaining(geoLocation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGeoLocation>>();
      const geoLocation = { id: 123 };
      jest.spyOn(geoLocationFormService, 'getGeoLocation').mockReturnValue({ id: null });
      jest.spyOn(geoLocationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ geoLocation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: geoLocation }));
      saveSubject.complete();

      // THEN
      expect(geoLocationFormService.getGeoLocation).toHaveBeenCalled();
      expect(geoLocationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGeoLocation>>();
      const geoLocation = { id: 123 };
      jest.spyOn(geoLocationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ geoLocation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(geoLocationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
