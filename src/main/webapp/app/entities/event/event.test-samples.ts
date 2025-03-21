import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 63022,
  created: 'Cotton',
  eventName: 'Steel red',
  eventDescription: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: IEvent = {
  id: 72471,
  created: 'transform one-to-one',
  eventName: 'Dollar Outdoors Personal',
  eventDescription: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IEvent = {
  id: 66629,
  created: 'coherent',
  eventName: 'virtual',
  eventDescription: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewEvent = {
  created: 'transmit generation fuchsia',
  eventName: 'Dalasi',
  eventDescription: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
