import {Alert, Pressable, Text, View} from 'react-native';
import FindShift, {IEvent} from './shift/findShift';
import {Ref, useEffect, useRef, useState} from 'react';
import SignOn, {IResource} from './shift/signOn';
import {baseDomain} from '../App';
import dayjs from 'dayjs/esm';
import ResourceHomePage from './resource/mainPage';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {CreateShift, GET, POST, PUT} from './server/helper';
import Sound from 'react-native-sound';
import {IEmergencyCall} from './resource/pages/assignedCall';
import AppLoginPage from './shift/login';

export interface IResourceBreaks {
  lastBreak?: dayjs.Dayjs | null;
  breakRequested?: boolean | null;
}

export default function AppHomePage() {
  const [loggedIn, setLoggedIn] = useState<boolean>(false);
  const [username, setUsername] = useState<string>('');

  const [event, setEvent] = useState<IEvent | null>(null);
  const [myResource, setMyResource] = useState<
    (IResource & {id: number}) | null
  >(null);

  const resourceId = useRef<number | null>(null);
  const eventId = useRef<number | null>(null);

  const [calls, setCalls] = useState<IEmergencyCall[]>([]);

  useEffect(() => {
    const getResource = async () => {
      // Potneitally resurce id and event id
      const resource = await AsyncStorage.getItem('resource');
      const event = await AsyncStorage.getItem('event');

      if (resource !== null && event !== null) {
        const url = `${baseDomain}events/${event}`;
        const url2 = `${baseDomain}resources/${resource}`;
        resourceId.current = parseInt(resource, 10);
        eventId.current = parseInt(event, 10);

        let data = await GET(url);
        let data2 = await GET(url2);

        setEvent((await data.json()) as any);
        setMyResource((await data2.json()) as any);
      }
    };

    getResource();
  }, []);

  useEffect(() => {
    console.log('Starting interval');
    let int = setInterval(async () => {
      if (resourceId.current === null || eventId.current === null) return;
      const url = `${baseDomain}resources/${resourceId.current}`;

      if (resourceId.current === null) return;

      // check for calls
      let callsIN = await GET(
        `${baseDomain}resource-assigneds?resourceId.equals=${resourceId.current}`,
      );
      let callsINjson = (await callsIN.json()) as any as IEmergencyCall[];
      setCalls(x => {
        if (callsIN.status === 200) {
          // Are there new calls?
          (async () => {
            // There may be less but we just care if callsInjson contains a new id
            let newCalls: IEmergencyCall[] = callsINjson.filter(
              (call: IEmergencyCall) => {
                return !x.some((c: IEmergencyCall) => c.id === call.id);
              },
            );

            if (newCalls.length > 0) {
              let beep = new Sound('call.wav', Sound.MAIN_BUNDLE, error => {
                beep.play();
              });
            }

            newCalls.forEach(async (thecall: IEmergencyCall) => {
              Alert.alert(
                'New Call',
                `You have a new call:\n ${
                  (thecall as any).emergencyCall.type
                } \n ${(thecall as any).emergencyCall.injuries} \n ${
                  (thecall as any).emergencyCall.condition
                }`,
              );
            });
          })();
          return callsINjson as any;
        } else {
          return x;
        }
      });

      let res = await GET(url);

      if (res.status === 200) {
        let data = await res.json();

        setMyResource(data as any);
      } else {
      }
    }, 3000);

    return () => {
      clearInterval(int);
    };
  }, [resourceId, eventId]);

  let endShift = async (returnFunc?: () => void) => {
    Alert.alert('Are you sure?', "You can't undo this action", [
      {
        text: 'Ok',
        onPress: async () => {
          console.log('Ending shift');

          await AsyncStorage.removeItem('resource');
          await AsyncStorage.removeItem('event');

          const url = `${baseDomain}resources/${resourceId.current}`;

          await PUT(url, {...myResource, status: 'SHIFT_END'});

          setMyResource(null);
          setEvent(null);

          resourceId.current = null;
          eventId.current = null;

          if (returnFunc !== undefined) {
            returnFunc();
          }
        },
      },
      {
        text: 'Cancel',
        onPress: () => {},
      },
    ]);
  };

  return (
    <View className="font-bold  flex-1 flex-col items-center justify-center">
      <View className="flex-1 flex-row justify-center mt-8 h-[200px]">
        <Text className="text-6xl font-extrabold text-blue-400">Ambu</Text>
        <Text className="text-white text-6xl font-bold">Dispatch</Text>
      </View>

      {loggedIn === false ? (
        <View className="flex-1 mt-[-50]  w-full">
          <AppLoginPage
            loggedIn={e => {
              setUsername(e);
              setLoggedIn(true);
            }}
          />
        </View>
      ) : (
        <>
          <View className="flex-1 mt-[-50]  w-full">
            {event === null ? (
              <FindShift gotShift={e => setEvent(e)} />
            ) : (
              <>
                {myResource === null ? (
                  <SignOn
                    event={event}
                    goback={() => setEvent(null)}
                    submit={x =>
                      CreateShift(x, setMyResource, event, resourceId, eventId)
                    }
                  />
                ) : (
                  <ResourceHomePage
                    endShift={endShift}
                    resourceId={resourceId}
                    event={event}
                    myResource={myResource}
                  />
                )}
              </>
            )}
          </View>
          {myResource !== null && (
            <View className=" mb-[20] w-full flex-1 items-center justify-center">
              <Pressable
                aria-label="Request break"
                className="bg-green-500  w-[80%] items-center py-4 rounded-lg ml-2 h-[40]"
                onPress={async () => {
                  Alert.alert('Request Break', 'Are you Sure?', [
                    {
                      text: 'Yes',
                      onPress: async () => {
                        let data = await GET(
                          `${baseDomain}resources/${resourceId.current}`,
                        );
                        let datajson = (await data.json()) as any;

                        datajson.resourceBreak.breakRequested = true;

                        console.log(datajson.resourceBreak);

                        let out = await PUT(
                          `${baseDomain}resource-breaks/${datajson.resourceBreak.id}`,
                          datajson.resourceBreak,
                        );
                      },
                    },
                    {
                      text: 'Cancel',
                      onPress: () => {},
                    },
                  ]);
                }}>
                <Text className="text-white font-bold text-xl mt-[-5]">
                  Request Break
                </Text>
              </Pressable>
            </View>
          )}
        </>
      )}

      {username !== '' && (
        <Pressable
          onPress={() => {
            endShift(() => {
              setLoggedIn(false);
              setUsername('');
            });
          }}
          aria-label="Request break">
          <View className="h-[20]">
            <Text className="text-white text-sm font-bold text-center">
              Logged in as {username}, click to log out!
            </Text>
          </View>
        </Pressable>
      )}

      <View className="h-[20%] flex-1 items-end justify-end">
        <Text className="text-white text-sm font-bold text-center">
          You are accessing an experimental mobile application developed by
          participants of the Team Project 2024 module.
        </Text>
      </View>
    </View>
  );
}
