import {Text, View} from 'react-native';
import MapView, {Marker} from 'react-native-maps';
import Geolocation from 'react-native-geolocation-service';
import {IEmergencyCall} from './assignedCall';

export default function Location({
  location,
  calls,
}: {
  location: Geolocation.GeoPosition | null;
  calls: IEmergencyCall[];
}) {
  return (
    <View className="flex-1 flex-col items-center justify-center h-full  px-2 min-h-[400]  mb-[40] mt-4 relative">
      <View className="items-center justify-center w-full flex-1  border-neutral-600 border border-solid rounded-lg overflow-hidden">
        <MapView
          className="h-[200] w-[200] rounded-lg"
          style={{height: '100%', width: '100%', borderRadius: 6}}
          scrollDuringRotateOrZoomEnabled={true}
          provider={undefined}
          showsUserLocation={true}
          userLocationPriority={'high'}
          showsMyLocationButton={true}
          showsCompass={true}
          showsTraffic={true}
          region={{
            latitude: location?.coords.latitude || 0,
            longitude: location?.coords.longitude || 0,
            latitudeDelta: 0.0922,
            longitudeDelta: 0.0421,
          }}>
          {calls.map((call, index) => (
            <Marker
              key={call.latitude ?? '' + call.longitude ?? ''}
              coordinate={{
                latitude: call.latitude || 0,
                longitude: call.longitude || 0,
              }}
            />
          ))}
        </MapView>
      </View>
    </View>
  );
}
