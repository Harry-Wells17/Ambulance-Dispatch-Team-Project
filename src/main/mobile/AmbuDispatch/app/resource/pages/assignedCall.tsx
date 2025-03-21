import {Text, View, ScrollView} from 'react-native';
import CallAssigned from './call';
import dayjs from 'dayjs/esm';
import {useEffect, useState} from 'react';
import {GET} from '../../server/helper';
import {baseDomain} from '../../../App';
import {IResource} from '../../shift/signOn';

export interface IEmergencyCall {
  id: number;
  created?: dayjs.Dayjs | null;
  open?: boolean | null;
  age?: number | null;
  history?: string | null;
  injuries?: string | null;
  condition?: string | null;
  latitude?: number | null;
  longitude?: number | null;
}

export interface IResourceAssigned {
  id: number;
  callRecievedTime?: dayjs.Dayjs | null;
  onSceneTime?: dayjs.Dayjs | null;
  leftSceneTime?: dayjs.Dayjs | null;
  arrivedHospitalTime?: dayjs.Dayjs | null;
  clearHospitalTime?: dayjs.Dayjs | null;
  greenTime?: dayjs.Dayjs | null;
  unAssignedTime?: dayjs.Dayjs | null;
  resource?: Pick<IResource, 'id'> | null;
  emergencyCall?: Pick<IEmergencyCall, 'id'> | null;
}

export default function AssignedCall({
  calls,
  update,
  live,
}: {
  calls: {call: IEmergencyCall; resourceAssigned: IResourceAssigned}[];
  update: () => void;
  live: boolean;
}) {
  return (
    <View className="flex-1 flex-col items-center justify-center h-full px-2  min-h-[400] mb-10">
      <View className="items-center justify-center w-full flex-1  border-neutral-600 border border-solid rounded-lg">
        <Text className="text-white text-3xl font-bold mt-2">
          {!live ? 'Assigned Closed' : 'Assigned Live'} Calls
        </Text>
        <ScrollView className="w-full max-h-[340] mt-2">
          {calls.map((call, index) => (
            <CallAssigned
              key={index}
              call={call.call}
              color="bg-red-500"
              update={update}
              resourceAssigned={call.resourceAssigned}
            />
          ))}
          {calls.length === 0 && (
            <View className="w-full flex items-center justify-center">
              <Text className="text-white text-3xl mt-4">
                No calls assigned!
              </Text>
            </View>
          )}
        </ScrollView>
      </View>
    </View>
  );
}
