import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SystemLogService } from '../service/system-log.service';

import { SystemLogComponent } from './system-log.component';

describe('SystemLog Management Component', () => {
  let comp: SystemLogComponent;
  let fixture: ComponentFixture<SystemLogComponent>;
  let service: SystemLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'system-log', component: SystemLogComponent }]), HttpClientTestingModule],
      declarations: [SystemLogComponent],
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
      .overrideTemplate(SystemLogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SystemLogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SystemLogService);

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
    expect(comp.systemLogs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to systemLogService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSystemLogIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSystemLogIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
