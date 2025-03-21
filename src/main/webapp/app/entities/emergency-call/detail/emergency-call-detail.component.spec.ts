import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EmergencyCallDetailComponent } from './emergency-call-detail.component';

describe('EmergencyCall Management Detail Component', () => {
  let comp: EmergencyCallDetailComponent;
  let fixture: ComponentFixture<EmergencyCallDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmergencyCallDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ emergencyCall: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EmergencyCallDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EmergencyCallDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load emergencyCall on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.emergencyCall).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
