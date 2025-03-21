import dayjs from 'dayjs/esm';
import {baseDomain} from '../../App';
import {IResourceBreaks} from '../homepage';
import {IResource} from '../shift/signOn';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {Alert} from 'react-native';

let bearedToken = ' ';

export {bearedToken};

export function updateToken(token: string) {
  bearedToken = `Bearer ${token}`;
}

export async function GET(url: string) {
  return await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: bearedToken,
    },
  });
}

export async function POST(url: string, body: any) {
  return await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: bearedToken,
    },
    body: JSON.stringify(body),
  });
}

export async function PUT(url: string, body: any) {
  return await fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: bearedToken,
    },
    body: JSON.stringify(body),
  });
}

export async function CreateShift(
  info: IResource,
  setMyResource: (r: IResource) => void,
  event: any,
  resourceId: React.MutableRefObject<number | null>,
  eventId: React.MutableRefObject<number | null>,
) {
  const url = `${baseDomain}resources`;
  const createBreaks = `${baseDomain}resource-breaks`;

  let newBreaks: IResourceBreaks = {
    lastBreak: dayjs(),
    breakRequested: false,
  };
  try {
    let data = await (await POST(createBreaks, newBreaks)).json();

    let res = await POST(url, {
      ...info,
      resourceBreak: {id: (data as any).id},
    });

    if (res.status === 201) {
      let data = await res.json();

      setMyResource(data as any);
      await AsyncStorage.setItem('resource', `${data.id}`);
      await AsyncStorage.setItem('event', `${event?.id}` as any);
      resourceId.current = data.id;
      eventId.current = event?.id;
    } else {
      Alert.alert('Error', 'Failed to create resource', [
        {text: 'OK', onPress: () => console.log('OK Pressed')},
      ]);
    }
  } catch (e) {
    console.log(e);
    Alert.alert('Error', 'Failed to create resource breaks', [
      {text: 'OK', onPress: () => console.log('OK Pressed')},
    ]);
  }
}
