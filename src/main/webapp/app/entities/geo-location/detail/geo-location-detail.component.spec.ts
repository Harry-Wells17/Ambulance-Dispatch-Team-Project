import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GeoLocationDetailComponent } from './geo-location-detail.component';

describe('GeoLocation Management Detail Component', () => {
  let comp: GeoLocationDetailComponent;
  let fixture: ComponentFixture<GeoLocationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GeoLocationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ geoLocation: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GeoLocationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GeoLocationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load geoLocation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.geoLocation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
