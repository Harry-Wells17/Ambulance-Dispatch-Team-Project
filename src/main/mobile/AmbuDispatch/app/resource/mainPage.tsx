import {Alert, Linking, Pressable, Text, View} from 'react-native';
import {IEvent} from '../shift/findShift';
import {IResource} from '../shift/signOn';
import AssignedCall, {
  IEmergencyCall,
  IResourceAssigned,
} from './pages/assignedCall';
import Location from './pages/location';
import Geolocation, {GeoPosition} from 'react-native-geolocation-service';
import {Ref, useEffect, useRef, useState} from 'react';
import {baseDomain} from '../../App';
import {GET, bearedToken} from '../server/helper';

export default function ResourceHomePage({
  event,
  myResource,
  endShift,
  resourceId,
}: {
  event: IEvent;
  myResource: IResource;
  endShift: () => void;
  resourceId: React.MutableRefObject<number | null>;
}) {
  const watchId = useRef<number | null>(null);
  const location = useRef<GeoPosition | null>(null);
  const [mainLocation, setMainLocation] = useState<GeoPosition | null>(null);

  const hasPermissionIOS = async () => {
    const openSetting = () => {
      Linking.openSettings().catch(() => {
        Alert.alert('Unable to open settings');
      });
    };
    const status = await Geolocation.requestAuthorization('whenInUse');

    if (status === 'granted') {
      return true;
    }

    if (status === 'denied') {
      Alert.alert('Location permission denied');
    }

    if (status === 'disabled') {
      Alert.alert(
        `Turn on Location Services to allow "AmbuDispatch" to determine your location.`,
        '',
        [
          {text: 'Go to Settings', onPress: openSetting},
          {text: "Don't Use Location", onPress: () => {}},
        ],
      );
    }

    return false;
  };

  const getLocationUpdates = async () => {
    const hasPermission = await hasPermissionIOS();

    if (!hasPermission) {
      return;
    }

    watchId.current = Geolocation.watchPosition(
      position => {
        setMainLocation(position);
        location.current = position;
      },
      error => {
        console.log(error);
      },
      {
        accuracy: {
          android: 'high',
          ios: 'bestForNavigation',
        },
        enableHighAccuracy: true,
        distanceFilter: 0,
        interval: 1000,
        fastestInterval: 2000,
        forceRequestLocation: true,
        forceLocationManager: false,
        showLocationDialog: true,
        useSignificantChanges: false,
      },
    );
  };

  useEffect(() => {
    getLocationUpdates();

    let int = setInterval(async () => {
      const url2 = `${baseDomain}resources/${resourceId.current}`;

      let data2 = await GET(url2);

      fetch(baseDomain + `resources/${resourceId.current}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: bearedToken,
        },

        body: JSON.stringify({
          ...(await data2.json()),
          latitude: location.current?.coords.latitude,
          longitude: location.current?.coords.longitude,
        }),
      }).catch(e => {
        console.log(e);
      });
    }, 5000);
  }, [resourceId]);

  const [callsAssigned, setCallsAssigned] = useState<
    {call: IEmergencyCall; resourceAssigned: IResourceAssigned}[]
  >([]);

  let update = async () => {
    let result = await GET(
      `${baseDomain}resource-assigneds?resourceId.equals=${resourceId.current}`,
    );
    let json = (await result.json()) as IResourceAssigned[];

    setCallsAssigned(
      json.map(e => {
        return {
          call: e.emergencyCall,
          resourceAssigned: e,
        } as any;
      }),
    );
  };

  useEffect(() => {
    let int = setInterval(async () => {
      update();
    }, 1000);

    return () => {
      clearInterval(int);
    };
  }, [resourceId]);

  return (
    <View className="flex-1">
      <View className="flex-1">
        <Text className="text-white text-4xl w-full text-center mt-[-80] font-bold">
          {event.eventName}
        </Text>
        <View className="mt-4 min-h-[100px] flex-1 flex-row items-center justify-center">
          <View className="w-[50%] flex-1 flex-col">
            <View className="w-full flex-1 flex-row items-start justify-start pl-4">
              <Text className="text-white text-2xl  text-left ">
                Call Sign:
              </Text>
              <Text className="text-white text-2xl  text-left pl-2  font-bold">
                {myResource.callSign}
              </Text>
            </View>
            <View className="w-full flex-1 flex-row items-start justify-start pl-4">
              <Text className="text-white text-2xl  text-left ">Resource:</Text>
              <Text className="text-white text-2xl  text-left pl-2  font-bold">
                {myResource.type}
              </Text>
            </View>
            <View className="w-full flex-1 flex-row items-start justify-start pl-4">
              <Text className="text-white text-2xl  text-left ">Status:</Text>
              <Text
                className={`${
                  myResource.status === 'GREEN'
                    ? 'text-green-400'
                    : 'text-red-400'
                }  text-2xl  text-left pl-2  font-bold`}>
                {myResource.status}
              </Text>
            </View>
          </View>

          <View className="w-[50%] flex-1 items-end justify-center pr-4">
            <Pressable
              aria-label="end shift"
              className="bg-red-500  w-[50%] items-center py-4 rounded-lg "
              onPress={() => {
                endShift();
              }}>
              <Text className="text-white font-bold text-xl mt-[-5]">
                End Shift
              </Text>
            </Pressable>
          </View>
        </View>
      </View>

      <View className="mt-10 ">
        <AssignedCall
          calls={callsAssigned.filter(
            e => e.resourceAssigned.greenTime === null,
          )}
          update={update}
          live={true}
        />

        <Location
          location={location.current}
          calls={callsAssigned.map(e => e.call)}
        />
        <AssignedCall
          calls={callsAssigned.filter(
            e => e.resourceAssigned.greenTime !== null,
          )}
          live={false}
          update={update}
        />
      </View>
    </View>
  );
}
