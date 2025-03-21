import { IGeoLocation, NewGeoLocation } from './geo-location.model';

export const sampleWithRequiredData: IGeoLocation = {
  id: 5818,
  latitude: 31562,
  longitude: 39562,
};

export const sampleWithPartialData: IGeoLocation = {
  id: 71421,
  latitude: 819,
  longitude: 98882,
};

export const sampleWithFullData: IGeoLocation = {
  id: 69810,
  latitude: 94627,
  longitude: 27218,
};

export const sampleWithNewData: NewGeoLocation = {
  latitude: 92654,
  longitude: 54790,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
