import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { GeoLocationService } from '../service/geo-location.service';

import { GeoLocationComponent } from './geo-location.component';

describe('GeoLocation Management Component', () => {
  let comp: GeoLocationComponent;
  let fixture: ComponentFixture<GeoLocationComponent>;
  let service: GeoLocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'geo-location', component: GeoLocationComponent }]), HttpClientTestingModule],
      declarations: [GeoLocationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(GeoLocationComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GeoLocationComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GeoLocationService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.geoLocations?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to geoLocationService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getGeoLocationIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getGeoLocationIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
