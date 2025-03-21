import {useState} from 'react';
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  TextInput,
} from 'react-native';
import {Text, View} from 'react-native';
import {GET, POST, updateToken} from '../server/helper';
import {baseDomain} from '../../App';

export default function AppLoginPage({
  loggedIn,
}: {
  loggedIn: (e: string) => void;
}) {
  const [userName, setUserName] = useState<string>('');
  const [password, setPassword] = useState<string>('');

  let checkUser = async () => {
    try {
      let res = await POST(baseDomain + `authenticate`, {
        username: userName,
        password: password,
        rememberMe: true,
      });

      let data = await res.json();
      updateToken(data.id_token ?? '');

      let accountReq = await GET(baseDomain + `account`);
      let accountData = await accountReq.json();
      loggedIn(
        accountData.login +
          ` (${accountData.authorities[0].replace('ROLE_', '')})`,
      );
    } catch (e) {
      Alert.alert(
        'Login Failed',
        'Please enter a valid username and password!',
      );
    }
  };

  return (
    <View className="flex-1 flex-col items-center justify-center h-full py-[10%] mt-[-475]">
      <View className="items-center justify-center ">
        <Text className="text-center text-white text-3xl font-bold">
          Welcome, please login:
        </Text>
      </View>

      <View className="h-[160] w-full items-center justify-center ">
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          className="w-full flex-1 items-start justify-center">
          <View className="h-full w-full flex-col items-center justify-center flex-1 mt-2">
            <TextInput
              className="h-[40] pb-1 bg-neutral-700 w-[80%] mt-4 text-white font-bold text-2xl text-center rounded-lg border-neutral-600 border border-solid"
              keyboardType="default"
              placeholder="Enter username..."
              returnKeyType="done"
              onChange={e => setUserName(e.nativeEvent.text)}
            />

            <TextInput
              className="h-[40] pb-1 bg-neutral-700 w-[80%] mt-4 text-white font-bold text-2xl text-center rounded-lg border-neutral-600 border border-solid"
              textContentType="password"
              placeholder="Enter password..."
              returnKeyType="done"
              onChange={e => setPassword(e.nativeEvent.text)}
            />

            <Pressable
              aria-label="submit call sign"
              className="bg-green-500  w-[80%] items-center py-4 rounded-lg ml-2 h-[40] mt-4 "
              onPress={() => checkUser()}>
              <Text className="text-white font-bold text-xl mt-[-5]">
                Submit
              </Text>
            </Pressable>
          </View>
        </KeyboardAvoidingView>
      </View>
    </View>
  );
}
