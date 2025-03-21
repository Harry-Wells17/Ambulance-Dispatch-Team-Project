import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserRoleDetailComponent } from './user-role-detail.component';

describe('UserRole Management Detail Component', () => {
  let comp: UserRoleDetailComponent;
  let fixture: ComponentFixture<UserRoleDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserRoleDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userRole: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserRoleDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserRoleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userRole on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userRole).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
