import dayjs from 'dayjs/esm';

export interface IResourceBreaks {
  id: number;
  lastBreak?: dayjs.Dayjs | null;
  breakRequested?: boolean | null;
  startedBreak?: dayjs.Dayjs | null;
  onBreak?: boolean | null;
}

export type NewResourceBreaks = Omit<IResourceBreaks, 'id'> & { id: null };
