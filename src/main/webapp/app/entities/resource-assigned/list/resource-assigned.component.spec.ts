import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ResourceAssignedService } from '../service/resource-assigned.service';

import { ResourceAssignedComponent } from './resource-assigned.component';

describe('ResourceAssigned Management Component', () => {
  let comp: ResourceAssignedComponent;
  let fixture: ComponentFixture<ResourceAssignedComponent>;
  let service: ResourceAssignedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'resource-assigned', component: ResourceAssignedComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ResourceAssignedComponent],
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
      .overrideTemplate(ResourceAssignedComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceAssignedComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ResourceAssignedService);

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
    expect(comp.resourceAssigneds?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to resourceAssignedService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getResourceAssignedIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getResourceAssignedIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
