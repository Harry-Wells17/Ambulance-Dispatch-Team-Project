import {Alert, Button, Pressable, Text, View} from 'react-native';
import {IEmergencyCall, IResourceAssigned} from './assignedCall';
import {useEffect, useState} from 'react';
import {GET, PUT} from '../../server/helper';
import {baseDomain} from '../../../App';
import dayjs from 'dayjs/esm';
import {IResource} from '../../shift/signOn';

const TimeSince = ({time}: {time: Date}) => {
  const diff = Date.now() - time.getTime();
  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);

  if (hours > 0) {
    return `${hours}h`;
  } else if (minutes > 0) {
    return `${minutes}m`;
  } else {
    return `${seconds}s`;
  }
};

export default function CallAssigned({
  call,
  color,
  resourceAssigned,
  update,
}: {
  call: IEmergencyCall;
  resourceAssigned: IResourceAssigned;
  color: string;
  update: () => void;
}) {
  let getState = () => {
    if (resourceAssigned.greenTime !== null) {
      return '';
    } else if (resourceAssigned.clearHospitalTime !== null) {
      return 'Status GREEN';
    } else if (resourceAssigned.arrivedHospitalTime !== null) {
      return 'Cleared Hospital';
    } else if (resourceAssigned.leftSceneTime !== null) {
      return 'Arrived Hospital';
    } else if (resourceAssigned.onSceneTime !== null) {
      return 'Left Scene';
    } else {
      return 'Arrived Scene';
    }
  };

  let changeStatus = async () => {
    let url = `${baseDomain}resource-assigneds`;
    let resourceUrl = `${baseDomain}resources`;
    // first lets grab the object
    let result = await GET(url + `/${resourceAssigned.id}`);
    let json = (await result.json()) as IResourceAssigned;

    console.log(resourceUrl + `/${resourceAssigned?.resource?.id}`);
    let resource = await GET(
      resourceUrl + `/${resourceAssigned?.resource?.id}`,
    );
    let resourceJSON = (await resource.json()) as IResource;

    if (json === undefined) {
      Alert.alert('ERROR', 'Failed to change status!');
    }

    //    RED, GREEN, BREAK, CLEAR, ONSCENE, ENROUTE, SHIFT_END

    let newStatus = '';
    let stateCurrent = getState();
    if (stateCurrent === 'Arrived Scene') {
      json.onSceneTime = dayjs();
      newStatus = 'ONSCENE';
    } else if (stateCurrent === 'Left Scene') {
      json.leftSceneTime = dayjs();
      newStatus = 'RED';
    } else if (stateCurrent === 'Arrived Hospital') {
      json.arrivedHospitalTime = dayjs();
      newStatus = 'RED';
    } else if (stateCurrent === 'Cleared Hospital') {
      json.clearHospitalTime = dayjs();
      newStatus = 'RED';
    } else {
      json.greenTime = dayjs();
      newStatus = 'GREEN';
    }

    // now push
    try {
      await PUT(url + `/${resourceAssigned.id}`, json);
      await PUT(resourceUrl + `/${resourceAssigned?.resource?.id}`, {
        ...resourceJSON,
        status: newStatus,
      });
      console.log(json);
      update();
    } catch (e) {
      console.error(e);
    }
  };

  let changeStatusReq = () => {
    Alert.alert('Are you sure?', undefined, [
      {
        text: 'Yes',
        onPress: changeStatus,
      },
      {
        text: 'Cancel',
      },
    ]);
  };

  return (
    <View
      className={`w-full py-4 ${color}  px-2 flex-1 flex-col items-center justify-start`}>
      <View className="w-full flex-1 flex-row items-center justify-center">
        <Text className="text-white text-3xl font-extrabold">
          {(call as any).type} -{' '}
        </Text>
        <Text className="text-white text-xl font-bold flex-grow">
          Call Created - {TimeSince({time: new Date(`${call.created}`)})}
        </Text>
      </View>
      <View className="w-full flex-1 flex-row items-center justify-center">
        <Text className="text-white text-2xl font-bold mr-1 min-w-[20]">
          A/S -
        </Text>
        <Text className="text-white text-xl font-bold flex-grow">
          {call.age} {(call as any).sexAssignedAtBirth}
        </Text>
      </View>
      <View className="w-full flex-1 flex-row items-center justify-center">
        <Text className="text-white text-2xl font-bold mr-1 min-w-[20]">
          H -
        </Text>
        <Text className="text-white text-xl font-bold flex-grow">
          {call.history}
        </Text>
      </View>
      <View className="w-full flex-1 flex-row items-center justify-center">
        <Text className="text-white text-2xl font-bold mr-1 min-w-[20]">
          I -
        </Text>
        <Text className="text-white text-xl font-bold flex-grow">
          {call.injuries}
        </Text>
      </View>
      <View className="w-full flex-1 flex-row items-center justify-center">
        <Text className="text-white text-2xl font-bold mr-1 min-w-[20]">
          C -
        </Text>
        <Text className="text-white text-xl font-bold flex-grow">
          {call.condition}
        </Text>
      </View>

      {getState() !== '' && (
        <View className="w-full flex-1 flex-row items-center justify-center mt-4">
          <Pressable
            aria-label="update status"
            className="w-full flex-1 items-center justify-center"
            onPress={changeStatusReq}>
            <View className="bg-neutral-600 w-[80%] py-2 rounded-lg border-2 border-neutral-500">
              <Text className="text-white text-2xl w-full text-center font-bold flex-grow">
                Mark {getState()}
              </Text>
            </View>
          </Pressable>
        </View>
      )}
    </View>
  );
}
