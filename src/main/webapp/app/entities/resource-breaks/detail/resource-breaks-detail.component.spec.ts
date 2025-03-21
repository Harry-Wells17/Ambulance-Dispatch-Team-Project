import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ResourceBreaksDetailComponent } from './resource-breaks-detail.component';

describe('ResourceBreaks Management Detail Component', () => {
  let comp: ResourceBreaksDetailComponent;
  let fixture: ComponentFixture<ResourceBreaksDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceBreaksDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ resourceBreaks: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ResourceBreaksDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ResourceBreaksDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load resourceBreaks on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.resourceBreaks).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
