import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SystemLogFormService } from './system-log-form.service';
import { SystemLogService } from '../service/system-log.service';
import { ISystemLog } from '../system-log.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEmergencyCall } from 'app/entities/emergency-call/emergency-call.model';
import { EmergencyCallService } from 'app/entities/emergency-call/service/emergency-call.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';

import { SystemLogUpdateComponent } from './system-log-update.component';

describe('SystemLog Management Update Component', () => {
  let comp: SystemLogUpdateComponent;
  let fixture: ComponentFixture<SystemLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let systemLogFormService: SystemLogFormService;
  let systemLogService: SystemLogService;
  let userService: UserService;
  let emergencyCallService: EmergencyCallService;
  let eventService: EventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SystemLogUpdateComponent],
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
      .overrideTemplate(SystemLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SystemLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    systemLogFormService = TestBed.inject(SystemLogFormService);
    systemLogService = TestBed.inject(SystemLogService);
    userService = TestBed.inject(UserService);
    emergencyCallService = TestBed.inject(EmergencyCallService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const systemLog: ISystemLog = { id: 456 };
      const createdBy: IUser = { id: 97542 };
      systemLog.createdBy = createdBy;

      const userCollection: IUser[] = [{ id: 21842 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ systemLog });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call EmergencyCall query and add missing value', () => {
      const systemLog: ISystemLog = { id: 456 };
      const emergencyCall: IEmergencyCall = { id: 16527 };
      systemLog.emergencyCall = emergencyCall;

      const emergencyCallCollection: IEmergencyCall[] = [{ id: 40309 }];
      jest.spyOn(emergencyCallService, 'query').mockReturnValue(of(new HttpResponse({ body: emergencyCallCollection })));
      const additionalEmergencyCalls = [emergencyCall];
      const expectedCollection: IEmergencyCall[] = [...additionalEmergencyCalls, ...emergencyCallCollection];
      jest.spyOn(emergencyCallService, 'addEmergencyCallToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ systemLog });
      comp.ngOnInit();

      expect(emergencyCallService.query).toHaveBeenCalled();
      expect(emergencyCallService.addEmergencyCallToCollectionIfMissing).toHaveBeenCalledWith(
        emergencyCallCollection,
        ...additionalEmergencyCalls.map(expect.objectContaining)
      );
      expect(comp.emergencyCallsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Event query and add missing value', () => {
      const systemLog: ISystemLog = { id: 456 };
      const event: IEvent = { id: 74361 };
      systemLog.event = event;

      const eventCollection: IEvent[] = [{ id: 69285 }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [event];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ systemLog });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining)
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const systemLog: ISystemLog = { id: 456 };
      const createdBy: IUser = { id: 85074 };
      systemLog.createdBy = createdBy;
      const emergencyCall: IEmergencyCall = { id: 26807 };
      systemLog.emergencyCall = emergencyCall;
      const event: IEvent = { id: 12366 };
      systemLog.event = event;

      activatedRoute.data = of({ systemLog });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.emergencyCallsSharedCollection).toContain(emergencyCall);
      expect(comp.eventsSharedCollection).toContain(event);
      expect(comp.systemLog).toEqual(systemLog);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISystemLog>>();
      const systemLog = { id: 123 };
      jest.spyOn(systemLogFormService, 'getSystemLog').mockReturnValue(systemLog);
      jest.spyOn(systemLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ systemLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: systemLog }));
      saveSubject.complete();

      // THEN
      expect(systemLogFormService.getSystemLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(systemLogService.update).toHaveBeenCalledWith(expect.objectContaining(systemLog));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISystemLog>>();
      const systemLog = { id: 123 };
      jest.spyOn(systemLogFormService, 'getSystemLog').mockReturnValue({ id: null });
      jest.spyOn(systemLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ systemLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: systemLog }));
      saveSubject.complete();

      // THEN
      expect(systemLogFormService.getSystemLog).toHaveBeenCalled();
      expect(systemLogService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISystemLog>>();
      const systemLog = { id: 123 };
      jest.spyOn(systemLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ systemLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(systemLogService.update).toHaveBeenCalled();
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

    describe('compareEmergencyCall', () => {
      it('Should forward to emergencyCallService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(emergencyCallService, 'compareEmergencyCall');
        comp.compareEmergencyCall(entity, entity2);
        expect(emergencyCallService.compareEmergencyCall).toHaveBeenCalledWith(entity, entity2);
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
