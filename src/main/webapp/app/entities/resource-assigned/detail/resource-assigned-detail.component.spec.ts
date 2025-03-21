import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ResourceAssignedDetailComponent } from './resource-assigned-detail.component';

describe('ResourceAssigned Management Detail Component', () => {
  let comp: ResourceAssignedDetailComponent;
  let fixture: ComponentFixture<ResourceAssignedDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceAssignedDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ resourceAssigned: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ResourceAssignedDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ResourceAssignedDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load resourceAssigned on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.resourceAssigned).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
