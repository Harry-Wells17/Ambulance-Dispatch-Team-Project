import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  Text,
  TextInput,
  View,
} from 'react-native';
import dayjs from 'dayjs/esm';
import {v4} from 'uuid';
import {IResourceBreaks} from '../../../../webapp/app/entities/resource-breaks/resource-breaks.model';
import {IGeoLocation} from '../../../../webapp/app/entities/geo-location/geo-location.model';
import {IEvent} from '../../../../webapp/app/entities/event/event.model';
import {ResourceType} from '../../../../webapp/app/entities/enumerations/resource-type.model';
import {Status} from '../../../../webapp/app/entities/enumerations/status.model';
import Picker from '../components/Picker';
import {useState} from 'react';
import TimeSince from '../components/TimeSince';

export interface IResource {
  id: number;
  created?: dayjs.Dayjs | null;
  type?: ResourceType | null;
  status?: Status | null;
  resourceBreak?: Pick<IResourceBreaks, 'id'> | null;
  geoId?: Pick<IGeoLocation, 'id'> | null;
  event?: Pick<IEvent, 'id'> | null;
  callSign?: string | null;
}

export default function SignOn({
  event,
  goback,
  submit,
}: {
  event: IEvent;
  goback: () => void;
  submit: (resource: IResource) => void;
}) {
  const [resourceType, setResourceType] = useState<ResourceType | null>(null);
  const [callsign, setCallsign] = useState<string>('' as string);

  return (
    <View className="flex-1 flex-col items-center justify-center h-full py-[10%] px-2   mt-[-50] mb-[400]">
      <View className="items-center justify-center w-full flex-1  border-neutral-600 border border-solid rounded-lg">
        <Text className="text-center text-white text-3xl font-bold mb-4 mt-4">
          Enter shift information:
        </Text>
        <View className="w-[80%]">
          <View className="flex-1 flex-row">
            <Text className="text-left mr-1 text-neutral-300 text-xl font-normal mb-2">
              Event Name:
            </Text>
            <Text className="text-left w-full text-neutral-300 text-xl font-extrabold mb-2">
              {event?.eventName}
            </Text>
          </View>
          <View className="flex-1 flex-row">
            <Text className="text-left mr-1 text-neutral-300 text-xl font-normal mb-2">
              Event Id:
            </Text>
            <Text className="text-left w-full text-neutral-300 text-xl font-extrabold mb-2">
              {event?.id}
            </Text>
          </View>

          <View className="flex-1 flex-row">
            <Text className="text-left mr-1 text-neutral-300 text-xl font-normal mb-2">
              Started:
            </Text>
            <Text className="text-left w-full text-neutral-300 text-xl font-extrabold mb-2">
              {TimeSince({
                time: new Date(
                  Number.isNaN(parseInt(event?.created as string))
                    ? 0
                    : parseInt(event?.created as string),
                ),
              })}{' '}
              ago
            </Text>
          </View>
        </View>
        <View className="flex-1 flex-row justify-start w-[80%]">
          <Pressable aria-label="change event" onPress={goback}>
            <Text className="text-left mr-1 text-blue-400 text-xl font-bold underline">
              Change Event
            </Text>
          </Pressable>
        </View>
        <View className="w-[80%] mt-4">
          <Text className="text-left text-white text-xl font-bold">
            Choose Resource Type:
          </Text>
          <Picker
            onChange={value => setResourceType(value as ResourceType)}
            placeholder="Select a resource type..."
            labels={[
              {label: 'Ambulance', value: 'AMBULANCE', color: 'violet'},
              {
                label: 'Cycle Responder',
                value: 'CYCLERESPONSE',
                color: 'orange',
              },
              {label: 'Foot Team', value: 'FOOTTEAM', color: 'green'},
              {label: 'Logistics', value: 'LOGISTICS', color: 'lightgreen'},
              {label: 'Management', value: 'MANAGEMENT', color: 'red'},
              {
                label: 'Treatment Center',
                value: 'TREATMENTCENTRE',
                color: 'pink',
              },
            ]}
          />
        </View>

        <View className="w-[80%] mt-4">
          <Text className="text-left text-white text-xl font-bold">
            Enter Call Sign:
          </Text>

          <TextInput
            className=" w-full pb-1 h-[35] bg-neutral-700  text-white text-md pt-1 text-left pl-3 rounded-[10] border-neutral-600 border border-solid"
            placeholder="Enter Call Sign..."
            returnKeyType="done"
            onChange={e => setCallsign(e.nativeEvent.text)}
          />
        </View>

        <View className="w-[80%] mt-4 mb-4">
          <Pressable
            aria-label="submit form"
            className="bg-green-500  w-[100%] items-center py-4 rounded-lg mt-4"
            onPress={() => {
              submit({
                // System overwrites this
                type: resourceType,
                event: {id: event.id},
                callSign: callsign,
                created: dayjs(),
                latitude: 0,
                longitude: 0,
                resourceType: 1,
                status: 'GREEN',
              } as any);
            }}>
            <Text className="text-white font-bold text-xl mt-[-5]">Submit</Text>
          </Pressable>
        </View>
      </View>
    </View>
  );
}
