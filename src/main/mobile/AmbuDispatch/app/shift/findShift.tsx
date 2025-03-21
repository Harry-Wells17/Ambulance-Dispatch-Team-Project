import {useState} from 'react';
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  Text,
  TextInput,
  View,
} from 'react-native';
import {baseDomain} from '../../App';
import {bearedToken} from '../server/helper';

export interface IEvent {
  id: number;
  created?: string | null;
  eventName?: string | null;
  eventDescription?: string | null;
  eventId?: string | null;
}

export default function FindShift({gotShift}: {gotShift: (e: IEvent) => void}) {
  const [debounce, setDebounce] = useState<boolean>(false);
  const [shiftId, setShiftId] = useState<string>('' as string);

  const checkShift = () => {
    setDebounce(true);

    // Send off to check if server id is valid
    const url = baseDomain + `events/${shiftId}`;
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: bearedToken,
      },
    })
      .then(async responseIN => {
        let response = await responseIN.json();
        if (response.status === 404) {
          Alert.alert('Shift Not Found', 'Please enter a valid shift id!', [
            {text: 'OK', onPress: () => console.log('OK Pressed')},
          ]);
          setDebounce(false);
          return;
        }

        gotShift(response);
        return;
      })
      .catch(e => {
        Alert.alert('Shift Not Found', 'Please enter a valid shift id!', [
          {text: 'OK', onPress: () => console.log('OK Pressed')},
        ]);
        setDebounce(false);
      });
  };

  return (
    <View className="flex-1 flex-col items-center justify-center h-full py-[10%] mt-[-475]">
      <View className="items-center justify-center ">
        <Text className="text-center text-white text-3xl font-bold">
          Welcome, please enter the shift ID:
        </Text>
      </View>

      <View className="h-[80] w-full items-center justify-center ">
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          className="w-full flex-1 items-start justify-center">
          <View className="h-[40] w-full flex-row items-center justify-center flex-1 mt-2">
            <TextInput
              className="h-[40] pb-1 bg-neutral-700 w-[60%] text-white font-bold text-2xl text-center rounded-lg border-neutral-600 border border-solid"
              style={{width: !debounce ? '60%' : '80%'}}
              keyboardType="number-pad"
              placeholder="Enter Shift Id..."
              returnKeyType="done"
              onChange={e => setShiftId(e.nativeEvent.text)}
              editable={!debounce}
            />
            {!debounce && (
              <Pressable
                aria-label="submit call sign"
                className="bg-green-500  w-[20%] items-center py-4 rounded-lg ml-2 h-[40]"
                onPress={() => checkShift()}>
                <Text className="text-white font-bold text-xl mt-[-5]">
                  Submit
                </Text>
              </Pressable>
            )}
          </View>
        </KeyboardAvoidingView>
      </View>
    </View>
  );
}
