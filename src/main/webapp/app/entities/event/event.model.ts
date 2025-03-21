export interface IEvent {
  id: number;
  created?: string | null;
  eventName?: string | null;
  eventDescription?: string | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
