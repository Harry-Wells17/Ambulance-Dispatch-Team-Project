import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserPermsDetailComponent } from './user-perms-detail.component';

describe('UserPerms Management Detail Component', () => {
  let comp: UserPermsDetailComponent;
  let fixture: ComponentFixture<UserPermsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserPermsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userPerms: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserPermsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserPermsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userPerms on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userPerms).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
