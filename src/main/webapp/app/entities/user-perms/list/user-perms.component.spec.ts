import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserPermsService } from '../service/user-perms.service';

import { UserPermsComponent } from './user-perms.component';

describe('UserPerms Management Component', () => {
  let comp: UserPermsComponent;
  let fixture: ComponentFixture<UserPermsComponent>;
  let service: UserPermsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'user-perms', component: UserPermsComponent }]), HttpClientTestingModule],
      declarations: [UserPermsComponent],
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
      .overrideTemplate(UserPermsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserPermsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserPermsService);

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
    expect(comp.userPerms?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to userPermsService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUserPermsIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUserPermsIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
