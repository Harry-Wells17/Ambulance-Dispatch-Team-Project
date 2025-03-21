import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EmergencyCallService } from '../service/emergency-call.service';

import { EmergencyCallComponent } from './emergency-call.component';

describe('EmergencyCall Management Component', () => {
  let comp: EmergencyCallComponent;
  let fixture: ComponentFixture<EmergencyCallComponent>;
  let service: EmergencyCallService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'emergency-call', component: EmergencyCallComponent }]), HttpClientTestingModule],
      declarations: [EmergencyCallComponent],
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
      .overrideTemplate(EmergencyCallComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmergencyCallComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EmergencyCallService);

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
    expect(comp.emergencyCalls?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to emergencyCallService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getEmergencyCallIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getEmergencyCallIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
