import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ResourceBreaksService } from '../service/resource-breaks.service';

import { ResourceBreaksComponent } from './resource-breaks.component';

describe('ResourceBreaks Management Component', () => {
  let comp: ResourceBreaksComponent;
  let fixture: ComponentFixture<ResourceBreaksComponent>;
  let service: ResourceBreaksService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'resource-breaks', component: ResourceBreaksComponent }]), HttpClientTestingModule],
      declarations: [ResourceBreaksComponent],
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
      .overrideTemplate(ResourceBreaksComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceBreaksComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ResourceBreaksService);

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
    expect(comp.resourceBreaks?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to resourceBreaksService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getResourceBreaksIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getResourceBreaksIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
