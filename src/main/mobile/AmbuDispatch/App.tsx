import React from 'react';
import './css/global.css';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  Text,
  View,
  useColorScheme,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';
import AppHomePage from './app/homepage';
import {Dimensions} from 'react-native';

let screenHeight = Dimensions.get('window').height;

const baseDomain = 'http://192.168.0.11:8080/api/';
// onst baseDomain = 'https://team49.dev.bham.team/api/';
//const baseDomain = 'http:/localhost:8080/api/';
export {baseDomain};

function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView className="w-full h-full flex-grow">
        <View
          className=" flex-1 h-auto"
          style={{minHeight: screenHeight * 1.5, flex: 1}}>
          <AppHomePage />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

export default App;
