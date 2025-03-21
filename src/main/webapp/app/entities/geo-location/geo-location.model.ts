export interface IGeoLocation {
  id: number;
  latitude?: number | null;
  longitude?: number | null;
}

export type NewGeoLocation = Omit<IGeoLocation, 'id'> & { id: null };
