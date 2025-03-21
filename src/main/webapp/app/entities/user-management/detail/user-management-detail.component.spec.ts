import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserManagementDetailComponent } from './user-management-detail.component';

describe('UserManagement Management Detail Component', () => {
  let comp: UserManagementDetailComponent;
  let fixture: ComponentFixture<UserManagementDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserManagementDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userManagement: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserManagementDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserManagementDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userManagement on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userManagement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
