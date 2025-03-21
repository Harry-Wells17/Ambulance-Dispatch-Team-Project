import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EmergencyCallFormService } from './emergency-call-form.service';
import { EmergencyCallService } from '../service/emergency-call.service';
import { IEmergencyCall } from '../emergency-call.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';

import { EmergencyCallUpdateComponent } from './emergency-call-update.component';

describe('EmergencyCall Management Update Component', () => {
  let comp: EmergencyCallUpdateComponent;
  let fixture: ComponentFixture<EmergencyCallUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let emergencyCallFormService: EmergencyCallFormService;
  let emergencyCallService: EmergencyCallService;
  let userService: UserService;
  let eventService: EventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EmergencyCallUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EmergencyCallUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmergencyCallUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    emergencyCallFormService = TestBed.inject(EmergencyCallFormService);
    emergencyCallService = TestBed.inject(EmergencyCallService);
    userService = TestBed.inject(UserService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const emergencyCall: IEmergencyCall = { id: 456 };
      const createdBy: IUser = { id: 58025 };
      emergencyCall.createdBy = createdBy;

      const userCollection: IUser[] = [{ id: 495 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ emergencyCall });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Event query and add missing value', () => {
      const emergencyCall: IEmergencyCall = { id: 456 };
      const event: IEvent = { id: 3511 };
      emergencyCall.event = event;

      const eventCollection: IEvent[] = [{ id: 28554 }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [event];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ emergencyCall });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining)
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const emergencyCall: IEmergencyCall = { id: 456 };
      const createdBy: IUser = { id: 40089 };
      emergencyCall.createdBy = createdBy;
      const event: IEvent = { id: 38138 };
      emergencyCall.event = event;

      activatedRoute.data = of({ emergencyCall });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.eventsSharedCollection).toContain(event);
      expect(comp.emergencyCall).toEqual(emergencyCall);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmergencyCall>>();
      const emergencyCall = { id: 123 };
      jest.spyOn(emergencyCallFormService, 'getEmergencyCall').mockReturnValue(emergencyCall);
      jest.spyOn(emergencyCallService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ emergencyCall });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: emergencyCall }));
      saveSubject.complete();

      // THEN
      expect(emergencyCallFormService.getEmergencyCall).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(emergencyCallService.update).toHaveBeenCalledWith(expect.objectContaining(emergencyCall));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmergencyCall>>();
      const emergencyCall = { id: 123 };
      jest.spyOn(emergencyCallFormService, 'getEmergencyCall').mockReturnValue({ id: null });
      jest.spyOn(emergencyCallService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ emergencyCall: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: emergencyCall }));
      saveSubject.complete();

      // THEN
      expect(emergencyCallFormService.getEmergencyCall).toHaveBeenCalled();
      expect(emergencyCallService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmergencyCall>>();
      const emergencyCall = { id: 123 };
      jest.spyOn(emergencyCallService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ emergencyCall });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(emergencyCallService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEvent', () => {
      it('Should forward to eventService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
