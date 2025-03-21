import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserExistDetailComponent } from './user-exist-detail.component';

describe('UserExist Management Detail Component', () => {
  let comp: UserExistDetailComponent;
  let fixture: ComponentFixture<UserExistDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserExistDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userExist: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserExistDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserExistDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userExist on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userExist).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
